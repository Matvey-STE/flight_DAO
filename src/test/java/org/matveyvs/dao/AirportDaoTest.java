package org.matveyvs.dao;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.matveyvs.entity.Airport;
import org.matveyvs.utils.ConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class AirportDaoTest {
    private String testKey;
    private AirportDao airportDao;
    private Connection connection;
    private static final String DELETE_SQL = """
            DELETE FROM airport
            WHERE code = ?
            """;

    @BeforeEach
    void setUp() throws SQLException {
        airportDao = AirportDao.getInstance();
        connection = ConnectionManager.open();
    }
    @AfterEach
    void tearDown() throws SQLException{
        PreparedStatement statement = connection.prepareStatement(DELETE_SQL);
        statement.setString(1, testKey);
        statement.executeUpdate();
        connection.close();
    }
    @Test
    void save() {
        Airport airport = new Airport("TST", "TestCountry", "TestCity");

        // Save the airport
        Airport savedAirport = airportDao.save(airport);

        assertNotNull(savedAirport);
        assertEquals("TST", savedAirport.code());
        assertEquals("TestCountry", savedAirport.country());
        assertEquals("TestCity", savedAirport.city());

        //set key to delete after tests
        testKey = airport.code();
    }

    @Test
    void findAllAirports() {
        List<Airport> airports = airportDao.findAll();

        assertNotNull(airports);
        assertTrue(airports.size() > 0);
    }

    @Test
    void findAirportById() {
        Optional<Airport> optionalAirport = airportDao.findById("MSK");

        assertTrue(optionalAirport.isPresent());
        Airport airport = optionalAirport.get();
        assertEquals("Россия", airport.country());
    }
    @Test
    void update() {
        Airport airport = new Airport("TST", "TestCountry", "TestCity");
        airportDao.save(airport);
        testKey = airport.code();

        Airport airportToUpdate = new Airport("TST", "UpdatedCountry", "UpdatedCity");

        boolean updated = airportDao.update(airportToUpdate);
        assertTrue(updated);

        // Verify that the airport details have been updated
        Optional<Airport> updatedAirport = airportDao.findById("TST");
        assertTrue(updatedAirport.isPresent());
        assertEquals("UpdatedCountry", updatedAirport.get().country());
        assertEquals("UpdatedCity", updatedAirport.get().city());
    }
    @Test
    void delete(){
        Airport airport = new Airport("TST", "TestCountry", "TestCity");
        airportDao.save(airport);
        testKey = airport.code();

        boolean deleted = airportDao.delete("TST");
        assertTrue(deleted);

        // Verify that the airport has been deleted
        Optional<Airport> deletedAirport = airportDao.findById("TST");
        assertTrue(deletedAirport.isEmpty());
    }

}