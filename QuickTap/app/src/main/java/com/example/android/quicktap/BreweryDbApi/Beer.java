package com.example.android.quicktap.BreweryDbApi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * Created by charlie on 3/12/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Beer {
    private String name, nameDisplay;
    private Style style;
    private List<Brewery> breweries;

    public String getName() { return name; }

    public String getDisplayName() { return nameDisplay; }

    public Style getStyle() { return style; }

    public Brewery getBrewery() {
        if (breweries.size() > 0) {
            return breweries.get(0);
        }
        return null;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNameDisplay(String nameDisplay) {
        this.nameDisplay = nameDisplay;
    }

    public void setStyle(Style style) {
        this.style = style;
    }

    public void setBreweries(List<Brewery> breweries) {
        this.breweries = breweries;
    }
}
