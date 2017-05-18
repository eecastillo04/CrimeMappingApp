package com.example.crimemappingapp.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static DatabaseHelper INSTANCE;

    public enum DATABASE_TABLE {
        ADMIN(TABLE_ADMIN, "create table "
                + TABLE_ADMIN + "("
                + COLUMN_ID + " integer primary key autoincrement, "
                + COLUMN_USERNAME + " text not null unique, "
                + COLUMN_PASSWORD + " text not null);"),
//        CRIME_TYPE(TABLE_CRIME_TYPE, "create table "
//                + TABLE_CRIME_TYPE + "("
//                + COLUMN_ID + " integer primary key autoincrement, "
//                + COLUMN_CRIME_NAME + " text not null);"),
        CRIME(TABLE_CRIME, "create table "
                + TABLE_CRIME + "("
                + COLUMN_ID + " integer primary key autoincrement, "
                + COLUMN_LOCATION + " text not null, "
                + COLUMN_LATITUDE + " text not null, "
                + COLUMN_LONGITUDE + " text not null, "
                + COLUMN_DATE + " long not null, "
                + COLUMN_CRIME_TYPE_ID + " text not null);");
//                + COLUMN_CRIME_TYPE_ID + " text, "
//                + " FOREIGN KEY (" + COLUMN_CRIME_TYPE_ID + ")"
//                + " REFERENCES " + CRIME_TYPE.getTableName()
//                + "(" + COLUMN_ID + "));");

        private final String tableName;
        private final String createTableSQL;

        DATABASE_TABLE(String tableName, String createTableSQL) {
            this.tableName = tableName;
            this.createTableSQL = createTableSQL;
        }

        public String getTableName() {
            return this.tableName;
        }

        public String getCreateTableSQL() {
            return this.createTableSQL;
        }
    }

    public static final String COLUMN_ID = "_id";

    public static final String TABLE_ADMIN = "admin";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_PASSWORD = "password";

    public static final String TABLE_CRIME_TYPE = "crime_type";
    public static final String COLUMN_CRIME_NAME = "crime_name";

    public static final String TABLE_CRIME = "crime";
    public static final String COLUMN_CRIME_TYPE_ID = "crime_type_id";
    public static final String COLUMN_LOCATION = "location";
    public static final String COLUMN_DATE = "crime_date";
    public static final String COLUMN_LATITUDE = "latitude";
    public static final String COLUMN_LONGITUDE = "longitude";

    public static final String DATABASE_NAME = "crime_mapping.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        for(DATABASE_TABLE dbTable: DATABASE_TABLE.values()) {
            db.execSQL(dbTable.getCreateTableSQL());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(this.getClass().getName(), String.format("Upgrading database from version %s to %s, which will destroy all data.", oldVersion, newVersion));

        dropAllTables(db);

        onCreate(db);
    }

    private void dropAllTables(SQLiteDatabase db) {
        for(DATABASE_TABLE dbTable: DATABASE_TABLE.values()) {
            db.execSQL("DROP TABLE IF EXISTS " + dbTable.getTableName());
        }
    }

//    public static void insertCrimeType(String crimeName) {
//        ContentValues values = new ContentValues();
//        values.put(COLUMN_CRIME_NAME, crimeName);
//
//        String selectString = buildSelectStatement(DATABASE_TABLE.CRIME_TYPE.getTableName(), COLUMN_CRIME_NAME);
//
//        Cursor cursor = getDatabase().rawQuery(selectString, new String[] {crimeName});
//
//        boolean crimeTypeExists = cursor.moveToFirst();
//        if(!crimeTypeExists) {
//            getDatabase(true).insert(DATABASE_TABLE.CRIME_TYPE.getTableName(), null, values);
//        }
//
//        closeDatabase(cursor);
//    }

    public static void insertAdmin(String username, String password) {
        String hashPassword = HashText.sha1(password);
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_PASSWORD, hashPassword);

        String selectString = buildSelectStatement(DATABASE_TABLE.ADMIN.getTableName(), COLUMN_USERNAME);

        Cursor cursor = getDatabase().rawQuery(selectString, new String[] {username});

        boolean adminExists = cursor.moveToFirst();
        if(!adminExists) {
            getDatabase(true).insert(DATABASE_TABLE.ADMIN.getTableName(), null, values);
        }

        closeDatabase(cursor);
    }

    public static boolean isValidAdminCredentials(String username, String password) {
        String hashPassword = HashText.sha1(password);

        String selectString = buildSelectStatement(DATABASE_TABLE.ADMIN.getTableName(), COLUMN_USERNAME, COLUMN_PASSWORD);

        Cursor cursor = getDatabase().rawQuery(selectString, new String[] {username, hashPassword});

        boolean adminExists = cursor.moveToFirst();
        closeDatabase(cursor);
        return adminExists;
    }

    public static void insertCrimeList(List<Crime> crimeList) {
        SQLiteDatabase database = getDatabase(true);

        for(Crime crime: crimeList) {
            LatLng latLng = crime.getLatLng();
            ContentValues values = new ContentValues();
            values.put(COLUMN_LOCATION, crime.getLocation());
            values.put(COLUMN_LATITUDE, latLng.latitude);
            values.put(COLUMN_LONGITUDE, latLng.longitude);
            values.put(COLUMN_DATE, crime.getDateMillis());
            values.put(COLUMN_CRIME_TYPE_ID, crime.getCrimeType().getId());

            database.insert(DATABASE_TABLE.CRIME.getTableName(), null, values);
        }

        closeDatabase();
    }

    public static List<Crime> retrieveAllCrimes(int crimeTypeId, long from, long to) {
        List<Crime> crimeList = new ArrayList<>();
        String selectStatement = buildSelectStatement(DATABASE_TABLE.CRIME.getTableName(), COLUMN_CRIME_TYPE_ID);
        selectStatement += " and " + COLUMN_DATE + " >= ?";
        selectStatement += " and " + COLUMN_DATE + " <= ?";

        Cursor cursor = getDatabase().rawQuery(selectStatement, new String[] {String.valueOf(crimeTypeId), String.valueOf(from), String.valueOf(to)});
        if (cursor.moveToFirst()) {
            crimeList.add(buildCrime(cursor));
            while(cursor.moveToNext()) {
                crimeList.add(buildCrime(cursor));
            }
        }
        return crimeList;
    }

    private static Crime buildCrime(Cursor cursor) {
        Crime crime = new Crime();
        crime.setId(cursor.getInt(0));
        crime.setLocation(cursor.getString(1));
        crime.setLatLng(new LatLng(Double.valueOf(cursor.getString(2)), Double.valueOf(cursor.getString(3))));
        crime.setDateMillis(cursor.getLong(4));
        crime.setCrimeType(CrimeTypes.getCrimeType(cursor.getInt(5)));
        return crime;
    }

    public static void deleteCrime(int crimeId) {
        getDatabase(true).delete(DATABASE_TABLE.CRIME.getTableName(), COLUMN_ID + " = " + crimeId, null);
        closeDatabase();
    }

//    public static HashMap<Integer, String> retrieveAllCrimeTypes() {
//        HashMap<Integer, String> crimeTypeMap = new HashMap<>();
//
//        String selectString = buildSelectStatement(DATABASE_TABLE.CRIME_TYPE.getTableName(), new String[]{});
//
//        Cursor cursor = getDatabase().rawQuery(selectString, null);
//        if (cursor.moveToFirst()) {
//            crimeTypeMap.put(cursor.getInt(0), cursor.getString(1));
//            while(cursor.moveToNext()) {
//                crimeTypeMap.put(cursor.getInt(0), cursor.getString(1));
//            }
//        }
//        return crimeTypeMap;
//    }

    private static String buildSelectStatement(String tableName, String ... columnNames) {
        String selectString = "SELECT * FROM " + tableName;

        if(columnNames != null && columnNames.length != 0) {
            selectString +=  " WHERE";
            boolean isFirstColumn = true;
            for(String columnName: columnNames) {
                if(!isFirstColumn) {
                    selectString += " and";
                }
                isFirstColumn = false;
                selectString += " " + columnName + " = ?";
            }
        }

        return selectString;
    }

    private static void closeDatabase() {
        closeDatabase(null);
    }

    private static void closeDatabase(Cursor cursor) {
        if(cursor != null) {
            cursor.close();
        }
        INSTANCE.close();
    }

    /**
     * @return Returns a readable database
     */
    private static SQLiteDatabase getDatabase() {
        return getDatabase(false);
    }

    private static SQLiteDatabase getDatabase(boolean isWritable) {
        return isWritable ? INSTANCE.getWritableDatabase():  INSTANCE.getReadableDatabase();
    }

    public static DatabaseHelper createInstance(Context context) {
        if(INSTANCE == null) {
            INSTANCE = new DatabaseHelper(context);
        }

        return INSTANCE;
    }


}
