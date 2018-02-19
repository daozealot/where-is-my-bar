package com.sample.mybar.events;

import com.sample.mybar.api.model.BarPresentData;

import java.util.List;

public class BarsReceivedEvent {
    public List<BarPresentData> barsData;

    public BarsReceivedEvent(List<BarPresentData> bars) {
        this.barsData = bars;
    }
}
