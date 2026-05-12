package com.mike.stagerhub.resource;

import com.mike.stagerhub.model.ItemRequest;
import com.mike.stagerhub.model.ItemResponse;
import com.mike.stagerhub.service.ItemService;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;

import java.math.BigDecimal;
import java.util.List;

@Path("/api/items")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ItemResource {

    private static final Logger LOG = Logger.getLogger(ItemResource.class);

    private final ItemService itemService;

    @Inject
    public ItemResource(final ItemService itemService) {
        this.itemService = itemService;
    }

    @POST
    public Response createItem(final ItemRequest request) {
        LOG.info("POST /api/items");
        final ItemResponse itemResponse = itemService.createItem(request);
        return Response.status(Response.Status.CREATED).entity(itemResponse).build();
    }

    @GET
    @Path("/{id:\\d+}")
    public ItemResponse getItem(@PathParam("id") final Long id) {
        LOG.infof("GET /api/items/%d", id);
        return itemService.getItem(id);
    }

    @PUT
    @Path("/{id:\\d+}")
    public ItemResponse updateItem(@PathParam("id") final Long id, final ItemRequest request) {
        LOG.infof("PUT /api/items/%d", id);
        return itemService.updateItem(id, request);
    }

    @DELETE
    @Path("/{id:\\d+}")
    public Response deleteItem(@PathParam("id") final Long id) {
        LOG.infof("DELETE /api/items/%d", id);
        itemService.deleteItem(id);
        return Response.noContent().build();
    }

    @GET
    public List<ItemResponse> getAllItems() {
        LOG.info("GET /api/items");
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
        LOG.infof("GET /api/items/search q=%s categoryId=%s locationId=%s minPrice=%s maxPrice=%s",
                q, categoryId, locationId, minPrice, maxPrice);
        return itemService.searchItems(q, categoryId, locationId, minPrice, maxPrice);
    }
}
