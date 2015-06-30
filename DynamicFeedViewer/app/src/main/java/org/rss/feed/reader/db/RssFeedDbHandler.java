package org.rss.feed.reader.db;

/**
 * Created by Admin on 28-06-2015.
 */

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;
import android.content.ContentValues;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RssFeedDbHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "rssFeedDb.db";
    private static final String TABLE_FEEDS = "feeds";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_IMAGE = "imageData";
    public static final String COLUMN_URL = "url";

    public  RssFeedDbHandler(Context context, String name,
                            SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_FEED_TABLE = "CREATE TABLE " +
                TABLE_FEEDS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY," + COLUMN_NAME
                + " TEXT," + COLUMN_DATE + " TEXT," + COLUMN_URL
                + " TEXT," + COLUMN_IMAGE + " TEXT" + ")";
        db.execSQL(CREATE_FEED_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion,
                          int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FEEDS);
        onCreate(db);
    }

    public void addProduct(RssMainPageEntity entity) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, entity.getTitle());
        values.put(COLUMN_DATE, new Date().getDate());
        values.put(COLUMN_URL, entity.getUrl());
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_FEEDS, null, values);
        db.close();
    }

    public List<Entity> getAllFeeds() {
        String query = "Select * FROM " + TABLE_FEEDS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        List<Entity> entities = new ArrayList<>();
        try {
            if (cursor.moveToFirst()) {
                cursor.moveToFirst();
                do {
                    RssMainPageEntity entity = new RssMainPageEntity();
                    entity.setId(cursor.getInt(0));
                    entity.setTitle((cursor.getString(1)));
                    entity.setDate(cursor.getString(2));
                    entity.setUrl((cursor.getString(3)));
                    entity.setImage(cursor.getString(4));
                    entities.add(entity);
                } while (cursor.moveToNext());

            }
        }finally{
            cursor.close();
            db.close();
        }

        return entities;
    }

    public boolean deleteProduct(int feedName) {

        boolean result = false;

        String query = "Select * FROM " + TABLE_FEEDS + " WHERE " + COLUMN_ID + " =  \"" + feedName + "\"";

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        RssMainPageEntity entity = new RssMainPageEntity();

        if (cursor.moveToFirst()) {
            entity.setId(Integer.parseInt(cursor.getString(0)));
            db.delete(TABLE_FEEDS, COLUMN_ID + " = ?",
                    new String[]{String.valueOf(entity.getId())});
            cursor.close();
            result = true;
        }
        db.close();
        return result;
    }
}