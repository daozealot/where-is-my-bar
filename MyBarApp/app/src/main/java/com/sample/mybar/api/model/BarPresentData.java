package com.sample.mybar.api.model;

import com.sample.mybar.api.model.places.Location;

public class BarPresentData {
    public String name;
    public String distance;
    public Location location;
    public String placeId;

    public BarPresentData(String name, Location location, String placeId) {
        this.name = name;
        this.location = location;
        this.placeId = placeId;
    }
}
