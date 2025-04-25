package com.gil.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PixelMapTest {

    @Test
    void setPixel() {
        PixelMap pixelMap = new PixelMap(5, 5);
        pixelMap.setPixel(2, 2, 0xFF0000FF); // Set a pixel to blue
        assertEquals(0xFF0000FF, pixelMap.getPixel(2, 2)); // Check if the pixel is set correctly

        // Test out of bounds
        assertThrows(IllegalArgumentException.class, () -> {
            pixelMap.setPixel(-1, -1, 0xFF0000FF);
        });
    }

    @Test
    void getPixel() {
        PixelMap pixelMap = new PixelMap(5, 5);
        pixelMap.setPixel(2, 2, 0xFF0000FF); // Set a pixel to blue
        assertEquals(0xFF0000FF, pixelMap.getPixel(2, 2)); // Check if the pixel is set correctly

        // Test default color
        assertEquals(0xFFFFFFFF, pixelMap.getPixel(0, 0)); // Default color is white
    }

    @Test
    void clear() {
        PixelMap pixelMap = new PixelMap(5, 5);
        pixelMap.setPixel(2, 2, 0xFF0000FF); // Set a pixel to blue
        assertEquals(0xFF0000FF, pixelMap.getPixel(2, 2)); // Check if the pixel is set correctly

        pixelMap.clear(); // Clear the pixel map
        assertEquals(0xFFFFFFFF, pixelMap.getPixel(2, 2)); // Check if the pixel is reset to default color
    }

    @Test
    void fill() {
        PixelMap pixelMap = new PixelMap(5, 5);
        pixelMap.fill(0xFF0000FF); // Fill with blue color

        // Check if all pixels are filled with the correct color
        for (int y = 0; y < 5; y++) {
            for (int x = 0; x < 5; x++) {
                assertEquals(0xFF0000FF, pixelMap.getPixel(x, y));
            }
        }
    }

    @Test
    void toArray() {
        PixelMap pixelMap = new PixelMap(5, 5);
        pixelMap.fill(0xFF0000FF); // Fill with blue color

        int[] array = pixelMap.toArray();

        // Check if the array is filled with the correct color
        for (int y = 0; y < 5; y++) {
            for (int x = 0; x < 5; x++) {
                assertEquals(0xFF0000FF, array[y*5+x]);
            }
        }
    }
    @Test
    void testSetPixelOutOfBounds() {
        PixelMap pixelMap = new PixelMap(5, 5);
        assertThrows(IllegalArgumentException.class, () -> {
            pixelMap.setPixel(5, 5, 0xFF0000FF); // Out of bounds
        });
        assertThrows(IllegalArgumentException.class, () -> {
            pixelMap.setPixel(-1, -1, 0xFF0000FF); // Out of bounds
        });
    }
}
