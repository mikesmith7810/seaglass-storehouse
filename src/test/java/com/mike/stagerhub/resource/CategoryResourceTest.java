package com.mike.stagerhub.resource;

import com.mike.stagerhub.model.CategoryRequest;
import com.mike.stagerhub.model.CategoryResponse;
import com.mike.stagerhub.service.CategoryService;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryResourceTest {

    @Mock
    CategoryService categoryService;

    CategoryResource categoryResource;

    @BeforeEach
    void setUp() {
        categoryResource = new CategoryResource(categoryService);
    }

    @Test
    void createCategory_returns201WithBody() {
        final CategoryRequest request = new CategoryRequest("Sofa");
        final CategoryResponse response = new CategoryResponse(1L, "Sofa");
        when(categoryService.createCategory(request)).thenReturn(response);

        final Response result = categoryResource.createCategory(request);

        assertThat(result.getStatus()).isEqualTo(201);
        assertThat(result.getEntity()).isEqualTo(response);
    }

    @Test
    void getAllCategories_returnsServiceResult() {
        final List<CategoryResponse> categories = List.of(new CategoryResponse(1L, "Sofa"));
        when(categoryService.getAllCategories()).thenReturn(categories);

        final List<CategoryResponse> result = categoryResource.getAllCategories();

        assertThat(result).isEqualTo(categories);
    }

    @Test
    void removeCategory_returns204() {
        final Response result = categoryResource.removeCategory(1L);

        verify(categoryService).removeCategory(1L);
        assertThat(result.getStatus()).isEqualTo(204);
    }
}
