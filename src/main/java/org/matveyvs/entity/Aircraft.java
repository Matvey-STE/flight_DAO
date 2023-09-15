package org.matveyvs.entity;

import java.math.BigDecimal;

public record Aircraft(Long id, String model) {
    public Aircraft(String model) {
        this(null, model);
    }
}
