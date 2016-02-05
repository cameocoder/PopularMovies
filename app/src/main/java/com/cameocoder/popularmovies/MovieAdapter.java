package com.cameocoder.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.cameocoder.popularmovies.data.MovieContract;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieItemHolder> implements SharedPreferences.OnSharedPreferenceChangeListener {

    public final String LOG_TAG = MovieAdapter.class.getSimpleName();
    private static final String POSTER_URL = "http://image.tmdb.org/t/p/w342/";

    private Activity activity;
    Cursor cursor;
    private boolean twoPane;

    public MovieAdapter(Activity activity, boolean twoPane) {
        this.activity = activity;
        this.twoPane = twoPane;
        PreferenceManager.getDefaultSharedPreferences(activity).registerOnSharedPreferenceChangeListener(this);
    }

    public void swapCursor(Cursor cursor) {
        this.cursor = cursor;
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
        cursor.moveToPosition(position);
        final int movieId = cursor.getInt(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_ID));
        final String posterPath = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER_PATH));
        final String posterUrl = POSTER_URL + posterPath;

        Picasso.with(activity).load(posterUrl).placeholder(R.drawable.ic_film_strip_128dp).error(R.drawable.ic_film_strip_128dp).into(holder.moviePoster);

        final Bundle arguments = new Bundle();
        arguments.putInt(MovieDetailFragment.ARG_MOVIE_ID, movieId);

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
        if (cursor != null) {
            return cursor.getCount();
        } else {
            return 0;
        }
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
