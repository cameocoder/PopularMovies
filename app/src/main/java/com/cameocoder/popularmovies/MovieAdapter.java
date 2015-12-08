package com.cameocoder.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.cameocoder.popularmovies.model.Movie;
import com.cameocoder.popularmovies.model.Movies;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieItemHolder> implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static String POSTER_URL = "http://image.tmdb.org/t/p/w342/";

    private static final String OpenMovieAipKey = BuildConfig.OPEN_MOVIE_DB_API_KEY;

    private Activity activity;
    private boolean twoPane;

    List<Movie> movies = new ArrayList<>();

    public MovieAdapter(Activity activity, boolean twoPane) {
        this.activity = activity;
        this.twoPane = twoPane;
        PreferenceManager.getDefaultSharedPreferences(activity).registerOnSharedPreferenceChangeListener(this);

        fetchMovies(activity);
    }

    private void fetchMovies(Activity activity) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api.themoviedb.org")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        MovieService movieService = retrofit.create(MovieService.class);

        String sortOrder = getSortOrder(activity);

        Call<Movies> movies = movieService.discoverMovies(sortOrder, OpenMovieAipKey);
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
            }
        });
    }

    private void addMovies(List<Movie> movies) {
        this.movies.addAll(movies);
        notifyDataSetChanged();
    }

    @Override
    public MovieItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(activity);
        View view = inflater.inflate(R.layout.movie_item, parent, false);
        return new MovieItemHolder(view);
    }

    @Override
    public void onBindViewHolder(final MovieItemHolder holder, int position) {
        final Movie currentMovie = movies.get(position);
        final String posterUrl = POSTER_URL + currentMovie.getPosterPath();
        final String movieId = currentMovie.getId().toString();
        Picasso.with(activity).load(posterUrl).placeholder(R.drawable.ic_film_strip_128dp).error(R.drawable.ic_film_strip_128dp).into(holder.moviePoster);

        final Movie movie = movies.get(position);
        final Bundle arguments = new Bundle();
        arguments.putString(MovieDetailFragment.ARG_ITEM_ID, movieId);
        arguments.putParcelable(MovieDetailFragment.ARG_MOVIE, movie);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (twoPane) {
                    MovieDetailFragment fragment = new MovieDetailFragment();
                    fragment.setArguments(arguments);
                    ((AppCompatActivity) activity).getSupportFragmentManager().beginTransaction()
                            .replace(R.id.movieitem_detail_container, fragment)
                            .commit();
                } else {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, MovieDetailActivity.class);
                    intent.putExtras(arguments);

                    context.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    @NonNull
    private String getSortOrder(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_sort_order_key),
                context.getString(R.string.pref_value_most_popular));
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(activity.getString(R.string.pref_sort_order_key))) {
            movies.clear();
            fetchMovies(activity);
            notifyDataSetChanged();
        }
    }


    public class MovieItemHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.movie_poster)
        ImageView moviePoster;

        public MovieItemHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
