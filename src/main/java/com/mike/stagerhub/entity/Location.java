package com.mike.stagerhub.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "location")
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(name = "photo_url", length = 500)
    private String photoUrl;

    protected Location() {}

    public Location(final String name, final String photoUrl) {
        this.name = name;
        this.photoUrl = photoUrl;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getPhotoUrl() { return photoUrl; }
}
