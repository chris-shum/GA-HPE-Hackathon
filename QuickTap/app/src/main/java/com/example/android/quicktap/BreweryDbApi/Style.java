package com.example.android.quicktap.BreweryDbApi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by charlie on 3/12/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Style {
    private String name, shortName;

    public String getFullName() { return name; }

    public String getShortName() { return shortName; }

    public void setName(String name) {
        this.name = name;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }
}
