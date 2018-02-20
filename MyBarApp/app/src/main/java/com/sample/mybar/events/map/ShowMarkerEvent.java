package com.sample.mybar.events.map;

import com.sample.mybar.utils.common.BarPresentData;

public class ShowMarkerEvent {

    public final BarPresentData bar;

    public ShowMarkerEvent(BarPresentData bar) {
        this.bar = bar;
    }
}
