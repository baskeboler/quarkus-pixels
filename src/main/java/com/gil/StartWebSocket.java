package com.gil;

import com.gil.service.PixelMapService;
import com.gil.service.PixelMapService.PixelMapData;
import com.gil.service.PixelMapService.PixelMapMessage;
import io.quarkus.runtime.StartupEvent;
import io.quarkus.vertx.ConsumeEvent;
import io.quarkus.websockets.next.*;
import io.smallrye.common.annotation.NonBlocking;
import io.smallrye.common.annotation.RunOnVirtualThread;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.core.eventbus.EventBus;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.UUID;
import java.util.concurrent.Executors;

@ApplicationScoped
@WebSocket(path = "/start-websocket/{pixelMapId}")
@Slf4j
public class StartWebSocket implements Serializable {

    private final PixelMapService pixelMapService;
    private final WebSocketConnection connection;
    private final OpenConnections openClientConnections;
    private final EventBus eventBus;

    @Inject
    public StartWebSocket(PixelMapService pixelMapService, WebSocketConnection connection, OpenConnections openClientConnections, EventBus eventBus) {
        this.pixelMapService = pixelMapService;
        this.connection = connection;
        this.openClientConnections = openClientConnections;
        this.eventBus = eventBus;
    }

    public void init(@Observes StartupEvent event) {
        // Initialization logic if needed
        log.info("StartWebSocket initializing");

    }

    @NonBlocking
    @RunOnVirtualThread
    @ConsumeEvent("pixelmap.update")
    public <T> void handlePixelMapUpdate(PixelMapMessage<T> pixelMapUpdate) {
        switch (pixelMapUpdate.type()) {
            case PIXEL_MAP_FETCH -> log.info("Received PIXEL_MAP_FETCH event: {}", pixelMapUpdate.payload());
            case PIXEL_MAP_UPDATE -> log.info("Received PIXEL_MAP_UPDATE event: {}", pixelMapUpdate.payload());
            case PIXEL_MAP_DELETE -> log.info("Received PIXEL_MAP_DELETE event: {}", pixelMapUpdate.payload());
        }
        ;
        log.info("Received pixel map update: {}", pixelMapUpdate);

        openClientConnections
                .forEach(webSocketConnection -> {
                            if (webSocketConnection.isOpen()) {
                                log.info("Sending pixel map update to connection: {}", webSocketConnection.id());
                                webSocketConnection.sendTextAndAwait(pixelMapUpdate);
                            } else {
                                log.warn("WebSocket connection {} is not open, skipping send", webSocketConnection.id());
                            }
                        }
                );
    }

    @NonBlocking
    @OnOpen
    @RunOnVirtualThread
    public Uni<PixelMapData> onOpen(@PathParam("pixelMapId") String pixelMapId) {
        try {
            UUID mapId = UUID.fromString(pixelMapId);
            if (!connection.isOpen()) {
                log.error("Connection is not open for pixel map ID: {}", pixelMapId);
                return Uni.createFrom().failure(new IllegalStateException("Connection is not open"));
            }

            log.info("Opening WebSocket connection {} for pixel map ID: {}", connection.id(), pixelMapId);
            return Uni.createFrom().item(
                    pixelMapService.getPixelMap(mapId)
                            .orElseThrow(() -> new IllegalArgumentException("Pixel map not found for ID: " + pixelMapId))
            );
        } catch (IllegalArgumentException e) {
            log.error("Invalid pixelMapId: {}", pixelMapId, e);
            return Uni.createFrom().failure(e);
        } catch (Exception e) {
            log.error("Unexpected error in onOpen: {}", e.getMessage(), e);
            return Uni.createFrom().failure(e);
        }
    }


    @OnClose
    public void onClose(@PathParam("pixelMapId") String pixelMapId) {
        log.info("onClose> Closing connection {}", connection.id());


    }


}
