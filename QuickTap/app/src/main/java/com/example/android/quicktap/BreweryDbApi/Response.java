package com.example.android.quicktap.BreweryDbApi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * Created by charlie on 3/12/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Response {
    private List<Beer> data;

    public List<Beer> getBeers() { return data; }

    public void setData(List<Beer> data) {
        this.data = data;
    }
}
