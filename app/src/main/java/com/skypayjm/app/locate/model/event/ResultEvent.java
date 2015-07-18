package com.skypayjm.app.locate.model.event;

import com.skypayjm.app.locate.model.Venue;

import java.util.List;

/**
 * Created by Sky on 17/7/2015.
 */
public class ResultEvent {
    private String searchTerm;

    private List<Venue> results;

    public List<Venue> getResults() {
        return results;
    }

    public void setResults(List<Venue> results) {
        this.results = results;
    }

    public String getSearchTerm() {
        return searchTerm;
    }

    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }

}
