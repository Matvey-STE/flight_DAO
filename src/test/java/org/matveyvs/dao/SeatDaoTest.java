package org.matveyvs.dao;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.matveyvs.entity.Seat;
import org.matveyvs.utils.ConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class SeatDaoTest {
    private Seat testDeleteBySeat;
    private SeatDao seatDao;
    private Connection connection;
    private static final String DELETE_SQL = """
            DELETE FROM seat
            WHERE aircraft_id = ? AND seat_no = ?
            """;

    @BeforeEach
    void setUp() {
        seatDao = seatDao.getInstance();
        connection = ConnectionManager.open();
    }

    @AfterEach
    void tearDown() throws SQLException {
        PreparedStatement statement = connection.prepareStatement(DELETE_SQL);
        statement.setInt(1, testDeleteBySeat.aircraftId());
        statement.setString(2, testDeleteBySeat.seatNo());
        statement.executeUpdate();
        connection.close();
    }

    @Test
    void save() {
        Seat seat = new Seat(1, "TEST");

        // Save the airport
        Seat savedSeat = seatDao.save(seat);

        assertNotNull(savedSeat);
        assertEquals("TEST", savedSeat.seatNo());

        testDeleteBySeat = new Seat(savedSeat.aircraftId(),savedSeat.seatNo());
    }

    @Test
    void findAll() {
        Seat seat = new Seat(1, "TEST");
        Seat savedSeat = seatDao.save(seat);

        List<Seat> airports = seatDao.findAll();

        assertNotNull(airports);
        assertTrue(airports.size() > 0);

        testDeleteBySeat = new Seat(savedSeat.aircraftId(),savedSeat.seatNo());
    }

    @Test
    void findById() {
        Seat seat = new Seat(1, "TEST");
        Seat savedSeat = seatDao.save(seat);

        Optional<Seat> optionalSeat = seatDao.findById(seat);

        assertTrue(optionalSeat.isPresent());
        assertEquals("TEST", seat.seatNo());

        testDeleteBySeat = new Seat(savedSeat.aircraftId(),savedSeat.seatNo());
    }

    @Test
    void update() {
        Seat seat = new Seat(1, "TEST");
        Seat savedSeat = seatDao.save(seat);

        String updateSeat = "UPDT";

        boolean updated = seatDao.update(seat, updateSeat);
        assertTrue(updated);
        // Verify that the airport details have been updated
        Optional<Seat> updatedSeat = seatDao.findById(new Seat(seat.aircraftId(),updateSeat));
        assertTrue(updatedSeat.isPresent());
        assertEquals("UPDT", updatedSeat.get().seatNo());

        testDeleteBySeat = updatedSeat.get();
    }

    @Test
    void delete() {
        Seat seat = new Seat(1, "TEST");
        Seat savedSeat = seatDao.save(seat);

        boolean deleted = seatDao.delete(savedSeat);
        assertTrue(deleted);

        Optional<Seat> deletedAirport = seatDao.findById(savedSeat);
        assertTrue(deletedAirport.isEmpty());

        testDeleteBySeat = new Seat(savedSeat.aircraftId(),savedSeat.seatNo());
    }
}