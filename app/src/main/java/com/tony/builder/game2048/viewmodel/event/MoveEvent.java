package com.tony.builder.game2048.viewmodel.event;

import com.tony.builder.game2048.model.Point;

public class MoveEvent extends MergeEvent{
    public MoveEvent(Point source, Point sink, int sourceValue, int sinkValue) {
        super(source, sink, sourceValue, sinkValue);
    }
}
