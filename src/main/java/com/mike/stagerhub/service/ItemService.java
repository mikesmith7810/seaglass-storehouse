package com.mike.stagerhub.service;

import com.mike.stagerhub.entity.Category;
import com.mike.stagerhub.entity.Item;
import com.mike.stagerhub.entity.Location;
import com.mike.stagerhub.model.ItemRequest;
import com.mike.stagerhub.model.ItemResponse;
import com.mike.stagerhub.repository.CategoryRepository;
import com.mike.stagerhub.repository.ItemRepository;
import com.mike.stagerhub.repository.LocationRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

import java.math.BigDecimal;
import java.util.List;

@ApplicationScoped
public class ItemService {

    private final ItemRepository itemRepository;
    private final LocationRepository locationRepository;
    private final CategoryRepository categoryRepository;

    @Inject
    public ItemService(
            final ItemRepository itemRepository,
            final LocationRepository locationRepository,
            final CategoryRepository categoryRepository) {
        this.itemRepository = itemRepository;
        this.locationRepository = locationRepository;
        this.categoryRepository = categoryRepository;
    }

    @Transactional
    public ItemResponse createItem(final ItemRequest request) {
        final Location location = resolveLocation(request.locationId());
        final Category category = resolveCategory(request.categoryId());
        final Item item = new Item(
                request.description(),
                category,
                location,
                request.price(),
                request.heightCm(),
                request.widthCm(),
                request.depthCm());
        itemRepository.persist(item);
        return toResponse(item);
    }

    public ItemResponse getItem(final Long id) {
        return itemRepository.findByIdOptional(id)
                .map(this::toResponse)
                .orElseThrow(() -> new NotFoundException("Item not found: " + id));
    }

    @Transactional
    public ItemResponse updateItem(final Long id, final ItemRequest request) {
        final Item item = itemRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Item not found: " + id));
        final Location location = resolveLocation(request.locationId());
        final Category category = resolveCategory(request.categoryId());
        item.update(request.description(), category, location, request.price(),
                request.heightCm(), request.widthCm(), request.depthCm());
        return toResponse(item);
    }

    @Transactional
    public void deleteItem(final Long id) {
        final Item item = itemRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Item not found: " + id));
        itemRepository.delete(item);
    }

    public List<ItemResponse> getAllItems() {
        return itemRepository.listAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public List<ItemResponse> searchItems(
            final String q,
            final Long categoryId,
            final Long locationId,
            final BigDecimal minPrice,
            final BigDecimal maxPrice) {
        return itemRepository.search(q, categoryId, locationId, minPrice, maxPrice).stream()
                .map(this::toResponse)
                .toList();
    }

    private Location resolveLocation(final Long locationId) {
        return locationRepository.findByIdOptional(locationId)
                .orElseThrow(() -> new NotFoundException("Location not found: " + locationId));
    }

    private Category resolveCategory(final Long categoryId) {
        return categoryRepository.findByIdOptional(categoryId)
                .orElseThrow(() -> new NotFoundException("Category not found: " + categoryId));
    }

    private ItemResponse toResponse(final Item item) {
        return new ItemResponse(
                item.getId(),
                item.getDescription(),
                item.getCategory().getId(),
                item.getCategory().getName(),
                item.getLocation().getId(),
                item.getLocation().getName(),
                item.getPrice(),
                item.getHeightCm(),
                item.getWidthCm(),
                item.getDepthCm(),
                item.getPhotoUrl(),
                item.getCreatedAt(),
                item.getUpdatedAt());
    }
}
