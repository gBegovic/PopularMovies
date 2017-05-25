package com.example.android.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.android.popularmovies.data.MoviesContract.MoviesEntry;

public class MoviesDbHelper extends SQLiteOpenHelper{

    private static final String DATABASE_NAME = "movies.db";
    private static final int DATABASE_VERSION = 3;

    public MoviesDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_MOVIES_TABLE =

                "CREATE TABLE " + MoviesEntry.TABLE_NAME + " (" +

                MoviesEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                MoviesEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +

                MoviesEntry.COLUMN_ORIGINAL_TITLE + " TEXT NOT NULL, " +

                MoviesEntry.COLUMN_EN_TITLE + " TEXT NOT NULL, " +

                MoviesEntry.COLUMN_POSTER + " TEXT NOT NULL, " +

                MoviesEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +

                MoviesEntry.COLUMN_RATING + " TEXT NOT NULL, " +

                MoviesEntry.COLUMN_PLOT + " TEXT NOT NULL, " +

                MoviesEntry.COLUMN_LANGUAGE + " TEXT NOT NULL " +

                ");";

        db.execSQL(SQL_CREATE_MOVIES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + MoviesEntry.TABLE_NAME);
        onCreate(db);
    }
}
