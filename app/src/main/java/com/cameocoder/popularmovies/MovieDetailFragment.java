package com.cameocoder.popularmovies;

import android.app.ActionBar;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.cameocoder.popularmovies.data.MovieContract.FavoriteEntry;
import com.cameocoder.popularmovies.data.MovieContract.MovieEntry;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;

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
    public static final int FAVORITE_LOADER = 3;

    public static final String[] MOVIE_COLUMNS = {
            MovieEntry.COLUMN_ID,
            MovieEntry.COLUMN_TITLE,
            MovieEntry.COLUMN_OVERVIEW,
            MovieEntry.COLUMN_POSTER_PATH,
            MovieEntry.COLUMN_RELEASE_DATE,
            MovieEntry.COLUMN_VOTE_AVERAGE
    };

    public static final String[] FAVORITE_COLUMNS = {
            FavoriteEntry.TABLE_NAME + "." + FavoriteEntry.COLUMN_ID
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
    ImageButton favoriteButton;

    @Bind(R.id.trailer_list)
    RecyclerView trailerList;
    @Bind(R.id.review_list)
    RecyclerView reviewList;

    @BindString(R.string.rating_format)
    String ratingFormat;

    TrailerAdapter trailerAdapter;
    ReviewAdapter reviewAdapter;

    private int movieId;
    private boolean isFavorite;
    private String shareUrl;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MovieDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
        if (getArguments().containsKey(ARG_MOVIE_ID)) {
            movieId = getArguments().getInt(ARG_MOVIE_ID);

            getLoaderManager().initLoader(DETAIL_LOADER, null, this);
            getLoaderManager().initLoader(FAVORITE_LOADER, null, this);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.movie_detail, container, false);
        ButterKnife.bind(this, rootView);

        trailerList.setHasFixedSize(true);
        trailerList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, true));
        trailerAdapter = new TrailerAdapter(getActivity(), movieId);
        trailerList.setAdapter(trailerAdapter);
        trailerAdapter.fetchVideos();

        reviewList.setHasFixedSize(true);
        reviewList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, true));
        reviewAdapter = new ReviewAdapter(getActivity(), movieId);
        reviewList.setAdapter(reviewAdapter);
        reviewAdapter.fetchReviews();

        favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleFavoriteButtonClick();

            }
        });

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_details, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_share) {
            String shareUrl = trailerAdapter.getFirstTrailerUrl();
            if (shareUrl != null) {
                Intent share = new Intent(android.content.Intent.ACTION_SEND);
                share.setType("text/plain");
                share.putExtra(Intent.EXTRA_SUBJECT, getContext().getString(R.string.send_trailer_subject));
                share.putExtra(Intent.EXTRA_TEXT, shareUrl);
                startActivity(Intent.createChooser(share, getContext().getString(R.string.share_chooser_title)));
            }
        }

        return true;
    }

    private void setTitle(String title) {
        Activity activity = this.getActivity();
        ActionBar actionbar = activity.getActionBar();
        if (actionbar != null) {
            actionbar.setTitle(title);
        }
    }

    private void handleFavoriteButtonClick() {
        ContentResolver contentResolver = getContext().getContentResolver();
        if (isFavorite) {
            contentResolver.delete(
                    FavoriteEntry.buildFavoriteWithId(movieId),
                    null,
                    null);
        } else {
            ContentValues favoriteValues = new ContentValues();
            favoriteValues.put(FavoriteEntry.COLUMN_ID, movieId);

            contentResolver.insert(
                    FavoriteEntry.CONTENT_URI,
                    favoriteValues
            );
        }
    }

    private void updateFavoriteButton(boolean favorite) {
        favoriteButton.setSelected(favorite);
        if (favorite) {
            favoriteButton.setContentDescription(getContext().getString(R.string.remove_as_favorite));
        } else {
            favoriteButton.setContentDescription(getContext().getString(R.string.mark_as_favorite));
        }
    }

    private String getReleaseYear(String date) {
        if (!TextUtils.isEmpty(date)) {
            return date.substring(0, date.indexOf('-'));
        } else {
            return getString(R.string.unknown_release_year);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case DETAIL_LOADER: {
                Uri uri = MovieEntry.buildMovieWithId(movieId);
                return new CursorLoader(getActivity(),
                        uri,
                        MOVIE_COLUMNS,
                        null,
                        null,
                        null);
            }
            case FAVORITE_LOADER:
                Uri uri = FavoriteEntry.buildFavoriteWithId(movieId);
                return new CursorLoader(getActivity(),
                        uri,
                        FAVORITE_COLUMNS,
                        null,
                        null,
                        null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case DETAIL_LOADER: {
                if (data != null && data.moveToFirst()) {
                    final int movieId = data.getInt(data.getColumnIndex(MovieEntry.COLUMN_ID));
                    Log.d(LOG_TAG, "Movie id: " + movieId);

                    ;

                    final String posterPath = data.getString(data.getColumnIndex(MovieEntry.COLUMN_POSTER_PATH));
                    final String posterUrl = POSTER_URL + posterPath;

                    Picasso.with(getActivity()).load(posterUrl).placeholder(R.drawable.ic_film_strip_128dp).error(R.drawable.ic_film_strip_128dp).into(detailPoster);

                    String title = data.getString(data.getColumnIndex(MovieEntry.COLUMN_TITLE));
                    setTitle(title);
                    if (detailTitle != null) {
                        detailTitle.setText(title);

                    }
                    String releaseYear = getReleaseYear(data.getString(data.getColumnIndex(MovieEntry.COLUMN_RELEASE_DATE)));
                    detailYear.setText(releaseYear);

                    double voteAverage = data.getDouble(data.getColumnIndex(MovieEntry.COLUMN_VOTE_AVERAGE));

                    detailRating.setText(String.format(ratingFormat, voteAverage));

                    detailOverview.setText(data.getString(data.getColumnIndex(MovieEntry.COLUMN_OVERVIEW)));

                }
                break;
            }

            case FAVORITE_LOADER: {
                isFavorite = false;
                if (data != null && data.moveToFirst()) {
                    final int movieId = data.getInt(data.getColumnIndex(FavoriteEntry.COLUMN_ID));
                    if (movieId == this.movieId) {
                        isFavorite = true;
                    }
                }
                updateFavoriteButton(isFavorite);
                Log.d(LOG_TAG, "Favorite = " + isFavorite);
                break;
            }

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

}
