package com.mike.seaglassstorehouse.repository;

import com.mike.seaglassstorehouse.entity.Item;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class ItemRepository implements PanacheRepository<Item> {

    public List<Item> search(
            final String q,
            final Long categoryId,
            final Long locationId,
            final BigDecimal minPrice,
            final BigDecimal maxPrice) {

        final List<String> conditions = new ArrayList<>();
        final Map<String, Object> params = new HashMap<>();

        if (q != null && !q.isBlank()) {
            conditions.add("lower(description) like lower(:q)");
            params.put("q", "%" + q + "%");
        }
        if (categoryId != null) {
            conditions.add("category.id = :categoryId");
            params.put("categoryId", categoryId);
        }
        if (locationId != null) {
            conditions.add("location.id = :locationId");
            params.put("locationId", locationId);
        }
        if (minPrice != null) {
            conditions.add("price >= :minPrice");
            params.put("minPrice", minPrice);
        }
        if (maxPrice != null) {
            conditions.add("price <= :maxPrice");
            params.put("maxPrice", maxPrice);
        }

        if (conditions.isEmpty()) {
            return listAll();
        }

        return find(String.join(" and ", conditions), params).list();
    }
}
