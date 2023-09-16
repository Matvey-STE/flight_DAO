package org.matveyvs.service;

import org.matveyvs.dao.FlightDao;
import org.matveyvs.dto.FlightDto;

import java.util.List;
import java.util.stream.Collectors;

public class FlightService {
    private static final FlightService INSTANCE = new FlightService();
    private final FlightDao flightDao = FlightDao.getInstance();
    public List<FlightDto> findAll(){
      return flightDao.findAll().stream().map(flight ->
              new FlightDto(flight.id(),
                      "%s - %s - %s".formatted(
                              flight.departureAirportCode(),
                              flight.arrivalAirportCode(),
                              flight.status()
                      ))).collect(Collectors.toList());
    }
    public static FlightService getInstance(){
        return INSTANCE;
    }

    private FlightService(){

    }
}
