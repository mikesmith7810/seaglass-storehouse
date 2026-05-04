package com.mike.stagerhub.service;

import com.mike.stagerhub.entity.Category;
import com.mike.stagerhub.exception.ConflictException;
import com.mike.stagerhub.model.CategoryRequest;
import com.mike.stagerhub.model.CategoryResponse;
import com.mike.stagerhub.repository.CategoryRepository;
import com.mike.stagerhub.repository.ItemRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

import java.util.List;

@ApplicationScoped
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ItemRepository itemRepository;

    @Inject
    public CategoryService(final CategoryRepository categoryRepository, final ItemRepository itemRepository) {
        this.categoryRepository = categoryRepository;
        this.itemRepository = itemRepository;
    }

    @Transactional
    public CategoryResponse createCategory(final CategoryRequest request) {
        final Category category = new Category(request.name());
        categoryRepository.persist(category);
        return toResponse(category);
    }

    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.listAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public void removeCategory(final Long id) {
        final Category category = categoryRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Category not found: " + id));

        if (itemRepository.count("category.id", id) > 0) {
            throw new ConflictException("Category '" + category.getName() + "' has items assigned — reassign them before deleting");
        }

        categoryRepository.delete(category);
    }

    private CategoryResponse toResponse(final Category category) {
        return new CategoryResponse(category.getId(), category.getName());
    }
}
