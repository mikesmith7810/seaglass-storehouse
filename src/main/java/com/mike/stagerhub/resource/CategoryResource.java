package com.mike.stagerhub.resource;

import com.mike.stagerhub.model.CategoryRequest;
import com.mike.stagerhub.model.CategoryResponse;
import com.mike.stagerhub.service.CategoryService;
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

@Path("/api/categories")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CategoryResource {

    private static final Logger LOG = Logger.getLogger(CategoryResource.class);

    private final CategoryService categoryService;

    @Inject
    public CategoryResource(final CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @POST
    public Response createCategory(final CategoryRequest request) {
        LOG.info("POST /api/categories");
        final CategoryResponse categoryResponse = categoryService.createCategory(request);
        return Response.status(Response.Status.CREATED).entity(categoryResponse).build();
    }

    @GET
    public List<CategoryResponse> getAllCategories() {
        LOG.info("GET /api/categories");
        return categoryService.getAllCategories();
    }

    @DELETE
    @Path("/{id:\\d+}")
    public Response removeCategory(@PathParam("id") final Long id) {
        LOG.infof("DELETE /api/categories/%d", id);
        categoryService.removeCategory(id);
        return Response.noContent().build();
    }
}
