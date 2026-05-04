package com.mike.stagerhub.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "item")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String description;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    @Column(nullable = false)
    private BigDecimal price;

    private BigDecimal heightCm;
    private BigDecimal widthCm;
    private BigDecimal depthCm;
    private String photoUrl;

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    protected Item() {}

    public Item(
            final String description,
            final Category category,
            final Location location,
            final BigDecimal price,
            final BigDecimal heightCm,
            final BigDecimal widthCm,
            final BigDecimal depthCm) {
        this.description = description;
        this.category = category;
        this.location = location;
        this.price = price;
        this.heightCm = heightCm;
        this.widthCm = widthCm;
        this.depthCm = depthCm;
    }

    public void update(
            final String description,
            final Category category,
            final Location location,
            final BigDecimal price,
            final BigDecimal heightCm,
            final BigDecimal widthCm,
            final BigDecimal depthCm) {
        this.description = description;
        this.category = category;
        this.location = location;
        this.price = price;
        this.heightCm = heightCm;
        this.widthCm = widthCm;
        this.depthCm = depthCm;
    }

    public void assignPhotoUrl(final String photoUrl) {
        this.photoUrl = photoUrl;
    }

    @PrePersist
    void onPrePersist() {
        createdAt = Instant.now();
        updatedAt = Instant.now();
    }

    @PreUpdate
    void onPreUpdate() {
        updatedAt = Instant.now();
    }

    public Long getId() { return id; }
    public String getDescription() { return description; }
    public Category getCategory() { return category; }
    public Location getLocation() { return location; }
    public BigDecimal getPrice() { return price; }
    public BigDecimal getHeightCm() { return heightCm; }
    public BigDecimal getWidthCm() { return widthCm; }
    public BigDecimal getDepthCm() { return depthCm; }
    public String getPhotoUrl() { return photoUrl; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
