package com.mike.seaglassstorehouse.resource;

import com.mike.seaglassstorehouse.model.LocationRequest;
import com.mike.seaglassstorehouse.model.LocationResponse;
import com.mike.seaglassstorehouse.service.LocationService;
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
class LocationResourceTest {

    @Mock
    LocationService locationService;

    LocationResource locationResource;

    @BeforeEach
    void setUp() {
        locationResource = new LocationResource(locationService);
    }

    @Test
    void createLocation_returns201WithBody() {
        final LocationRequest request = new LocationRequest("Living Room");
        final LocationResponse response = new LocationResponse(1L, "Living Room");
        when(locationService.createLocation(request)).thenReturn(response);

        final Response result = locationResource.createLocation(request);

        assertThat(result.getStatus()).isEqualTo(201);
        assertThat(result.getEntity()).isEqualTo(response);
    }

    @Test
    void getAllLocations_returnsServiceResult() {
        final List<LocationResponse> locations = List.of(new LocationResponse(1L, "Living Room"));
        when(locationService.getAllLocations()).thenReturn(locations);

        final List<LocationResponse> result = locationResource.getAllLocations();

        assertThat(result).isEqualTo(locations);
    }

    @Test
    void removeLocation_returns204() {
        final Response result = locationResource.removeLocation(1L);

        verify(locationService).removeLocation(1L);
        assertThat(result.getStatus()).isEqualTo(204);
    }
}
