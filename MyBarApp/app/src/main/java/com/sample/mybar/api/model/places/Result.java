package com.sample.mybar.api.model.places;

import com.google.gson.annotations.SerializedName;

public class Result {
    public Geometry geometry;
    public String name;
    @SerializedName("place_id")
    public String placeId;
}
