package com.sample.mybar.events;

import com.sample.mybar.utils.common.BarPresentData;

import java.util.List;

public class BarsReceivedEvent {
    public List<BarPresentData> barsData;

    public BarsReceivedEvent(List<BarPresentData> bars) {
        this.barsData = bars;
    }
}
