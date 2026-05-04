package com.mike.stagerhub.model;

import java.math.BigDecimal;
import java.time.Instant;

public record ItemResponse(
        Long id,
        String description,
        Long categoryId,
        String categoryName,
        Long locationId,
        String locationName,
        BigDecimal price,
        BigDecimal heightCm,
        BigDecimal widthCm,
        BigDecimal depthCm,
        String photoUrl,
        Instant createdAt,
        Instant updatedAt) {}
