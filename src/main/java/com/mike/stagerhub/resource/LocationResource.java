package com.mike.stagerhub.resource;

import com.mike.stagerhub.model.LocationRequest;
import com.mike.stagerhub.model.LocationResponse;
import com.mike.stagerhub.service.LocationService;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;

import java.util.List;

@Path("/api/locations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LocationResource {

    private static final Logger LOG = Logger.getLogger(LocationResource.class);

    private final LocationService locationService;

    @Inject
    public LocationResource(final LocationService locationService) {
        this.locationService = locationService;
    }

    @POST
    public Response createLocation(final LocationRequest request) {
        LOG.info("POST /api/locations");
        final LocationResponse locationResponse = locationService.createLocation(request);
        return Response.status(Response.Status.CREATED).entity(locationResponse).build();
    }

    @GET
    public List<LocationResponse> getAllLocations() {
        LOG.info("GET /api/locations");
        return locationService.getAllLocations();
    }

    @DELETE
    @Path("/{id:\\d+}")
    public Response removeLocation(@PathParam("id") final Long id) {
        LOG.infof("DELETE /api/locations/%d", id);
        locationService.removeLocation(id);
        return Response.noContent().build();
    }
}
