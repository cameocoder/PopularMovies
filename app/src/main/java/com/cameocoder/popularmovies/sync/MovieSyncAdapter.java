package com.cameocoder.popularmovies.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;

import com.cameocoder.popularmovies.BuildConfig;
import com.cameocoder.popularmovies.RetrofitMovieInterface;
import com.cameocoder.popularmovies.R;
import com.cameocoder.popularmovies.RetrofitMovieService;
import com.cameocoder.popularmovies.data.MovieContract;
import com.cameocoder.popularmovies.data.MovieContract.ReviewEntry;
import com.cameocoder.popularmovies.data.MovieContract.TrailerEntry;
import com.cameocoder.popularmovies.model.Movie;
import com.cameocoder.popularmovies.model.Movies;
import com.cameocoder.popularmovies.model.ReviewResult;
import com.cameocoder.popularmovies.model.Reviews;
import com.cameocoder.popularmovies.model.VideoResult;
import com.cameocoder.popularmovies.model.Videos;

import java.util.ArrayList;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;


public class MovieSyncAdapter extends AbstractThreadedSyncAdapter {
    public final String LOG_TAG = MovieSyncAdapter.class.getSimpleName();

    private static final String ARG_SYNC_TYPE = "syncType";
    private static final String ARG_MOVIE_ID = "movieId";
    private static final String ARG_PAGE = "page";

    private static final int MOVIES = 0;
    private static final int TRAILERS = 1;
    private static final int REVIEWS = 2;

    // Interval at which to sync movies, in seconds.
    // 60 seconds (1 minute) * 180 = 3 hours
    public static final int SYNC_INTERVAL = 60 * 180;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL/3;


    public MovieSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        final int syncType = extras.getInt(ARG_SYNC_TYPE, MOVIES);
        final int movieId = extras.getInt(ARG_MOVIE_ID);
        final int page = extras.getInt(ARG_PAGE, 1);
        if (syncType == MOVIES) {
            fetchMovies(getContext(), page);
        } else if (syncType == TRAILERS) {
            if (movieId != 0) {
                fetchTrailers(movieId);
            }
        } else if (syncType == REVIEWS) {
            if (movieId != 0) {
                fetchReviews(movieId);
            }
        }

    }

    public void fetchMovies(Context context, int page) {
        RetrofitMovieInterface retrofitMovieInterface = RetrofitMovieService.createMovieService();

        String sortOrder = getSortOrder(context);

        Call<Movies> movies = retrofitMovieInterface.discoverMovies(sortOrder, page, BuildConfig.OPEN_MOVIE_DB_API_KEY);
        movies.enqueue(new Callback<Movies>() {
            @Override
            public void onResponse(Response<Movies> response, Retrofit retrofit) {
                if (response != null && response.body() != null) {
                    addMovies(response.body().getMovies());
                }
            }

            @Override
            public void onFailure(Throwable t) {
                // Ignore for now
                if (t.getMessage() != null) {
                    Log.e(LOG_TAG, "Unable to parse response: " + t.getMessage());
                }
            }
        });
    }

    private void addMovies(List<Movie> movies) {
        ArrayList<ContentValues> contentValues = new ArrayList<>();
        for (int i = 0; i < movies.size(); i++) {
            Movie movie = movies.get(i);
            ContentValues contentValue = new ContentValues();
            contentValue.put(MovieContract.MovieEntry.COLUMN_TITLE, movie.getTitle());
            contentValue.put(MovieContract.MovieEntry.COLUMN_ID, movie.getId());
            contentValue.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, movie.getOverview());
            contentValue.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, movie.getPosterPath());
            contentValue.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, movie.getReleaseDate());
            contentValue.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, movie.getVoteAverage());
            contentValue.put(MovieContract.MovieEntry.COLUMN_VOTE_COUNT, movie.getVoteCount());
            contentValue.put(MovieContract.MovieEntry.COLUMN_POPULARITY, movie.getPopularity());
            contentValues.add(contentValue);
        }

        ContentValues[] contentValuesArray = new ContentValues[contentValues.size()];
        contentValues.toArray(contentValuesArray);

        int itemsAdded = getContext().getContentResolver().bulkInsert(MovieContract.MovieEntry.CONTENT_URI, contentValuesArray);
        Log.d(LOG_TAG, itemsAdded + "/" + contentValuesArray.length + " movies added to database");
    }

    public void fetchTrailers(final int movieId) {
        RetrofitMovieInterface retrofitMovieInterface = RetrofitMovieService.createMovieService();

        Call<Videos> videos = retrofitMovieInterface.getVideos(movieId, BuildConfig.OPEN_MOVIE_DB_API_KEY);
        videos.enqueue(new Callback<Videos>() {
            @Override
            public void onResponse(Response<Videos> response, Retrofit retrofit) {
                if (response != null && response.body() != null) {
                    addVideos(movieId, response.body().getResults());
                }
            }

            @Override
            public void onFailure(Throwable t) {
                // Ignore for now
                if (t.getMessage() != null) {
                    Log.e(LOG_TAG, "Unable to parse video response: " + t.getMessage());
                }
            }
        });
    }

    private void addVideos(int movieId, List<VideoResult> videos) {
        ArrayList<ContentValues> contentValues = new ArrayList<>();
        for (int i = 0; i < videos.size(); i++) {
            VideoResult video = videos.get(i);
            ContentValues contentValue = new ContentValues();
            contentValue.put(TrailerEntry.COLUMN_MOVIE_ID, movieId);
            contentValue.put(TrailerEntry.COLUMN_ID, video.getId());
            contentValue.put(TrailerEntry.COLUMN_ISO, video.getIso6391());
            contentValue.put(TrailerEntry.COLUMN_KEY, video.getKey());
            contentValue.put(TrailerEntry.COLUMN_NAME, video.getName());
            contentValue.put(TrailerEntry.COLUMN_SITE, video.getSite());
            contentValue.put(TrailerEntry.COLUMN_SIZE, video.getSize());
            contentValue.put(TrailerEntry.COLUMN_TYPE, video.getType());
            contentValues.add(contentValue);
        }

        ContentValues[] contentValuesArray = new ContentValues[contentValues.size()];
        contentValues.toArray(contentValuesArray);

        int itemsAdded = getContext().getContentResolver().bulkInsert(TrailerEntry.CONTENT_URI, contentValuesArray);
        Log.d(LOG_TAG, itemsAdded + "/" + contentValuesArray.length + " videos added to database");
    }

    public void fetchReviews(final int movieId) {
        RetrofitMovieInterface retrofitMovieInterface = RetrofitMovieService.createMovieService();

        Call<Reviews> reviews = retrofitMovieInterface.getReviews(movieId, BuildConfig.OPEN_MOVIE_DB_API_KEY);
        reviews.enqueue(new Callback<Reviews>() {
            @Override
            public void onResponse(Response<Reviews> response, Retrofit retrofit) {
                if (response != null && response.body() != null) {
                    addReviews(movieId, response.body().getReviewResults());
                }
            }

            @Override
            public void onFailure(Throwable t) {
                // Ignore for now
                if (t.getMessage() != null) {
                    Log.e(LOG_TAG, "Unable to parse review response: " + t.getMessage());
                }
            }
        });
    }

    private void addReviews(int movieId, List<ReviewResult> reviews) {
        ArrayList<ContentValues> contentValues = new ArrayList<>();
        for (int i = 0; i < reviews.size(); i++) {
            ReviewResult review = reviews.get(i);
            ContentValues contentValue = new ContentValues();
            contentValue.put(ReviewEntry.COLUMN_MOVIE_ID, movieId);
            contentValue.put(ReviewEntry.COLUMN_ID, review.getId());
            contentValue.put(ReviewEntry.COLUMN_AUTHOR, review.getAuthor());
            contentValue.put(ReviewEntry.COLUMN_CONTENT, review.getContent());
            contentValue.put(ReviewEntry.COLUMN_URL, review.getUrl());
            contentValues.add(contentValue);
        }

        ContentValues[] contentValuesArray = new ContentValues[contentValues.size()];
        contentValues.toArray(contentValuesArray);

        int itemsAdded = getContext().getContentResolver().bulkInsert(ReviewEntry.CONTENT_URI, contentValuesArray);
        Log.d(LOG_TAG, itemsAdded + "/" + contentValuesArray.length + " reviews added to database");
    }

    @NonNull
    private String getSortOrder(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_sort_order_key),
                context.getString(R.string.pref_value_most_popular));
    }

    /**
     * Helper method to have the sync adapter sync movies immediately
     *
     * @param context The context used to access the account service
     */
    public static void syncMovies(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        bundle.putInt(ARG_SYNC_TYPE, MOVIES);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    /**
     * Helper method to have the sync adapter sync trailers immediately
     *
     * @param context The context used to access the account service
     */
    public static void syncTrailers(Context context, int movieId) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        bundle.putInt(ARG_SYNC_TYPE, TRAILERS);
        bundle.putInt(ARG_MOVIE_ID, movieId);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    /**
     * Helper method to have the sync adapter sync trailers immediately
     *
     * @param context The context used to access the account service
     */
    public static void syncReviews(Context context, int movieId) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        bundle.putInt(ARG_SYNC_TYPE, REVIEWS);
        bundle.putInt(ARG_MOVIE_ID, movieId);
        Account account = getSyncAccount(context);
        ContentResolver.requestSync(account,
                context.getString(R.string.content_authority), bundle);
    }

    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if (accountManager.getPassword(newAccount) == null) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */

            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        /*
         * Since we've created an account
         */
        MovieSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        /*
         * Finally, let's do a sync to get things started
         */
        syncMovies(context);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }
}
