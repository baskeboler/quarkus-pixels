package com.gil.service;

import com.gil.model.PixelMap;
import io.quarkus.runtime.StartupEvent;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.mutiny.core.eventbus.EventBus;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


@Slf4j
@ApplicationScoped
public class PixelMapService {
    public record PixelMapData(UUID id, int width, int height, List<String> pixels) implements Serializable {
        public PixelMapData(UUID id, PixelMap pixelMap) {
            this(id, pixelMap.getWidth(), pixelMap.getHeight(),
                    Arrays.stream(pixelMap.toArray()).boxed().map(Integer::toHexString).toList());
        }
    }

    public enum PixelMapMessageType {
        PIXEL_MAP_FETCH,
        PIXEL_MAP_UPDATE,
        PIXEL_MAP_DELETE
    }

    public record PixelMapMessage<T>(PixelMapMessageType type, T payload) implements Serializable {
    }

    public record PixelMapUpdate(UUID id, int x, int y, int color, String cssColor) {
        public PixelMapUpdate(UUID id, int x, int y, int color) {
            this(id, x, y, color, intToCssString(color));
        }
    }

    private final Map<UUID, PixelMap> pixelMaps = new ConcurrentHashMap<>();
    private final int defaultHeight = 10;
    private final int defaultWidth = 10;

    @Inject
    EventBus eventBus;

    public void init(@Observes StartupEvent event) {
        // Initialization logic if needed
        log.info("PixelMapService initialized");
    }


    public PixelMapData createPixelMap() {
        return createPixelMap(defaultWidth, defaultHeight);
    }

    public PixelMapData createPixelMap(int width, int height) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Width and height must be positive");
        }
        UUID id = UUID.randomUUID();
        PixelMap pixelMap = new PixelMap(width, height);
        pixelMaps.put(id, pixelMap);
        log.info("Created PixelMap with ID: {}", id);
        return new PixelMapData(id, pixelMap);
    }

    public Optional<PixelMapData> getPixelMap(UUID id) {
        PixelMap pixelMap = pixelMaps.get(id);
        if (pixelMap == null) {
            return Optional.empty();
        }
        return Optional.of(new PixelMapData(id, pixelMap));
    }

    public Optional<PixelMapData> updatePixelMap(UUID id, int x, int y, int color) {
        return Optional.ofNullable(
                pixelMaps.computeIfPresent(id, (uuid, pixelMap) -> {
                            pixelMap.setPixel(x, y, color);
                            // print color in hex format
                            String hexColor = String.format("#%08X", color);
                            log.info("Updated PixelMap with ID: {} at ({}, {}) to color {}", id, x, y, hexColor);

                            var pixelMapMessage = new PixelMapMessage<PixelMapUpdate>(
                                    PixelMapMessageType.PIXEL_MAP_UPDATE,
                                    new PixelMapUpdate(id, x, y, color));

                            eventBus.publish("pixelmap.update",
                                    pixelMapMessage, new DeliveryOptions());
                            return pixelMap;
                        }
                )).map(pixelMap -> new PixelMapData(id, pixelMap));
    }

    public Set<UUID> getAvailablePixelMapIds() {
        return pixelMaps.keySet();
    }

    public static  String intToCssString(int color) {
        return String.format("#%08X", color);
    }

}
