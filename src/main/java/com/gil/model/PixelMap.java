package com.gil.model;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Collections;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Data
@RegisterForReflection
public class PixelMap {
    private final int width;
    private final int height;
    private volatile int defaultColor = 0xFFFFFFFF; // Default color is white

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private final NavigableMap<PixelPosition, Integer> pixelMap;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    public PixelMap(int width, int height) {
        this.width = width;
        this.height = height;
        this.pixelMap = Collections.synchronizedNavigableMap(new TreeMap<>());
    }

    public void setPixel(int x, int y, int color) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            throw new IllegalArgumentException("Pixel position out of bounds");
        }
        lock.writeLock().lock();
        try {
            pixelMap.put(new PixelPosition(x, y), color);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public int getPixel(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            throw new IllegalArgumentException("Pixel position out of bounds");
        }
        lock.readLock().lock();
        try {
            return pixelMap.getOrDefault(new PixelPosition(x, y), defaultColor);
        } finally {
            lock.readLock().unlock();
        }
    }

    public void clear() {
        lock.writeLock().lock();
        try {
            pixelMap.clear();
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void fill(int color) {
        lock.writeLock().lock();
        try {
            pixelMap.clear();
            defaultColor = color;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public int[] toArray() {
        int[] array = new int[height * width];
        lock.readLock().lock();
        try {
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    array[y * width + x] = getPixel(x, y);
                }
            }
            return array;
        } finally {
            lock.readLock().unlock();
        }
    }
}
