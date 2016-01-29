package com.cameocoder.popularmovies;

import com.cameocoder.popularmovies.model.Movies;
import com.cameocoder.popularmovies.model.Reviews;
import com.cameocoder.popularmovies.model.Videos;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

public interface MovieService {
    @GET("/3/discover/movie")
    Call<Movies> discoverMovies(@Query("sort_by") String sort, @Query("api_key") String api_key);

    @GET("/3/movie/{id}/videos")
    Call<Videos> getVideos(@Path("id") int id, @Query("api_key") String api_key);

    @GET("/3/movie/{id}/reviews")
    Call<Reviews> getReviews(@Path("id") int id, @Query("api_key") String api_key);
}
