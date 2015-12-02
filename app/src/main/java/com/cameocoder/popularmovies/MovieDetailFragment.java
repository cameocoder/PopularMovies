package com.cameocoder.popularmovies;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cameocoder.popularmovies.model.Movie;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A fragment representing a single MovieItem detail screen.
 * This fragment is either contained in a {@link MainActivity}
 * in two-pane mode (on tablets) or a {@link MovieDetailActivity}
 * on handsets.
 */
public class MovieDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment represents.
     */
    public static final String ARG_ITEM_ID = "item_id";
    public static final String ARG_MOVIE = "movie";

    private static String POSTER_URL = "http://image.tmdb.org/t/p/w342/";

    private Movie movie;

    @Bind(R.id.detail_poster)
    ImageView detailPoster;
    @Bind(R.id.detail_year)
    TextView detailYear;
    @Bind(R.id.detail_rating)
    TextView detailRating;
    @Bind(R.id.detail_overview)
    TextView detailOverview;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MovieDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_MOVIE)) {
            movie = getArguments().getParcelable(ARG_MOVIE);

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(movie.getTitle());
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.movie_detail, container, false);
        ButterKnife.bind(this, rootView);

        final String posterUrl = POSTER_URL + movie.getPosterPath();

        Picasso.with(getActivity()).load(posterUrl).into(detailPoster);

        String releaseYear = getReleaseYear(movie.getReleaseDate());
        detailYear.setText(releaseYear);

        double voteAverage = movie.getVoteAverage();

        String ratingFormat = getContext().getString(R.string.rating_format);
        detailRating.setText(String.format(ratingFormat, voteAverage));

        detailOverview.setText(movie.getOverview());

        return rootView;
    }

    private String getReleaseYear(String date) {
        String year = date.substring(0, date.indexOf('-'));
        return year;
    }
}
