package com.gil.model;

import lombok.Value;

import java.io.Serializable;

@Value
public class PixelPosition implements Comparable<PixelPosition>, Serializable {
    int x;
    int y;


    @Override
    public int compareTo(PixelPosition o) {
        if (this.y == o.y) {
            return Integer.compare(this.x, o.x);
        }
        return Integer.compare(this.y, o.y);
    }
}
