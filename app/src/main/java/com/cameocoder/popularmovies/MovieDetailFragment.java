package com.cameocoder.popularmovies;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.cameocoder.popularmovies.data.MovieContract;
import com.cameocoder.popularmovies.model.ReviewResult;
import com.cameocoder.popularmovies.model.Reviews;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * A fragment representing a single MovieItem detail screen.
 * This fragment is either contained in a {@link MainActivity}
 * in two-pane mode (on tablets) or a {@link MovieDetailActivity}
 * on handsets.
 */
public class MovieDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public final String LOG_TAG = MovieDetailFragment.class.getSimpleName();

    /**
     * The fragment argument representing the item ID that this fragment represents.
     */
    public static final String ARG_MOVIE_ID = "movieId";

    private static String POSTER_URL = "http://image.tmdb.org/t/p/w342/";

    public static final int DETAIL_LOADER = 2;

    public static final String[] MOVIE_COLUMNS = {
            MovieContract.MovieEntry.COLUMN_ID,
            MovieContract.MovieEntry.COLUMN_TITLE,
            MovieContract.MovieEntry.COLUMN_OVERVIEW,
            MovieContract.MovieEntry.COLUMN_POSTER_PATH,
            MovieContract.MovieEntry.COLUMN_RELEASE_DATE,
            MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE
    };

    @Nullable
    @Bind(R.id.detail_title)
    TextView detailTitle;
    @Bind(R.id.detail_poster)
    ImageView detailPoster;
    @Bind(R.id.detail_year)
    TextView detailYear;
    @Bind(R.id.detail_rating)
    TextView detailRating;
    @Bind(R.id.detail_overview)
    TextView detailOverview;
    @Bind(R.id.button_favorite)
    Button favoriteButton;

    @Bind(R.id.trailer_list)
    RecyclerView trailerList;
    @Bind(R.id.review_list)
    RecyclerView reviewList;

    @BindString(R.string.rating_format)
    String ratingFormat;

    private int movieId;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MovieDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_MOVIE_ID)) {
            movieId = getArguments().getInt(ARG_MOVIE_ID);

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
//                appBarLayout.setTitle(movie.getTitle());
            }
            getLoaderManager().initLoader(DETAIL_LOADER, null, this);
            fetchReviews();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.movie_detail, container, false);
        ButterKnife.bind(this, rootView);

        trailerList.setHasFixedSize(true);
        trailerList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, true));
        TrailerAdapter trailerAdapter = new TrailerAdapter(getActivity(), movieId);
        trailerList.setAdapter(trailerAdapter);
        trailerAdapter.fetchVideos();

        reviewList.setHasFixedSize(true);
        reviewList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, true));
        ReviewAdapter reviewAdapter = new ReviewAdapter(getActivity(), movieId);
        reviewList.setAdapter(reviewAdapter);
        reviewAdapter.fetchReviews();

        return rootView;
    }

    private String getReleaseYear(String date) {
        if (!TextUtils.isEmpty(date)) {
            return date.substring(0, date.indexOf('-'));
        } else {
            return getString(R.string.unknown_release_year);
        }
    }

    public void fetchReviews() {
        MovieService movieService = RetrofitMovieService.createMovieService();

        Call<Reviews> reviews = movieService.getReviews(movieId, BuildConfig.OPEN_MOVIE_DB_API_KEY);
        reviews.enqueue(new Callback<Reviews>() {
            @Override
            public void onResponse(Response<Reviews> response, Retrofit retrofit) {
                if (response != null && response.body() != null) {
                    List<ReviewResult> results = response.body().getReviewResults();
                    Log.d(LOG_TAG, "Movie Response: " + results.size());
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

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = MovieContract.MovieEntry.buildMovieWithId(movieId);

        return new CursorLoader(getActivity(),
                uri,
                MOVIE_COLUMNS,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {
            final int movieId = data.getInt(data.getColumnIndex(MovieContract.MovieEntry.COLUMN_ID));
            Log.d(LOG_TAG, "Movie id: " + movieId);

            ;

            final String posterPath = data.getString(data.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER_PATH));
            final String posterUrl = POSTER_URL + posterPath;

            Picasso.with(getActivity()).load(posterUrl).placeholder(R.drawable.ic_film_strip_128dp).error(R.drawable.ic_film_strip_128dp).into(detailPoster);

            if (detailTitle != null) {
                detailTitle.setText(data.getString(data.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE)));
            }
            String releaseYear = getReleaseYear(data.getString(data.getColumnIndex(MovieContract.MovieEntry.COLUMN_RELEASE_DATE)));
            detailYear.setText(releaseYear);

            double voteAverage = data.getDouble(data.getColumnIndex(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE));

            detailRating.setText(String.format(ratingFormat, voteAverage));

            detailOverview.setText(data.getString(data.getColumnIndex(MovieContract.MovieEntry.COLUMN_OVERVIEW)));

            favoriteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
