package com.mike.stagerhub.model;

import java.math.BigDecimal;

public record ItemRequest(
        String description,
        Long categoryId,
        Long locationId,
        BigDecimal price,
        BigDecimal heightCm,
        BigDecimal widthCm,
        BigDecimal depthCm) {}
