package com.tony.builder.game2048.viewmodel.event;

import com.tony.builder.game2048.model.Point;

import androidx.annotation.NonNull;

public class MergeEvent {
    public Point source;
    public Point sink;
    public int sourceValue;
    public int sinkValue;
    public MergeEvent(Point source, Point sink, int sourceValue, int sinkValue) {
        this.source = source;
        this.sink = sink;
        this.sourceValue = sourceValue;
        this.sinkValue = sinkValue;
    }

    @NonNull
    @Override
    public String toString() {
        String result = "["+source.x+","+source.y+"] -> ["+sink.x+","+sink.y+"], "+sourceValue+"->"+sinkValue;
        return result;
    }
}
