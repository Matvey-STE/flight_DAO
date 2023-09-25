package org.matveyvs.service;

import lombok.extern.slf4j.Slf4j;
import org.matveyvs.dao.FlightDao;
import org.matveyvs.dto.FlightDto;

import java.util.List;
import java.util.stream.Collectors;
@Slf4j
public class FlightService {
    private static final FlightService INSTANCE = new FlightService();
    private final FlightDao flightDao = FlightDao.getInstance();
    public List<FlightDto> findAll(){
      return flightDao.findAll().stream().map(flight ->
              new FlightDto(flight.getId(),
                      "%s - %s - %s".formatted(
                              flight.getDepartureAirport(),
                              flight.getArrivalAirport(),
                              flight.getStatus()
                      ))).collect(Collectors.toList());
    }
    public static FlightService getInstance(){
        return INSTANCE;
    }

    private FlightService(){

    }
}
