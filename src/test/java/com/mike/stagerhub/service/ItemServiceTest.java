package com.mike.stagerhub.service;

import com.mike.stagerhub.entity.Category;
import com.mike.stagerhub.entity.Item;
import com.mike.stagerhub.entity.Location;
import com.mike.stagerhub.model.ItemRequest;
import com.mike.stagerhub.model.ItemResponse;
import com.mike.stagerhub.repository.CategoryRepository;
import com.mike.stagerhub.repository.ItemRepository;
import com.mike.stagerhub.repository.LocationRepository;
import jakarta.ws.rs.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @Mock
    ItemRepository itemRepository;

    @Mock
    LocationRepository locationRepository;

    @Mock
    CategoryRepository categoryRepository;

    ItemService itemService;

    @BeforeEach
    void setUp() {
        itemService = new ItemService(itemRepository, locationRepository, categoryRepository);
    }

    @Test
    void createItem_persistsAndReturnsResponse() {
        final Location location = buildLocation(1L, "Living Room");
        final Category category = buildCategory(2L, "Sofa");
        when(locationRepository.findByIdOptional(1L)).thenReturn(Optional.of(location));
        when(categoryRepository.findByIdOptional(2L)).thenReturn(Optional.of(category));
        doAnswer(invocation -> {
            final Item item = invocation.getArgument(0);
            setItemId(item, 10L);
            return null;
        }).when(itemRepository).persist(any(Item.class));

        final ItemResponse result = itemService.createItem(
                new ItemRequest("Grey corner sofa", 2L, 1L, new BigDecimal("599.99"),
                        new BigDecimal("85"), new BigDecimal("250"), new BigDecimal("95")));

        assertThat(result.id()).isEqualTo(10L);
        assertThat(result.description()).isEqualTo("Grey corner sofa");
        assertThat(result.locationId()).isEqualTo(1L);
        assertThat(result.locationName()).isEqualTo("Living Room");
        assertThat(result.categoryId()).isEqualTo(2L);
        assertThat(result.categoryName()).isEqualTo("Sofa");
        assertThat(result.price()).isEqualByComparingTo("599.99");
    }

    @Test
    void createItem_throwsNotFound_whenLocationDoesNotExist() {
        when(locationRepository.findByIdOptional(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.createItem(
                new ItemRequest("Sofa", 1L, 99L, BigDecimal.TEN, null, null, null)))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void createItem_throwsNotFound_whenCategoryDoesNotExist() {
        final Location location = buildLocation(1L, "Living Room");
        when(locationRepository.findByIdOptional(1L)).thenReturn(Optional.of(location));
        when(categoryRepository.findByIdOptional(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.createItem(
                new ItemRequest("Sofa", 99L, 1L, BigDecimal.TEN, null, null, null)))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void getItem_returnsResponse_whenItemExists() {
        final Item item = buildItem(5L, "Oak table", buildCategory(1L, "Table"), buildLocation(2L, "Dining Room"));
        when(itemRepository.findByIdOptional(5L)).thenReturn(Optional.of(item));

        final ItemResponse result = itemService.getItem(5L);

        assertThat(result.id()).isEqualTo(5L);
        assertThat(result.description()).isEqualTo("Oak table");
    }

    @Test
    void getItem_throwsNotFound_whenItemDoesNotExist() {
        when(itemRepository.findByIdOptional(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.getItem(99L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void getAllItems_returnsMappedResponses() {
        final Item itemOne = buildItem(1L, "Sofa", buildCategory(1L, "Sofa"), buildLocation(1L, "Living Room"));
        final Item itemTwo = buildItem(2L, "Chair", buildCategory(2L, "Chair"), buildLocation(2L, "Office"));
        when(itemRepository.listAll()).thenReturn(List.of(itemOne, itemTwo));

        final List<ItemResponse> result = itemService.getAllItems();

        assertThat(result).hasSize(2);
        assertThat(result).extracting(ItemResponse::description).containsExactly("Sofa", "Chair");
    }

    @Test
    void searchItems_delegatesToRepositoryWithAllParams() {
        final Item item = buildItem(1L, "Sofa", buildCategory(1L, "Sofa"), buildLocation(1L, "Living Room"));
        when(itemRepository.search("sofa", 1L, 1L, BigDecimal.ZERO, new BigDecimal("1000")))
                .thenReturn(List.of(item));

        final List<ItemResponse> result = itemService.searchItems("sofa", 1L, 1L, BigDecimal.ZERO, new BigDecimal("1000"));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).description()).isEqualTo("Sofa");
    }

    private Location buildLocation(final Long id, final String name) {
        final Location location = new Location(name);
        setField(location, Location.class, "id", id);
        return location;
    }

    private Category buildCategory(final Long id, final String name) {
        final Category category = new Category(name);
        setField(category, Category.class, "id", id);
        return category;
    }

    private Item buildItem(final Long id, final String description, final Category category, final Location location) {
        final Item item = new Item(description, category, location, new BigDecimal("100.00"), null, null, null);
        setItemId(item, id);
        return item;
    }

    private void setItemId(final Item item, final Long id) {
        setField(item, Item.class, "id", id);
    }

    private void setField(final Object target, final Class<?> clazz, final String fieldName, final Object value) {
        try {
            final var field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
