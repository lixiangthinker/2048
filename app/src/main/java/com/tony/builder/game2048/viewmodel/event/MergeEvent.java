package com.tony.builder.game2048.viewmodel.event;

import androidx.annotation.NonNull;

import com.tony.kotlin.libboardview.Point;

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
        return "["+source.getX()+","+source.getY()+"] -> ["+sink.getX()+","+sink.getY()+"], "+sourceValue+"->"+sinkValue;
    }
}
