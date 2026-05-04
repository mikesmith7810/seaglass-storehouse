package com.mike.stagerhub.service;

import com.mike.stagerhub.entity.Category;
import com.mike.stagerhub.exception.ConflictException;
import com.mike.stagerhub.model.CategoryRequest;
import com.mike.stagerhub.model.CategoryResponse;
import com.mike.stagerhub.repository.CategoryRepository;
import com.mike.stagerhub.repository.ItemRepository;
import jakarta.ws.rs.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    CategoryRepository categoryRepository;

    @Mock
    ItemRepository itemRepository;

    CategoryService categoryService;

    @BeforeEach
    void setUp() {
        categoryService = new CategoryService(categoryRepository, itemRepository);
    }

    @Test
    void createCategory_persistsAndReturnsResponse() {
        doAnswer(invocation -> {
            final Category category = invocation.getArgument(0);
            setId(category, 1L);
            return null;
        }).when(categoryRepository).persist(any(Category.class));

        final CategoryResponse result = categoryService.createCategory(new CategoryRequest("Sofa"));

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.name()).isEqualTo("Sofa");
    }

    @Test
    void getAllCategories_returnsMappedResponses() {
        final Category categoryOne = buildCategory(1L, "Sofa");
        final Category categoryTwo = buildCategory(2L, "Chair");
        when(categoryRepository.listAll()).thenReturn(List.of(categoryOne, categoryTwo));

        final List<CategoryResponse> result = categoryService.getAllCategories();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).name()).isEqualTo("Sofa");
        assertThat(result.get(1).name()).isEqualTo("Chair");
    }

    @Test
    void removeCategory_deletesCategory_whenNoItemsAssigned() {
        final Category category = buildCategory(1L, "Sofa");
        when(categoryRepository.findByIdOptional(1L)).thenReturn(Optional.of(category));
        when(itemRepository.count("category.id", 1L)).thenReturn(0L);

        categoryService.removeCategory(1L);

        verify(categoryRepository).delete(category);
    }

    @Test
    void removeCategory_throwsConflict_whenItemsAreAssigned() {
        final Category category = buildCategory(1L, "Sofa");
        when(categoryRepository.findByIdOptional(1L)).thenReturn(Optional.of(category));
        when(itemRepository.count("category.id", 1L)).thenReturn(2L);

        assertThatThrownBy(() -> categoryService.removeCategory(1L))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Sofa");
    }

    @Test
    void removeCategory_throwsNotFound_whenCategoryDoesNotExist() {
        when(categoryRepository.findByIdOptional(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.removeCategory(99L))
                .isInstanceOf(NotFoundException.class);
    }

    private Category buildCategory(final Long id, final String name) {
        final Category category = new Category(name);
        setId(category, id);
        return category;
    }

    private void setId(final Category category, final Long id) {
        try {
            final var field = Category.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(category, id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
