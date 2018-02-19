package com.sample.mybar.events;

import com.sample.mybar.api.model.places.Result;

import java.util.List;

public class BarsReceivedEvent {
    public List<Result> bars;

    public BarsReceivedEvent(List<Result> bars) {
        this.bars = bars;
    }
}
