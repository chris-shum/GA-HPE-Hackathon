package com.example.android.quicktap.BreweryDbApi;

import java.util.List;

/**
 * Created by charlie on 3/12/16.
 */
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
}
