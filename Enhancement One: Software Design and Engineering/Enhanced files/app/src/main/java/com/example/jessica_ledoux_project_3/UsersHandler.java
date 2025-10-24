package com.example.jessica_ledoux_project_3;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class UsersHandler extends SQLiteOpenHelper {

    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "UsersData.DB";
    public static final String TABLE_NAME = "UsersTable";

    public static final String COL_ID = "id";
    public static final String COL_NAME = "name";
    public static final String COL_PHONE = "phone_number";
    public static final String COL_EMAIL = "email";
    public static final String COL_PASSWORD = "password";

    private static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
            COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
            COL_NAME + " TEXT, " +
            COL_PHONE + " TEXT, " +
            COL_EMAIL + " TEXT, " +
            COL_PASSWORD + " TEXT);";

    public UsersHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    /**
     * Database CRUD (Create, Read, Update, Delete) Operations
     */

    // Adding user and all its variables to database
    public void createUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_NAME, user.getName());
        values.put(COL_PHONE, user.getPhone());
        values.put(COL_EMAIL, user.getEmail());
        values.put(COL_PASSWORD, user.getPassword());
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    // Reading user and variables from database
    public User readUser(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME,
                new String[]{COL_ID, COL_NAME, COL_PHONE, COL_EMAIL, COL_PASSWORD},
                COL_ID + "=?",
                new String[]{String.valueOf(id)},
                null, null, null);

        User user = null;
        if (cursor != null && cursor.moveToFirst()) {
            user = new User(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4)
            );
            cursor.close();
        }

        return user;
    }

    // Updating user and variables in database
    public int updateUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_NAME, user.getName());
        values.put(COL_PHONE, user.getPhone());
        values.put(COL_EMAIL, user.getEmail());
        values.put(COL_PASSWORD, user.getPassword());

        return db.update(TABLE_NAME, values, COL_ID + "=?", new String[]{String.valueOf(user.getId())});
    }

    // Deleting user from the database
    // Delete one
    public void deleteUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, COL_ID + "=?", new String[]{String.valueOf(user.getId())});
        db.close();
    }

    /**
     * Global Database Operations
     */

    // List to get all users from Array
    public List<User> getAllUsers() {
        List<User> userList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Select All Query
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);

        // Adding to the list by a do loop
        if (cursor.moveToFirst()) {
            do {
                User user = new User(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4)
                );
                userList.add(user);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return userList;
    }

    // Deleting all users variables from the database
    public void deleteAllUsers() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, null, null);
        db.close();
    }

    // Getting total users from the database
    public int getUsersCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_NAME, null);
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }
    }

