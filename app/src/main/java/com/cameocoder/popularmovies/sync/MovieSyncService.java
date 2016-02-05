package com.cameocoder.popularmovies.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class MovieSyncService extends Service {

    public final String LOG_TAG = MovieSyncService.class.getSimpleName();

    private static final Object sSyncAdapterLock = new Object();
    private static MovieSyncAdapter movieSyncAdapter = null;

    public MovieSyncService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(LOG_TAG, "onCreate");
        synchronized (sSyncAdapterLock) {
            if (movieSyncAdapter == null) {
                movieSyncAdapter = new MovieSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return movieSyncAdapter.getSyncAdapterBinder();
    }
}
