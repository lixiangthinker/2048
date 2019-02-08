package com.tony.builder.game2048.model;

import androidx.annotation.NonNull;

public class Point {
    public int x;
    public int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @NonNull
    @Override
    public String toString() {
        return "["+x+","+y+"]";
    }
}
