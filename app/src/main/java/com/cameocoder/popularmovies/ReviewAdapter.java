package com.cameocoder.popularmovies;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cameocoder.popularmovies.model.ReviewResult;
import com.cameocoder.popularmovies.model.Reviews;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewItemHolder> {

    public final String LOG_TAG = ReviewAdapter.class.getSimpleName();

    private Activity activity;
    private int movieId;

    List<ReviewResult> reviews = new ArrayList<>();

    public ReviewAdapter(Activity activity, int movieId) {
        this.activity = activity;
        this.movieId = movieId;
    }

    public void fetchReviews() {
        RetrofitMovieInterface retrofitMovieInterface = RetrofitMovieService.createMovieService();

        Call<Reviews> reviews = retrofitMovieInterface.getReviews(movieId, BuildConfig.OPEN_MOVIE_DB_API_KEY);
        reviews.enqueue(new Callback<Reviews>() {
            @Override
            public void onResponse(Response<Reviews> response, Retrofit retrofit) {
                if (response != null && response.body() != null) {
                    addReviews(response.body().getReviewResults());
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

    private void addReviews(List<ReviewResult> reviews) {
        this.reviews.addAll(reviews);
        notifyDataSetChanged();
    }

    @Override
    public ReviewItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(activity);
        View view = inflater.inflate(R.layout.review_item, parent, false);
        return new ReviewItemHolder(view);
    }

    @Override
    public void onBindViewHolder(final ReviewItemHolder holder, int position) {
        final ReviewResult review = reviews.get(position);
        holder.reviewAuthor.setText(review.getAuthor());
        holder.reviewContent.setText(review.getContent());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(review.getUrl())));
            }
        });
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    public class ReviewItemHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.review_author)
        TextView reviewAuthor;
        @Bind(R.id.review_content)
        TextView reviewContent;

        public ReviewItemHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
