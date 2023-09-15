package org.matveyvs.dao;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.matveyvs.entity.Aircraft;
import org.matveyvs.utils.ConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class AircraftDaoTest {
    private static long newIdPointToReset;
    private long testKey;
    private AircraftDao aircraftDao;
    private Connection connection;
    private static final String DELETE_SQL = """
            DELETE FROM aircraft
            WHERE id = ?
            """;

    private String getResetIdTableSql() {
        return "ALTER SEQUENCE flight_repo.public.aircraft_id_seq RESTART WITH " + newIdPointToReset;
    }

    @BeforeEach
    void setUp() throws SQLException {
        aircraftDao = AircraftDao.getInstance();
        connection = ConnectionManager.open();

        newIdPointToReset = aircraftDao.findAll().size() + 1;
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
    void save() throws SQLException {
        Aircraft aircraft = new Aircraft("TEST");
        Aircraft savedAircraft = aircraftDao.save(aircraft);

        assertNotNull(savedAircraft);
        assertEquals("TEST", savedAircraft.model());
        //set key to delete after tests
        testKey = savedAircraft.id();
    }

    @Test
    void findAll() throws SQLException {
        Aircraft aircraft = new Aircraft("TEST");
        Aircraft savedAircraft = aircraftDao.save(aircraft);

        List<Aircraft> aircrafts = aircraftDao.findAll();

        assertNotNull(aircrafts);
        assertTrue(aircrafts.size() > 0);
        testKey = savedAircraft.id();
    }

    @Test
    void findById() throws SQLException {
        Aircraft aircraft = new Aircraft("TEST");
        Aircraft savedAircraft = aircraftDao.save(aircraft);

        Optional<Aircraft> optionalAircraft = aircraftDao.findById(savedAircraft.id());

        assertTrue(optionalAircraft.isPresent());
        Aircraft aircraftFind = optionalAircraft.get();
        assertEquals("TEST", aircraftFind.model());
        testKey = savedAircraft.id();
    }

    @Test
    void update() {
        Aircraft aircraft = new Aircraft("TEST");
        Aircraft savedAircraft = aircraftDao.save(aircraft);

        Aircraft updateAircraft = new Aircraft(savedAircraft.id(), "UpdatedModel");

        boolean updated = aircraftDao.update(updateAircraft);
        assertTrue(updated);

        Optional<Aircraft> updatedAirport = aircraftDao.findById(updateAircraft.id());
        assertTrue(updatedAirport.isPresent());
        assertEquals("UpdatedModel", updatedAirport.get().model());

        testKey = updateAircraft.id();
    }

    @Test
    void delete() {
        Aircraft aircraft = new Aircraft("TEST");
        Aircraft savedAircraft = aircraftDao.save(aircraft);
        testKey = savedAircraft.id();

        boolean deleted = aircraftDao.delete(testKey);
        assertTrue(deleted);

        // Verify that the airport has been deleted
        Optional<Aircraft> deletedAirport = aircraftDao.findById(testKey);
        assertTrue(deletedAirport.isEmpty());
    }
}