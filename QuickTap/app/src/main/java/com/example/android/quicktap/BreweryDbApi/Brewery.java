package com.example.android.quicktap.BreweryDbApi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by charlie on 3/12/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Brewery {
    private String name, nameShortDisplay;

    public String getFullName() { return name; }

    public String getShortName() { return nameShortDisplay; }

    public void setName(String name) {
        this.name = name;
    }

    public void setNameShortDisplay(String nameShortDisplay) {
        this.nameShortDisplay = nameShortDisplay;
    }
}
