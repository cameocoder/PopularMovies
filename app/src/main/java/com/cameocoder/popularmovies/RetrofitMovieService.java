package com.cameocoder.popularmovies;


import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitMovieService {
    private static final String API_URL = "http://api.themoviedb.org";

    public static RetrofitMovieInterface createMovieService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(RetrofitMovieInterface.class);
    }

}
