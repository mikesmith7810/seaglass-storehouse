package com.mike.seaglassstorehouse.resource;

import com.mike.seaglassstorehouse.model.CategoryRequest;
import com.mike.seaglassstorehouse.model.CategoryResponse;
import com.mike.seaglassstorehouse.service.CategoryService;
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

import java.util.List;

@Path("/api/categories")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CategoryResource {

    private final CategoryService categoryService;

    @Inject
    public CategoryResource(final CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @POST
    public Response createCategory(final CategoryRequest request) {
        final CategoryResponse categoryResponse = categoryService.createCategory(request);
        return Response.status(Response.Status.CREATED).entity(categoryResponse).build();
    }

    @GET
    public List<CategoryResponse> getAllCategories() {
        return categoryService.getAllCategories();
    }

    @DELETE
    @Path("/{id:\\d+}")
    public Response removeCategory(@PathParam("id") final Long id) {
        categoryService.removeCategory(id);
        return Response.noContent().build();
    }
}
