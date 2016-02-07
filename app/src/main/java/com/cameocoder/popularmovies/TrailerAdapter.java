package com.cameocoder.popularmovies;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cameocoder.popularmovies.model.VideoResult;
import com.cameocoder.popularmovies.model.Videos;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerItemHolder> {

    public final String LOG_TAG = TrailerAdapter.class.getSimpleName();

    private Activity activity;
    private int movieId;

    List<VideoResult> trailers = new ArrayList<>();

    public TrailerAdapter(Activity activity, int movieId) {
        this.activity = activity;
        this.movieId = movieId;
    }

    public void fetchVideos() {
        RetrofitMovieInterface retrofitMovieInterface = RetrofitMovieService.createMovieService();

        Call<Videos> videos = retrofitMovieInterface.getVideos(movieId, BuildConfig.OPEN_MOVIE_DB_API_KEY);
        videos.enqueue(new Callback<Videos>() {
            @Override
            public void onResponse(Response<Videos> response, Retrofit retrofit) {
                if (response != null && response.body() != null) {
                    addTrailers(response.body().getResults());
                }
            }

            @Override
            public void onFailure(Throwable t) {
                // Ignore for now
                if (t.getMessage() != null) {
                    Log.e(LOG_TAG, "Unable to parse video response: " + t.getMessage());
                }
            }
        });
    }

    public String getFirstTrailerUrl() {
        if (!trailers.isEmpty()) {
            return getTrailerUriString(trailers.get(0));
        }
        return null;
    }

    private void addTrailers(List<VideoResult> trailers) {
        this.trailers.addAll(trailers);
        notifyDataSetChanged();
    }

    @Override
    public TrailerItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(activity);
        View view = inflater.inflate(R.layout.trailer_item, parent, false);
        return new TrailerItemHolder(view);
    }

    @Override
    public void onBindViewHolder(final TrailerItemHolder holder, int position) {
        final VideoResult currentTrailer = trailers.get(position);
        holder.trailerTitle.setText(currentTrailer.getName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getTrailerUriString(currentTrailer))));
            }
        });
    }

    @NonNull
    private String getTrailerUriString(VideoResult currentTrailer) {
        return activity.getString(R.string.youtube_trailer_base_url) + currentTrailer.getKey();
    }

    @Override
    public int getItemCount() {
        return trailers.size();
    }


    public class TrailerItemHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.trailer_title)
        TextView trailerTitle;

        public TrailerItemHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
