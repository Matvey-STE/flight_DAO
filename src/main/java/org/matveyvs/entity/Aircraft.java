package org.matveyvs.entity;

public record Aircraft(Long id, String model) {
    public Aircraft(String model) {
        this(null, model);
    }
}
