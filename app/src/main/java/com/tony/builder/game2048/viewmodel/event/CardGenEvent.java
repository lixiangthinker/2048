package com.tony.builder.game2048.viewmodel.event;

import androidx.annotation.NonNull;

import com.tony.kotlin.libboardview.Point;

public class CardGenEvent {
    public Point position;
    public int value;
    public CardGenEvent(Point position, int value) {
        this.position = new Point(position.getX(), position.getY());
        this.value = value;
    }

    @NonNull
    @Override
    public String toString() {
        return "["+position.getX()+","+position.getY()+"] -> "+value+"->"+value;
    }
}
