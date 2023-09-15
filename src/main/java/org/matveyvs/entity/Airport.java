package org.matveyvs.entity;

public record Airport(String code, String country, String city) {
    public Airport(String country, String city) {
        this(null, country, city);
    }

}
