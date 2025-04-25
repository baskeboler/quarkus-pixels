package com.gil;

import com.gil.service.PixelMapService;
import com.gil.service.PixelMapService.PixelMapData;
import com.gil.service.PixelMapService.PixelMapUpdate;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.Set;
import java.util.UUID;

@Path("/api")
public class ExampleResource {

    @Inject
    PixelMapService pixelMapService;

    @Path("/hello")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return "Hello from Quarkus REST";
    }

    @Path("/pixels")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Set<UUID> getPixelMaps() {
        return pixelMapService.getAvailablePixelMapIds();
    }

    @Path("/pixels")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public PixelMapData createPixelMap() {
        return pixelMapService.createPixelMap();
    }

    @Path("/pixels/{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public PixelMapData getPixelMap(@PathParam("id") String id) {

        return pixelMapService.getPixelMap(UUID.fromString(id))
                .orElseThrow(() -> new NotFoundException("PixelMap not found"));
    }

    @Path("/pixels/{id}")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    public PixelMapUpdate updatePixelMap(@PathParam("id") String id, PixelMapUpdate pixelMapUpdate) {
        return pixelMapService.updatePixelMap(UUID.fromString(id), pixelMapUpdate.x(), pixelMapUpdate.y(), pixelMapUpdate.color())
                .map(pixelMap -> pixelMapUpdate)
                .orElseThrow(() -> new NotFoundException("PixelMap not found"));
    }
}
