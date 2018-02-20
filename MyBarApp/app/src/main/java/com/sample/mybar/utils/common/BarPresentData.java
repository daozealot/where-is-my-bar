package com.sample.mybar.utils.common;

import com.sample.mybar.api.model.places.Location;

public class BarPresentData {
    public final String name;
    public String distance;
    public final Location location;
    public final String placeId;

    public BarPresentData(String name, Location location, String placeId) {
        this.name = name;
        this.location = location;
        this.placeId = placeId;
    }
}
