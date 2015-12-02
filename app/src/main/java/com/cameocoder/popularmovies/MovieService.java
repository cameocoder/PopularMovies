package com.cameocoder.popularmovies;

import com.cameocoder.popularmovies.model.Movies;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Query;

public interface MovieService {
    //http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key=97d220222ae7ca2618a5fa1ffc5fa03c
    @GET("/3/discover/movie")
    Call<Movies> discoverMovies(@Query("sort_by") String sort, @Query("api_key") String api_key);

}
