package com.mike.seaglassstorehouse.service;

import com.mike.seaglassstorehouse.entity.Location;
import com.mike.seaglassstorehouse.exception.ConflictException;
import com.mike.seaglassstorehouse.model.LocationRequest;
import com.mike.seaglassstorehouse.model.LocationResponse;
import com.mike.seaglassstorehouse.repository.ItemRepository;
import com.mike.seaglassstorehouse.repository.LocationRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

import java.util.List;

@ApplicationScoped
public class LocationService {

    private final LocationRepository locationRepository;
    private final ItemRepository itemRepository;

    @Inject
    public LocationService(final LocationRepository locationRepository, final ItemRepository itemRepository) {
        this.locationRepository = locationRepository;
        this.itemRepository = itemRepository;
    }

    @Transactional
    public LocationResponse createLocation(final LocationRequest request) {
        final Location location = new Location(request.name());
        locationRepository.persist(location);
        return toResponse(location);
    }

    public List<LocationResponse> getAllLocations() {
        return locationRepository.listAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public void removeLocation(final Long id) {
        final Location location = locationRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Location not found: " + id));

        if (itemRepository.count("location.id", id) > 0) {
            throw new ConflictException("Location '" + location.getName() + "' has items assigned — reassign them before deleting");
        }

        locationRepository.delete(location);
    }

    private LocationResponse toResponse(final Location location) {
        return new LocationResponse(location.getId(), location.getName());
    }
}
