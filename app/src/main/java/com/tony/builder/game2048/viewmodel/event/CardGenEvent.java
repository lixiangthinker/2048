package com.tony.builder.game2048.viewmodel.event;

import com.tony.builder.game2048.model.Point;

import androidx.annotation.NonNull;

public class CardGenEvent {
    public Point position;
    public int value;
    public CardGenEvent(Point position, int value) {
        this.position = new Point(position.x, position.y);
        this.value = value;
    }

    @NonNull
    @Override
    public String toString() {
        String result = "["+position.x+","+position.y+"] -> "+value+"->"+value;
        return result;
    }
}
