package org.matveyvs.entity;

import java.math.BigDecimal;

public record Ticket(Long id,
                     String passportNo,
                     String passengerName,
                     Flight flight,
                     String seatNo,
                     BigDecimal cost) {
    public Ticket(String passportNo, String passengerName, Flight flight, String seatNo, BigDecimal cost) {
        this(null, passportNo, passengerName, flight, seatNo, cost);
    }
}
