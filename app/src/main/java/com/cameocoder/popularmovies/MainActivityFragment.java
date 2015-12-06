package com.cameocoder.popularmovies;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private boolean mTwoPane;

    @Bind(R.id.movies_grid)
    RecyclerView recyclerView;

    @BindString(R.string.pref_sort_order_key)
    String prefSortOrderKey;
    @BindString(R.string.pref_value_most_popular)
    String prefValueMostPopular;
    @BindString(R.string.pref_value_highest_rated)
    String prefValueHighestRated;

    public MainActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, view);

        if (view.findViewById(R.id.movieitem_detail_container) != null) {
            mTwoPane = true;
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        int gridSize = getResources().getInteger(R.integer.grid_size);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), gridSize));
        recyclerView.setAdapter(new MovieAdapter(getActivity(), mTwoPane));

        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_movie_list, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
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

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        if (id == R.id.menuHighestRated) {
            prefs.edit().putString(prefSortOrderKey, prefValueHighestRated).apply();
        } else if (id == R.id.menuMostPopular) {
            prefs.edit().putString(prefSortOrderKey, prefValueMostPopular).apply();
        }

        getActivity().invalidateOptionsMenu();
        return true;

    }

}
