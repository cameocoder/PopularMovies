package com.cameocoder.popularmovies;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private static final String OpenMovieAipKey = BuildConfig.OPEN_MOVIE_DB_API_KEY;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        int gridSize = getResources().getInteger(R.integer.grid_size);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.movies_grid);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), gridSize));
        recyclerView.setAdapter(new MovieAdapter(getActivity()));

        super.onViewCreated(view, savedInstanceState);
    }
}
