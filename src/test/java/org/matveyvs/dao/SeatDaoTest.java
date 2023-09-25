package org.matveyvs.dao;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.matveyvs.entity.Seat;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
@Slf4j
class SeatDaoTest {
    private Seat savedSeat;
    private final SeatDao seatDao = SeatDao.getInstance();

    @BeforeEach
    void setUp() {

    }

    @AfterEach
    void tearDown() throws SQLException {
        try{
            boolean delete = seatDao.delete(savedSeat);
            log.info("The entity in tearDown" + delete);
        } catch (Exception e){
            log.info(e + "Entity is not presented");
        }
    }

    public Seat getTestSeat() {
        return new Seat(1, "Test");
    }

    @Test
    void save() {
        Seat seat = getTestSeat();
        savedSeat = seatDao.save(seat);

        assertNotNull(savedSeat);
        assertEquals(seat.getSeatNo(), savedSeat.getSeatNo());
    }

    @Test
    void findAll() {
        Seat seat = getTestSeat();
        savedSeat = seatDao.save(seat);

        List<Seat> airports = seatDao.findAll();

        assertNotNull(airports);
        assertTrue(airports.size() > 0);
    }

    @Test
    void findById() {
        Seat seat = getTestSeat();
        savedSeat = seatDao.save(seat);

        Optional<Seat> optionalSeat = seatDao.findBySeat(seat);

        assertTrue(optionalSeat.isPresent());
        assertEquals(seat.getSeatNo(), optionalSeat.get().getSeatNo());
    }

    @Test
    void update() {
        Seat seat = getTestSeat();
        savedSeat = seatDao.save(seat);

        String testSeat = "UPDT";

        boolean updated = seatDao.update(seat, testSeat);
        assertTrue(updated);
        // Verify that the airport details have been updated
        Optional<Seat> updatedSeat = seatDao.findBySeat(new Seat(seat.getAircraftId(),testSeat));
        assertTrue(updatedSeat.isPresent());
        assertEquals(testSeat, updatedSeat.get().getSeatNo());
        // to delete entity in tearDown method
        savedSeat.setSeatNo(testSeat);

    }

    @Test
    void delete() {
        Seat seat = getTestSeat();
        savedSeat = seatDao.save(seat);

        boolean deleted = seatDao.delete(savedSeat);
        assertTrue(deleted);
        try{
            Optional<Seat> deletedAirport = seatDao.findBySeat(savedSeat);
            assertTrue(deletedAirport.isEmpty());
        } catch (Exception e){
            log.warn(e + "The entity is not presented");
        }
    }
}
