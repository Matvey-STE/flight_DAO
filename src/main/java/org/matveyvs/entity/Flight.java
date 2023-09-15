package org.matveyvs.entity;

import java.time.LocalDateTime;

public record Flight(Long id,
                     String flightNo,
                     LocalDateTime departureDate,
                     String departureAirportCode,
                     LocalDateTime arrivalDate,
                     String arrivalAirportCode,
                     Integer aircraftId,
                     FlightStatus status) {
    public Flight(String flightNo,
                  LocalDateTime departureDate,
                  String departureAirportCode,
                  LocalDateTime arrivalDate,
                  String arrivalAirportCode,
                  Integer aircraftId,
                  FlightStatus status) {
        this(null, flightNo, departureDate, departureAirportCode, arrivalDate,
                arrivalAirportCode, aircraftId, status);
    }
}
