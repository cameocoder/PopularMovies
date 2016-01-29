package com.cameocoder.popularmovies.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Videos {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("results")
    @Expose
    private List<VideoResult> results = new ArrayList<>();

    /**
     *
     * @return
     * The id
     */
    public Integer getId() {
        return id;
    }

    /**
     *
     * @param id
     * The id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    public Videos withId(Integer id) {
        this.id = id;
        return this;
    }

    /**
     *
     * @return
     * The video results
     */
    public List<VideoResult> getResults() {
        return results;
    }

    /**
     *
     * @param results
     * The video results
     */
    public void setResults(List<VideoResult> results) {
        this.results = results;
    }

    public Videos withResults(List<VideoResult> videoResults) {
        this.results = videoResults;
        return this;
    }

}