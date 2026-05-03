package com.mike.seaglassstorehouse.model;

import java.math.BigDecimal;

public record ItemRequest(
        String description,
        Long categoryId,
        Long locationId,
        BigDecimal price,
        BigDecimal heightCm,
        BigDecimal widthCm,
        BigDecimal depthCm) {}
