package com.sample.mybar.events;

import com.sample.mybar.api.model.distance.Row;

import java.util.List;

public class DistanceReceivedEvent {
    public List<Row> barDistanceData;

    public DistanceReceivedEvent(List<Row> barDistanceData) {
        this.barDistanceData = barDistanceData;
    }
}
