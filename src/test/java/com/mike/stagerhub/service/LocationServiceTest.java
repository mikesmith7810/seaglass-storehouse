package com.mike.stagerhub.service;

import com.mike.stagerhub.entity.Location;
import com.mike.stagerhub.exception.ConflictException;
import com.mike.stagerhub.model.LocationRequest;
import com.mike.stagerhub.model.LocationResponse;
import com.mike.stagerhub.repository.ItemRepository;
import com.mike.stagerhub.repository.LocationRepository;
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
class LocationServiceTest {

    @Mock
    LocationRepository locationRepository;

    @Mock
    ItemRepository itemRepository;

    LocationService locationService;

    @BeforeEach
    void setUp() {
        locationService = new LocationService(locationRepository, itemRepository);
    }

    @Test
    void createLocation_persistsAndReturnsResponse() {
        doAnswer(invocation -> {
            final Location location = invocation.getArgument(0);
            setId(location, 1L);
            return null;
        }).when(locationRepository).persist(any(Location.class));

        final LocationResponse result = locationService.createLocation(new LocationRequest("Living Room", null));

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.name()).isEqualTo("Living Room");
        assertThat(result.photoUrl()).isNull();
    }

    @Test
    void getAllLocations_returnsMappedResponses() {
        final Location locationOne = buildLocation(1L, "Living Room");
        final Location locationTwo = buildLocation(2L, "Bedroom");
        when(locationRepository.listAll()).thenReturn(List.of(locationOne, locationTwo));

        final List<LocationResponse> result = locationService.getAllLocations();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).name()).isEqualTo("Living Room");
        assertThat(result.get(1).name()).isEqualTo("Bedroom");
    }

    @Test
    void removeLocation_deletesLocation_whenNoItemsAssigned() {
        final Location location = buildLocation(1L, "Living Room");
        when(locationRepository.findByIdOptional(1L)).thenReturn(Optional.of(location));
        when(itemRepository.count("location.id", 1L)).thenReturn(0L);

        locationService.removeLocation(1L);

        verify(locationRepository).delete(location);
    }

    @Test
    void removeLocation_throwsConflict_whenItemsAreAssigned() {
        final Location location = buildLocation(1L, "Living Room");
        when(locationRepository.findByIdOptional(1L)).thenReturn(Optional.of(location));
        when(itemRepository.count("location.id", 1L)).thenReturn(3L);

        assertThatThrownBy(() -> locationService.removeLocation(1L))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Living Room");
    }

    @Test
    void removeLocation_throwsNotFound_whenLocationDoesNotExist() {
        when(locationRepository.findByIdOptional(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> locationService.removeLocation(99L))
                .isInstanceOf(NotFoundException.class);
    }

    private Location buildLocation(final Long id, final String name) {
        final Location location = new Location(name, null);
        setId(location, id);
        return location;
    }

    private void setId(final Location location, final Long id) {
        try {
            final var field = Location.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(location, id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
