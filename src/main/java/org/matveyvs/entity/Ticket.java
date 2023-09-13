package org.matveyvs.entity;

import java.math.BigDecimal;

public record Ticket(Long id,
                     String passportNo,
                     String passengerName,
                     Long flightId,
                     String seatNo,
                     BigDecimal cost) {
    public Ticket(String passportNo, String passengerName, Long flightId, String seatNo, BigDecimal cost) {
        this(null, passportNo, passengerName, flightId, seatNo, cost);
    }
}
