package com.cameocoder.popularmovies;

import com.cameocoder.popularmovies.model.Movies;
import com.cameocoder.popularmovies.model.Reviews;
import com.cameocoder.popularmovies.model.Videos;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RetrofitMovieInterface {
    @GET("/3/discover/movie")
    Call<Movies> discoverMovies(@Query("sort_by") String sort, @Query("page") int page, @Query("api_key") String api_key);

    @GET("/3/movie/{id}/videos")
    Call<Videos> getVideos(@Path("id") int id, @Query("api_key") String api_key);

    @GET("/3/movie/{id}/reviews")
    Call<Reviews> getReviews(@Path("id") int id, @Query("api_key") String api_key);
}
