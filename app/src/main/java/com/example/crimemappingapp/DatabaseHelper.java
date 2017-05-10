package com.example.crimemappingapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by klanezurbano on 30/04/2017.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private static DatabaseHelper INSTANCE;

    public enum DATABASE_TABLE {
        ADMIN(TABLE_ADMIN, "create table "
                + TABLE_ADMIN + "("
                + COLUMN_ID + " integer primary key autoincrement, "
                + COLUMN_USERNAME + " text not null unique, "
                + COLUMN_PASSWORD + " text not null)"),
        CRIME_TYPE(TABLE_CRIME_TYPE, "create table "
                + TABLE_CRIME_TYPE + "("
                + COLUMN_ID + " integer primary key autoincrement, "
                + COLUMN_CRIME_NAME + " text not null)"),
        CRIME(TABLE_CRIME, "create table "
                + TABLE_CRIME + "("
                + COLUMN_ID + " integer primary key autoincrement, "
                + COLUMN_LOCATION + " text not null, "
                + COLUMN_DATE + " text not null, "
                + COLUMN_CRIME_TYPE_ID + " text, "
                + " FOREIGN KEY (" + COLUMN_CRIME_TYPE_ID
                + " REFERENCES " + TABLE_CRIME
                + "(" + CRIME_TYPE + "))");

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

    private static final String DATABASE_NAME = "crime_mapping.db";
    private static final int DATABASE_VERSION = 2;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createDbSQL = "";

        for(DATABASE_TABLE dbTable: DATABASE_TABLE.values()) {
            createDbSQL += dbTable.getCreateTableSQL() + " ";
        }

        createDbSQL += ";";
        db.execSQL(createDbSQL);
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

    public void insertAdmin(String username, String password) {
        String hashPassword = HashText.sha1(password);
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_USERNAME, username);
        values.put(DatabaseHelper.COLUMN_PASSWORD, hashPassword);

        try {
            getWritableDatabase().insert(DatabaseHelper.TABLE_ADMIN, null, values);
        } catch(SQLiteConstraintException e) {
            Log.i("Unable to add new admin credentials", e.getMessage());
        }
        close();
    }

    public boolean isValidAdminCredentials(String username, String password) {
        String hashPassword = HashText.sha1(password);

        String selectString = String.format("SELECT * FROM %s WHERE %s =? and %s=?", DATABASE_TABLE.ADMIN.getTableName(), COLUMN_USERNAME, COLUMN_PASSWORD);

        Cursor cursor = getReadableDatabase().rawQuery(selectString, new String[] {username, hashPassword});

        boolean adminExists = cursor.moveToFirst();
        cursor.close();
        close();
        return adminExists;
    }

    public static DatabaseHelper getInstance(Context context) {
        if(INSTANCE == null) {
            INSTANCE = new DatabaseHelper(context);
        }

        return INSTANCE;
    }


}
