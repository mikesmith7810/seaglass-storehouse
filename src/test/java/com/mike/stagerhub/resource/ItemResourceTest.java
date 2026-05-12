package com.mike.stagerhub.resource;

import com.mike.stagerhub.model.ItemRequest;
import com.mike.stagerhub.model.ItemResponse;
import com.mike.stagerhub.service.ItemService;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemResourceTest {

    @Mock
    ItemService itemService;

    ItemResource itemResource;

    @BeforeEach
    void setUp() {
        itemResource = new ItemResource(itemService);
    }

    @Test
    void createItem_returns201WithBody() {
        final ItemRequest request = new ItemRequest("Grey sofa", 1L, 1L, new BigDecimal("499.99"), null, null, null);
        final ItemResponse response = buildItemResponse(1L, "Grey sofa");
        when(itemService.createItem(request)).thenReturn(response);

        final Response result = itemResource.createItem(request);

        assertThat(result.getStatus()).isEqualTo(201);
        assertThat(result.getEntity()).isEqualTo(response);
    }

    @Test
    void getItem_returnsServiceResult() {
        final ItemResponse response = buildItemResponse(1L, "Grey sofa");
        when(itemService.getItem(1L)).thenReturn(response);

        final ItemResponse result = itemResource.getItem(1L);

        assertThat(result).isEqualTo(response);
    }

    @Test
    void updateItem_returnsServiceResult() {
        final ItemRequest request = new ItemRequest("Updated sofa", 1L, 1L, new BigDecimal("399.99"), null, null, null);
        final ItemResponse response = buildItemResponse(1L, "Updated sofa");
        when(itemService.updateItem(1L, request)).thenReturn(response);

        final ItemResponse result = itemResource.updateItem(1L, request);

        assertThat(result).isEqualTo(response);
    }

    @Test
    void getAllItems_returnsServiceResult() {
        final List<ItemResponse> items = List.of(buildItemResponse(1L, "Grey sofa"));
        when(itemService.getAllItems()).thenReturn(items);

        final List<ItemResponse> result = itemResource.getAllItems();

        assertThat(result).isEqualTo(items);
    }

    @Test
    void searchItems_passesAllParamsToService() {
        final List<ItemResponse> items = List.of(buildItemResponse(1L, "Grey sofa"));
        when(itemService.searchItems("sofa", 1L, 2L, BigDecimal.ZERO, new BigDecimal("1000"))).thenReturn(items);

        final List<ItemResponse> result = itemResource.searchItems("sofa", 1L, 2L, BigDecimal.ZERO, new BigDecimal("1000"));

        assertThat(result).isEqualTo(items);
    }

    @Test
    void deleteItem_returns204() {
        final Response result = itemResource.deleteItem(1L);

        assertThat(result.getStatus()).isEqualTo(204);
        verify(itemService).deleteItem(1L);
    }

    private ItemResponse buildItemResponse(final Long id, final String description) {
        return new ItemResponse(id, description, 1L, "Sofa", 1L, "Living Room",
                new BigDecimal("499.99"), null, null, null, null, Instant.now(), Instant.now());
    }
}
