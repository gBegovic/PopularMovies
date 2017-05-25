package com.example.android.popularmovies.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class MoviesContract {

    static final String CONTENT_AUTHORITY = "com.example.android.popularmovies";
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    static final String PATH_MOVIE = "movies";

    public static final class MoviesEntry implements BaseColumns{

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_MOVIE)
                .build();

        public static final String TABLE_NAME = "movies";

        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_ORIGINAL_TITLE = "original_title";
        public static final String COLUMN_EN_TITLE = "en_title";
        public static final String COLUMN_POSTER = "poster";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_RATING = "rating";
        public static final String COLUMN_PLOT = "plot";
        public static final String COLUMN_LANGUAGE = "language";


        public static final String[] MOVIE_DETAILS_PROJECTION = {

                COLUMN_MOVIE_ID,
                COLUMN_ORIGINAL_TITLE,
                COLUMN_EN_TITLE,
                COLUMN_POSTER,
                COLUMN_RELEASE_DATE,
                COLUMN_RATING,
                COLUMN_PLOT,
                COLUMN_LANGUAGE
        };

        public static final int INDEX_MOVIE_ID = 0;
        public static final int INDEX_ORIGINAL_TITLE = 1;
        public static final int INDEX_EN_TITLE = 2;
        public static final int INDEX_POSTER = 3;
        public static final int INDEX_RELEASE_DATE = 4;
        public static final int INDEX_RATING = 5;
        public static final int INDEX_PLOT = 6;
        public static final int INDEX_LANGUAGE = 7;

        public static Uri buildMovieUriWithMovieId(String movieID){

            return Uri.parse(CONTENT_URI+"/"+movieID);
        }
    }
}
