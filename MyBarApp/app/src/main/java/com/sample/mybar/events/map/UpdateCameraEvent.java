package com.sample.mybar.events.map;

import com.google.android.gms.maps.model.LatLng;

public class UpdateCameraEvent {
    public LatLng latLng;

    public UpdateCameraEvent(LatLng latLng) {
        this.latLng = latLng;
    }
}
