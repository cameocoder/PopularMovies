package com.cameocoder.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.Arrays;
import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieItemHolder> {

    private Activity activity;
    private boolean twoPane;

    List<String> posterUrls = Arrays.asList(
            "http://image.tmdb.org/t/p/w342/D6e8RJf2qUstnfkTslTXNTUAlT.jpg",
            "http://image.tmdb.org/t/p/w342/1n9D32o30XOHMdMWuIT4AaA5ruI.jpg",
            "http://image.tmdb.org/t/p/w342/g23cs30dCMiG4ldaoVNP1ucjs6.jpg",
            "http://image.tmdb.org/t/p/w342/5JU9ytZJyR3zmClGmVm9q4Geqbd.jpg",
            "http://image.tmdb.org/t/p/w342/jjBgi2r5cRt36xF6iNUEhzscEcb.jpg",
            "http://image.tmdb.org/t/p/w342/A7HtCxFe7Ms8H7e7o2zawppbuDT.jpg",
            "http://image.tmdb.org/t/p/w342/l3Lb8UWmqfXY9kr9YhJXvnTvf4I.jpg",
            "http://image.tmdb.org/t/p/w342/iapRFMGKvN9tsjqPlN7MIDTCezG.jpg");

    public MovieAdapter(Activity activity, boolean twoPane) {
        this.activity = activity;
        this.twoPane = twoPane;
    }

    @Override
    public MovieItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(activity);
        View view = inflater.inflate(R.layout.movie_item, parent, false);
        return new MovieItemHolder(view);
    }

    @Override
    public void onBindViewHolder(final MovieItemHolder holder, int position) {
        String posterUrl = posterUrls.get(position);
        Picasso.with(activity).load(posterUrl).into(holder.moviePoster);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (twoPane) {
                    Bundle arguments = new Bundle();
                    arguments.putString(MovieDetailFragment.ARG_ITEM_ID, "foo");
                    MovieDetailFragment fragment = new MovieDetailFragment();
                    fragment.setArguments(arguments);
                    ((AppCompatActivity)activity).getSupportFragmentManager().beginTransaction()
                            .replace(R.id.movieitem_detail_container, fragment)
                            .commit();
                } else {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, MovieDetailActivity.class);
                    intent.putExtra(MovieDetailFragment.ARG_ITEM_ID, "foo");

                    context.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return posterUrls.size();
    }

    public class MovieItemHolder extends RecyclerView.ViewHolder {
        public ImageView moviePoster;

        public MovieItemHolder (View itemView) {
            super(itemView);
            moviePoster = (ImageView) itemView.findViewById(R.id.movie_poster);
        }
    }
}
