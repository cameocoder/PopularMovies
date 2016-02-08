package com.cameocoder.popularmovies.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.cameocoder.popularmovies.data.MovieContract.FavoriteEntry;
import com.cameocoder.popularmovies.data.MovieContract.MovieEntry;
import com.cameocoder.popularmovies.data.MovieContract.TrailerEntry;

public class MovieProvider extends ContentProvider {

    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MovieDbHelper mOpenHelper;

    static final int MOVIES = 100;
    static final int MOVIE_WITH_ID = 101;
    static final int FAVORITES = 200;
    static final int FAVORITE_WITH_ID = 201;
    static final int TRAILERS = 300;
    static final int TRAILERS_WITH_MOVIE_ID = 301;
    static final int REVIEWS = 400;
    static final int REVIEWS_WITH_MOVIE_ID = 401;

    private static final SQLiteQueryBuilder movieByIdQueryBuilder;
    private static final SQLiteQueryBuilder favoriteByIdQueryBuilder;
    private static final SQLiteQueryBuilder trailerByMovieIdQueryBuilder;

    static {
        movieByIdQueryBuilder = new SQLiteQueryBuilder();
        movieByIdQueryBuilder.setTables(MovieEntry.TABLE_NAME);
        favoriteByIdQueryBuilder = new SQLiteQueryBuilder();
        favoriteByIdQueryBuilder.setTables(
                FavoriteEntry.TABLE_NAME + " INNER JOIN " +
                        MovieEntry.TABLE_NAME +
                        " ON " + FavoriteEntry.TABLE_NAME +
                        "." + FavoriteEntry.COLUMN_ID +
                        " = " + MovieEntry.TABLE_NAME +
                        "." + MovieEntry.COLUMN_ID);

        trailerByMovieIdQueryBuilder = new SQLiteQueryBuilder();
        trailerByMovieIdQueryBuilder.setTables(TrailerEntry.TABLE_NAME);
    }

    private static final String movieIdSelection = MovieEntry.TABLE_NAME + "."
            + MovieEntry.COLUMN_ID + " = ? ";

    private static final String favortiesIdSelection = FavoriteEntry.TABLE_NAME + "."
            + FavoriteEntry.COLUMN_ID + " = ? ";

    private static final String trailerByMovieIdSelection = TrailerEntry.TABLE_NAME + "."
            + TrailerEntry.COLUMN_MOVIE_ID + " = ? ";

    static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;

        // For each type of URI you want to add, create a corresponding code.
        matcher.addURI(authority, MovieContract.PATH_MOVIE, MOVIES);
        matcher.addURI(authority, MovieContract.PATH_MOVIE + "/#", MOVIE_WITH_ID);
        matcher.addURI(authority, MovieContract.PATH_FAVORITE, FAVORITES);
        matcher.addURI(authority, MovieContract.PATH_FAVORITE + "/#", FAVORITE_WITH_ID);
        matcher.addURI(authority, MovieContract.PATH_TRAILER, TRAILERS);
        matcher.addURI(authority, MovieContract.PATH_TRAILER + "/#", TRAILERS_WITH_MOVIE_ID);
        matcher.addURI(authority, MovieContract.PATH_REVIEW, REVIEWS);
        matcher.addURI(authority, MovieContract.PATH_REVIEW + "/#", REVIEWS);
        return matcher;
    }

    private Cursor getMovieByID(Uri uri, String[] projection, String sortOrder) {
        String movie_id = MovieEntry.getMovieIDFromUri(uri);
        return movieByIdQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                movieIdSelection,
                new String[] { movie_id },
                null,
                null,
                sortOrder
        );
    }

    private Cursor getFavoriteByID(Uri uri, String[] projection, String sortOrder) {
        String movie_id = FavoriteEntry.getFavoriteIDFromUri(uri);
        return favoriteByIdQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                favortiesIdSelection,
                new String[] { movie_id },
                null,
                null,
                sortOrder
        );
    }

    private Cursor getFavorites(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return favoriteByIdQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    private Cursor getTrailerByMovieID(Uri uri, String[] projection, String sortOrder) {
        String movie_id = TrailerEntry.getTrailerIDFromUri(uri);
        return trailerByMovieIdQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                trailerByMovieIdSelection,
                new String[] { movie_id },
                null,
                null,
                sortOrder
        );
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new MovieDbHelper(getContext());
        return false;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case MOVIES:
                return MovieEntry.CONTENT_ITEM_TYPE;
            case MOVIE_WITH_ID:
                return MovieEntry.CONTENT_ITEM_TYPE;
            case FAVORITES:
                return FavoriteEntry.CONTENT_ITEM_TYPE;
            case FAVORITE_WITH_ID:
                return FavoriteEntry.CONTENT_ITEM_TYPE;
            case TRAILERS:
                return TrailerEntry.CONTENT_ITEM_TYPE;
            case TRAILERS_WITH_MOVIE_ID:
                return TrailerEntry.CONTENT_ITEM_TYPE;
            case REVIEWS:
                return TrailerEntry.CONTENT_ITEM_TYPE;
            case REVIEWS_WITH_MOVIE_ID:
                return TrailerEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        // Here's the switch statement that, given a URI, will determine what kind of request it is,
        // and query the database accordingly.
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();

        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            case MOVIES: {
                retCursor = db.query(
                        MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case MOVIE_WITH_ID: {
                retCursor = getMovieByID(uri, projection, sortOrder);
                break;
            }
            case FAVORITES: {
                retCursor = getFavorites(uri, projection, selection, selectionArgs, sortOrder);

                break;
            }
            case FAVORITE_WITH_ID: {
                retCursor = getFavoriteByID(uri, projection, sortOrder);
                break;
            }
            case TRAILERS: {
                retCursor = db.query(
                        TrailerEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case TRAILERS_WITH_MOVIE_ID: {
                retCursor = getTrailerByMovieID(uri, projection, sortOrder);
                break;
            }

            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;

    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        long insertedId;

        switch (match) {
            case MOVIES: {
                insertedId = db.insert(MovieEntry.TABLE_NAME, null, values);
                if (insertedId > 0)
                    returnUri = MovieEntry.buildMovieWithId(insertedId);
                else
                    throw new SQLException("Failed to insert row into " + uri);
                break;
            }
            case FAVORITES: {
                insertedId = db.insert(FavoriteEntry.TABLE_NAME, null, values);
                if (insertedId > 0)
                    returnUri = FavoriteEntry.buildFavoriteWithId(insertedId);
                else
                    throw new SQLException("Failed to insert row into " + uri);
                break;
            }

            case TRAILERS: {
                insertedId = db.insert(TrailerEntry.TABLE_NAME, null, values);
                if (insertedId > 0)
                    returnUri = TrailerEntry.buildTrailerWithMovieId(insertedId);
                else
                    throw new SQLException("Failed to insert row into " + uri);
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if (null == selection) selection = "1";
        switch (match) {
            case MOVIES: {
                rowsDeleted = db.delete(
                        MovieEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            case FAVORITES: {
                rowsDeleted = db.delete(
                        FavoriteEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            case FAVORITE_WITH_ID: {
                String movie_id = FavoriteEntry.getFavoriteIDFromUri(uri);
                rowsDeleted = db.delete(
                        FavoriteEntry.TABLE_NAME, favortiesIdSelection,
                        new String[] { movie_id });
                break;
            }
            case TRAILERS: {
                rowsDeleted = db.delete(
                        TrailerEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;
        switch (match) {
            case MOVIES: {
                rowsUpdated = db.update(MovieEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            }
            case FAVORITES: {
                rowsUpdated = db.update(FavoriteEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            }
            case TRAILERS: {
                rowsUpdated = db.update(TrailerEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;

    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MOVIES:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MovieEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;

            case TRAILERS:
                db.beginTransaction();
                int trailerCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(TrailerEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            trailerCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return trailerCount;


            default:
                return super.bulkInsert(uri, values);

        }
    }

    // This is a method specifically to assist the testing framework in running smoothly.
    // http://developer.android.com/reference/android/content/ContentProvider.html#shutdown()
    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }
}
