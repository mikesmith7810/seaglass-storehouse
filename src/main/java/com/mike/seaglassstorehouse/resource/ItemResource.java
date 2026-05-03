package com.mike.seaglassstorehouse.resource;

import com.mike.seaglassstorehouse.model.ItemRequest;
import com.mike.seaglassstorehouse.model.ItemResponse;
import com.mike.seaglassstorehouse.service.ItemService;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.math.BigDecimal;
import java.util.List;

@Path("/api/items")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ItemResource {

    private final ItemService itemService;

    @Inject
    public ItemResource(final ItemService itemService) {
        this.itemService = itemService;
    }

    @POST
    public Response createItem(final ItemRequest request) {
        final ItemResponse itemResponse = itemService.createItem(request);
        return Response.status(Response.Status.CREATED).entity(itemResponse).build();
    }

    @GET
    @Path("/{id:\\d+}")
    public ItemResponse getItem(@PathParam("id") final Long id) {
        return itemService.getItem(id);
    }

    @PUT
    @Path("/{id:\\d+}")
    public ItemResponse updateItem(@PathParam("id") final Long id, final ItemRequest request) {
        return itemService.updateItem(id, request);
    }

    @GET
    public List<ItemResponse> getAllItems() {
        return itemService.getAllItems();
    }

    @GET
    @Path("/search")
    public List<ItemResponse> searchItems(
            @QueryParam("q") final String q,
            @QueryParam("categoryId") final Long categoryId,
            @QueryParam("locationId") final Long locationId,
            @QueryParam("minPrice") final BigDecimal minPrice,
            @QueryParam("maxPrice") final BigDecimal maxPrice) {
        return itemService.searchItems(q, categoryId, locationId, minPrice, maxPrice);
    }
}
