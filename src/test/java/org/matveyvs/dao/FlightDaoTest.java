package org.matveyvs.dao;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.matveyvs.entity.Flight;
import org.matveyvs.entity.FlightStatus;
import org.matveyvs.utils.ConnectionManager;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class FlightDaoTest {
    private static long newIdPointToReset;
    private long testKey;
    private FlightDao flightDao;
    private Connection connection;
    private static final String DELETE_SQL = """
            DELETE FROM flight
            WHERE id = ?
            """;

    private String getResetIdTableSql() {
        return "ALTER SEQUENCE flight_repo.public.flight_id_seq RESTART WITH " + newIdPointToReset;
    }

    @BeforeEach
    void setUp() {
        flightDao = FlightDao.getInstance();
        connection = ConnectionManager.open();

        newIdPointToReset = flightDao.findAll().size() + 1;
    }

    @AfterEach
    void tearDown() throws SQLException {
        PreparedStatement statement = connection.prepareStatement(DELETE_SQL);
        statement.setLong(1, testKey);
        statement.executeUpdate();

        Statement resetStatementId = connection.createStatement();
        resetStatementId.execute(getResetIdTableSql());
        connection.close();
    }

    @Test
    void save() {
        Flight testFlight = new Flight
                ( "FL123", LocalDateTime.now(),
                        "MNK", LocalDateTime.now(),
                        "MNK", 1, FlightStatus.DEPARTED);

        Flight savedAircraft = flightDao.save(testFlight);

        assertNotNull(savedAircraft);
        assertEquals(testFlight.flightNo(), savedAircraft.flightNo());
        assertEquals(testFlight.arrivalAirportCode(), savedAircraft.arrivalAirportCode());
        assertEquals(testFlight.status(), savedAircraft.status());

        testKey = savedAircraft.id();
    }

    @Test
    void findAll() {
        Flight testFlight = new Flight
                ( "FL123", LocalDateTime.now(),
                        "MNK", LocalDateTime.now(),
                        "MNK", 1, FlightStatus.DEPARTED);

        Flight savedFlight = flightDao.save(testFlight);


        List<Flight> aircrafts = flightDao.findAll();

        assertNotNull(aircrafts);
        assertTrue(aircrafts.size() > 0);
        testKey = savedFlight.id();
    }

    @Test
    void findById() {
        Flight testFlight = new Flight
                ( "FL123", LocalDateTime.now(),
                        "MNK", LocalDateTime.now(),
                        "MNK", 1, FlightStatus.DEPARTED);

        Flight savedFlight = flightDao.save(testFlight);

        Optional<Flight> optionalAircraft = flightDao.findById(savedFlight.id());

        assertTrue(optionalAircraft.isPresent());
        Flight flightFind = optionalAircraft.get();
        assertEquals(testFlight.flightNo(), flightFind.flightNo());
        assertEquals(testFlight.arrivalAirportCode(), flightFind.arrivalAirportCode());
        assertEquals(testFlight.status(), flightFind.status());
        testKey = savedFlight.id();
    }

    @Test
    void update() {
        Flight testFlight = new Flight
                ( "FL123", LocalDateTime.now(),
                        "MNK", LocalDateTime.now(),
                        "MNK", 1, FlightStatus.DEPARTED);

        Flight savedFlight = flightDao.save(testFlight);

        Flight updatedFlight = new Flight
                (savedFlight.id(),"UP123", LocalDateTime.now(),
                        "MNK", LocalDateTime.now(),
                        "MNK", 1, FlightStatus.ARRIVED);

        boolean updated = flightDao.update(updatedFlight);

        assertTrue(updated);

        Optional<Flight> findFlight = flightDao.findById(updatedFlight.id());
        assertTrue(findFlight.isPresent());
        assertEquals(updatedFlight.flightNo(), findFlight.get().flightNo());
        assertEquals(updatedFlight.status(), findFlight.get().status());

        testKey = updatedFlight.id();
    }

    @Test
    void delete() {
        Flight testFlight = new Flight
                ( "FL123", LocalDateTime.now(),
                        "MNK", LocalDateTime.now(),
                        "MNK", 1, FlightStatus.DEPARTED);

        Flight savedFlight = flightDao.save(testFlight);

        boolean deleted = flightDao.delete(savedFlight.id());
        assertTrue(deleted);
        // Verify that the airport has been deleted
        Optional<Flight> deletedAirport = flightDao.findById(testKey);
        assertTrue(deletedAirport.isEmpty());

        testKey = savedFlight.id();
    }
}