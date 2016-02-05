package com.cameocoder.popularmovies;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.cameocoder.popularmovies.data.MovieContract;
import com.cameocoder.popularmovies.sync.MovieSyncAdapter;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String BUNDLE_RECYCLER_LAYOUT = "layoutManager";

    private static final String MOVIE_LIST = "movie_list";
    public static final int MOVIE_LOADER = 0;

    public static final String[] MOVIE_COLUMNS = {
            MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_TITLE,
            MovieContract.MovieEntry.COLUMN_POSTER_PATH,
            MovieContract.MovieEntry.COLUMN_ID
    };

    private boolean mTwoPane;

    @Nullable
    @Bind(R.id.movieitem_detail_container)
    View movieDetailContainer;
    @Bind(R.id.movies_grid)
    RecyclerView movieList;

    @BindString(R.string.pref_sort_order_key)
    String prefSortOrderKey;
    @BindString(R.string.pref_value_most_popular)
    String prefValueMostPopular;
    @BindString(R.string.pref_value_highest_rated)
    String prefValueHighestRated;

    SharedPreferences prefs;

    GridLayoutManager layoutManager;
    MovieAdapter adapter;

    public MainActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        MovieSyncAdapter.syncImmediately(getContext());
        prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, view);

        if (movieDetailContainer != null) {
            mTwoPane = true;
        }
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIE_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        int gridSize = getResources().getInteger(R.integer.grid_size);
        movieList.setHasFixedSize(true);
        layoutManager = new GridLayoutManager(getActivity(), gridSize);
        movieList.setLayoutManager(layoutManager);
        adapter = new MovieAdapter(getActivity(), mTwoPane);
        movieList.setAdapter(adapter);

        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_movie_list, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        String sortOrder = prefs.getString(prefSortOrderKey, prefValueMostPopular);

        if (sortOrder.equals(prefValueMostPopular)) {
            menu.findItem(R.id.menuMostPopular).setChecked(true);
            menu.findItem(R.id.menuHighestRated).setChecked(false);
        } else if (sortOrder.equals(prefValueHighestRated)) {
            menu.findItem(R.id.menuMostPopular).setChecked(false);
            menu.findItem(R.id.menuHighestRated).setChecked(true);
        }

        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menuHighestRated) {
            prefs.edit().putString(prefSortOrderKey, prefValueHighestRated).apply();
        } else if (id == R.id.menuMostPopular) {
            prefs.edit().putString(prefSortOrderKey, prefValueMostPopular).apply();
        }

        getActivity().invalidateOptionsMenu();
        return true;

    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        if (savedInstanceState != null) {
//            List<Movie> movies = (List<Movie>)savedInstanceState.get(MOVIE_LIST);
            Parcelable savedRecyclerLayoutState = savedInstanceState.getParcelable(BUNDLE_RECYCLER_LAYOUT);
            movieList.getLayoutManager().onRestoreInstanceState(savedRecyclerLayoutState);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(BUNDLE_RECYCLER_LAYOUT, movieList.getLayoutManager().onSaveInstanceState());
        super.onSaveInstanceState(outState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = MovieContract.MovieEntry.CONTENT_URI;
        String sortOrder = prefs.getString(prefSortOrderKey, prefValueMostPopular);

        String cursorSortOrder = MovieContract.MovieEntry.COLUMN_POPULARITY + " DESC";
        if (sortOrder.equals(prefValueMostPopular)) {
            cursorSortOrder = MovieContract.MovieEntry.COLUMN_POPULARITY + " DESC";
        } else if (sortOrder.equals(prefValueHighestRated)) {
            cursorSortOrder = MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE + " DESC";
        }

        return new CursorLoader(getActivity(),
                uri,
                MOVIE_COLUMNS,
                null,
                null,
                cursorSortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }
}
