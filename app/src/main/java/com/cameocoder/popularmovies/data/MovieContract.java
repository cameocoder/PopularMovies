package com.cameocoder.popularmovies.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class MovieContract {

    public static final String CONTENT_AUTHORITY = "com.cameocoder.popularmovies.app";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_MOVIE = "movies";
    public static final String PATH_FAVORITE = "favorite";
    public static final String PATH_TRAILER = "trailer";
    public static final String PATH_REVIEW = "review";

    /* Inner class that defines the table contents of the movies table */
    public static final class MovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;

        // Table name
        public static final String TABLE_NAME = "movies";


        public static final String COLUMN_ID = "id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_ORIGINAL_TITLE = "original_title";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_POSTER_PATH = "poster_path";
        public static final String COLUMN_BACKDROP_PATH = "backdrop_path";
        public static final String COLUMN_ORIGINAL_LANGUAGE = "original_language";
        public static final String COLUMN_VIDEO = "video";
        public static final String COLUMN_GENRE_IDS = "genre_ids";
        public static final String COLUMN_POPULARITY = "popularity";
        public static final String COLUMN_VOTE_AVERAGE = "vote_average";
        public static final String COLUMN_VOTE_COUNT = "vote_count";
        public static final String COLUMN_ADULT = "adult";


        public static Uri buildMovieWithId(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static String getMovieIDFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }

    /* Inner class that defines the table contents of the favorites table */
    public static final class FavoriteEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVORITE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAVORITE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAVORITE;

        // Table name
        public static final String TABLE_NAME = "favorites";


        public static final String COLUMN_ID = "id";


        public static Uri buildFavoriteWithId(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static String getFavoriteIDFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }

    /* Inner class that defines the table contents of the trailers table */
    public static final class TrailerEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_TRAILER).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TRAILER;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TRAILER;

        // Table name
        public static final String TABLE_NAME = "trailers";


        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_ISO= "iso_639_1";
        public static final String COLUMN_KEY = "key";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_SITE = "site";
        public static final String COLUMN_SIZE = "size";
        public static final String COLUMN_TYPE = "type";


        public static Uri buildTrailerWithMovieId(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static String getTrailerIDFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }

    /* Inner class that defines the table contents of the reviews table */
    public static final class ReviewEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_REVIEW).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REVIEW;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REVIEW;

        // Table name
        public static final String TABLE_NAME = "reviews";


        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_ISO= "author";
        public static final String COLUMN_KEY = "content";
        public static final String COLUMN_NAME = "url";


        public static Uri buildReviewWithMovieId(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static String getReviewIDFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }

}
