package com.example.crimemappingapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
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
                + COLUMN_PASSWORD + " text not null)");

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

        getWritableDatabase().insert(DatabaseHelper.TABLE_ADMIN, null, values);
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
