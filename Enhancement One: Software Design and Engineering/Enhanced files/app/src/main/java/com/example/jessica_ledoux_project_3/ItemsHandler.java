package com.example.jessica_ledoux_project_3;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class ItemsHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "ItemsData.DB";
    private static final String TABLE_NAME = "ItemsTable";

    private static final String COL_ID = "id";
    private static final String COL_EMAIL = "email";
    private static final String COL_DESC = "description";
    private static final String COL_QTY = "quantity";
    private static final String COL_UNIT = "unit";

    private static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
            COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COL_EMAIL + " TEXT, " +
            COL_DESC + " TEXT, " +
            COL_QTY + " TEXT, " +
            COL_UNIT + " TEXT);";

    public ItemsHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
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

    // Creating item and all its variables to database
    public void createItem(Item item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_EMAIL, item.getUserEmail());
        values.put(COL_DESC, item.getDescription());
        values.put(COL_QTY, item.getQuantity());
        values.put(COL_UNIT, item.getCategory());
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    // Reading item variables from database
    public Item readItem(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME,
                new String[]{COL_ID, COL_EMAIL, COL_DESC, COL_QTY, COL_UNIT},
                COL_ID + "=?",
                new String[]{String.valueOf(id)},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            Item item = new Item(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4)
            );
            cursor.close();
            return item;
        }

        return null;
    }

    // Updating the item variables in the database
    public int updateItem(Item item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_EMAIL, item.getUserEmail());
        values.put(COL_DESC, item.getDescription());
        values.put(COL_QTY, item.getQuantity());
        values.put(COL_UNIT, item.getCategory());

        return db.update(TABLE_NAME, values, COL_ID + "=?", new String[]{String.valueOf(item.getItemId())});
    }

    // Deleting items variables from the database
    // Delete one
    public void deleteItem(Item item) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, COL_ID + "=?", new String[]{String.valueOf(item.getItemId())});
        db.close();
    }

    // Deleting all item variables from the database
    public void deleteAllItems() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, null, null);
        db.close();
    }

    /**
     * Global Database Operations
     */

    // List to get all items from Array
    public List<Item> getAllItems() {
        List<Item> itemList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);

        if (cursor.moveToFirst()) {
            do {
                Item item = new Item(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4)
                );
                itemList.add(item);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return new ArrayList<>(itemList);

    }


    // Getting total item count from the database
    public int getItemsCount() {
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

