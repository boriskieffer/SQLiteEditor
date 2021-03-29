package com.bki.sqliteedit;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SQLiteDatabaseHelper extends SQLiteOpenHelper {
    String dbPath = "";

    public SQLiteDatabaseHelper(@Nullable Context context, @Nullable String name, @Nullable android.database.sqlite.SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(android.database.sqlite.SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(android.database.sqlite.SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public Cursor executeRawSQL(String sql) {
        android.database.sqlite.SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(sql, null);
    }

    public int fetchTableCount () {
        Cursor res= executeRawSQL("SELECT name FROM sqlite_master WHERE type='table';");
        return res.getCount();
    }

    public List<String> fetchAllTables () {
        List<String> tables = new ArrayList<String>();
        Cursor res= executeRawSQL("SELECT name FROM sqlite_master WHERE type='table';");
        res.moveToFirst();
        while (!res.isAfterLast()) {
            Log.d("current", String.valueOf(res.getPosition()));
            tables.add(res.getString(0));
            res.moveToNext();
        }
        return tables;
    }
}
