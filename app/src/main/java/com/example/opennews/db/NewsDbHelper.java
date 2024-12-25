package com.example.opennews.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.opennews.model.hot.HotNewsItem;

import java.util.ArrayList;
import java.util.List;


public class NewsDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "news_database";
    private static final int DATABASE_VERSION = 1;

    public NewsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE hot_news_items (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "newsIndex INTEGER, " +
                "title TEXT, " +
                "url TEXT, " +
                "imageUrl TEXT, " +
                "imageWidth INTEGER, " +
                "imageHeight INTEGER, " +
                "label TEXT, " +
                "labelUrl TEXT, " +
                "hotValue TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS hot_news_items");
        onCreate(db);
    }

    public void insertAll(List<HotNewsItem> items) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            for (HotNewsItem item : items) {
                ContentValues values = new ContentValues();
                values.put("newsIndex", item.getIndex());
                values.put("title", item.getTitle());
                values.put("url", item.getUrl());
                values.put("imageUrl", item.getImageUrl());
                values.put("imageWidth", item.getImageWidth());
                values.put("imageHeight", item.getImageHeight());
                values.put("label", item.getLabel());
                values.put("labelUrl", item.getLabelUrl());
                values.put("hotValue", item.getHotValue());
                //values.put("labelDesc", item.getLabelDesc());

                db.insertWithOnConflict("hot_news_items", null, values, SQLiteDatabase.CONFLICT_REPLACE);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public List<HotNewsItem> getAllHotNewsItems() {
        List<HotNewsItem> newsItems = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("hot_news_items", null, null, null, null, null, "id DESC");

        while (cursor.moveToNext()) {
            HotNewsItem item = new HotNewsItem();
            item.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
            item.setIndex(cursor.getInt(cursor.getColumnIndexOrThrow("newsIndex")));
            item.setTitle(cursor.getString(cursor.getColumnIndexOrThrow("title")));
            item.setUrl(cursor.getString(cursor.getColumnIndexOrThrow("url")));
            item.setImageUrl(cursor.getString(cursor.getColumnIndexOrThrow("imageUrl")));
            item.setImageWidth(cursor.getInt(cursor.getColumnIndexOrThrow("imageWidth")));
            item.setImageHeight(cursor.getInt(cursor.getColumnIndexOrThrow("imageHeight")));
            item.setLabel(cursor.getString(cursor.getColumnIndexOrThrow("label")));
            item.setLabelUrl(cursor.getString(cursor.getColumnIndexOrThrow("labelUrl")));
            item.setHotValue(cursor.getString(cursor.getColumnIndexOrThrow("hotValue")));
            //item.setLabelDesc(cursor.getString(cursor.getColumnIndexOrThrow("labelDesc")));

            newsItems.add(item);
        }
        cursor.close();
        return newsItems;
    }
}