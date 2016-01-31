package com.cameocoder.popularmovies.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Reviews {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("page")
    @Expose
    private Integer page;
    @SerializedName("results")
    @Expose
    private List<ReviewResult> reviewResults = new ArrayList<ReviewResult>();
    @SerializedName("total_pages")
    @Expose
    private Integer totalPages;
    @SerializedName("total_results")
    @Expose
    private Integer totalResults;

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

    public Reviews withId(Integer id) {
        this.id = id;
        return this;
    }

    /**
     *
     * @return
     * The page
     */
    public Integer getPage() {
        return page;
    }

    /**
     *
     * @param page
     * The page
     */
    public void setPage(Integer page) {
        this.page = page;
    }

    public Reviews withPage(Integer page) {
        this.page = page;
        return this;
    }

    /**
     *
     * @return
     * The reviewResults
     */
    public List<ReviewResult> getReviewResults() {
        return reviewResults;
    }

    /**
     *
     * @param reviewResults
     * The reviewResults
     */
    public void setReviewResults(List<ReviewResult> reviewResults) {
        this.reviewResults = reviewResults;
    }

    public Reviews withResults(List<ReviewResult> reviewResults) {
        this.reviewResults = reviewResults;
        return this;
    }

    /**
     *
     * @return
     * The totalPages
     */
    public Integer getTotalPages() {
        return totalPages;
    }

    /**
     *
     * @param totalPages
     * The total_pages
     */
    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

    public Reviews withTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
        return this;
    }

    /**
     *
     * @return
     * The totalResults
     */
    public Integer getTotalResults() {
        return totalResults;
    }

    /**
     *
     * @param totalResults
     * The total_results
     */
    public void setTotalResults(Integer totalResults) {
        this.totalResults = totalResults;
    }

    public Reviews withTotalResults(Integer totalResults) {
        this.totalResults = totalResults;
        return this;
    }
}