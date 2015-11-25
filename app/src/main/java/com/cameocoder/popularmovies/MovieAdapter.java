package com.cameocoder.popularmovies;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.Arrays;
import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieItemHolder> {

    Activity activity;

    List<String> posterUrls = Arrays.asList(
            "http://image.tmdb.org/t/p/w342/D6e8RJf2qUstnfkTslTXNTUAlT.jpg",
            "http://image.tmdb.org/t/p/w342/1n9D32o30XOHMdMWuIT4AaA5ruI.jpg",
            "http://image.tmdb.org/t/p/w342/g23cs30dCMiG4ldaoVNP1ucjs6.jpg",
            "http://image.tmdb.org/t/p/w342/5JU9ytZJyR3zmClGmVm9q4Geqbd.jpg",
            "http://image.tmdb.org/t/p/w342/jjBgi2r5cRt36xF6iNUEhzscEcb.jpg",
            "http://image.tmdb.org/t/p/w342/A7HtCxFe7Ms8H7e7o2zawppbuDT.jpg",
            "http://image.tmdb.org/t/p/w342/l3Lb8UWmqfXY9kr9YhJXvnTvf4I.jpg",
            "http://image.tmdb.org/t/p/w342/iapRFMGKvN9tsjqPlN7MIDTCezG.jpg");

    public MovieAdapter(Activity activity) {
        this.activity = activity;
    }

    @Override
    public MovieItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(activity);
        View view = inflater.inflate(R.layout.movie_item, parent, false);
        return new MovieItemHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieItemHolder holder, int position) {
        String posterUrl = posterUrls.get(position);
        Picasso.with(activity).load(posterUrl).into(holder.moviePoster);
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
