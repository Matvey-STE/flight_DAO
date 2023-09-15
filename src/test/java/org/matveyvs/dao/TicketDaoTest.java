package org.matveyvs.dao;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.matveyvs.entity.Flight;
import org.matveyvs.entity.FlightStatus;
import org.matveyvs.entity.Ticket;
import org.matveyvs.utils.ConnectionManager;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class TicketDaoTest {
    private Flight testFlight;
    private static long newIdPointToReset;
    private static long newFlightIdPointToReset;
    private long testKey;
    private long flightTestKey;
    private TicketDao ticketDao;
    private FlightDao flightDao;
    private Connection connection;
    private static final String DELETE_SQL = """
            DELETE FROM ticket
            WHERE id = ?
            """;
    private static final String DELETE_FLIGHT_SQL = """
            DELETE FROM flight
            WHERE id = ?
            """;

    private String getResetIdTableSql() {
        return "ALTER SEQUENCE flight_repo.public.ticket_id_seq RESTART WITH " + newIdPointToReset;
    }
    private String getFlightSqlResetIdTableSql() {
        return "ALTER SEQUENCE flight_repo.public.flight_id_seq RESTART WITH " + newFlightIdPointToReset;
    }

    @BeforeEach
    void setUp() throws SQLException {
        flightDao = FlightDao.getInstance();
        ticketDao = TicketDao.getInstance();
        connection = ConnectionManager.open();
        testFlight = flightDao.save(new Flight
                ("FL123", LocalDateTime.now(),
                        "MNK", LocalDateTime.now(),
                        "MNK", 1, FlightStatus.DEPARTED));

        newIdPointToReset = ticketDao.findAll().size() + 1;
        newFlightIdPointToReset = flightDao.findAll().size() + 1;
    }

    @AfterEach
    void tearDown() throws SQLException {
        PreparedStatement statement = connection.prepareStatement(DELETE_SQL);
        statement.setLong(1, testKey);
        statement.executeUpdate();

        PreparedStatement flightStatement = connection.prepareStatement(DELETE_FLIGHT_SQL);
        flightStatement.setLong(1, flightTestKey);
        flightStatement.executeUpdate();


        Statement resetFlightStatementId = connection.createStatement();
        resetFlightStatementId.execute(getFlightSqlResetIdTableSql());

        Statement resetStatementId = connection.createStatement();
        resetStatementId.execute(getResetIdTableSql());

        connection.close();
    }

    @Test
    void save() {
        Ticket ticket = new Ticket
                ("TestSeat", "TestName",
                        testFlight, "TEST", BigDecimal.valueOf(22.22));
        Ticket savedTicket = ticketDao.save(ticket);

        assertNotNull(savedTicket);
        assertEquals("TestSeat", savedTicket.passportNo());
        assertEquals("TestName", savedTicket.passengerName());
        assertEquals(testFlight, savedTicket.flight());
        assertEquals("TEST", savedTicket.seatNo());
        assertEquals(BigDecimal.valueOf(22.22), savedTicket.cost());

        //set key to delete after tests
        testKey = savedTicket.id();
        flightTestKey = testFlight.id();
    }

    @Test
    void findAll() {
        Ticket ticket = new Ticket
                ("TestSeat", "TestName",
                        testFlight, "TEST", BigDecimal.valueOf(22.22));
        Ticket savedTicket = ticketDao.save(ticket);

        List<Ticket> tickets = ticketDao.findAll();

        assertNotNull(tickets);
        assertTrue(tickets.size() > 0);
        testKey = savedTicket.id();
        flightTestKey = testFlight.id();
    }

    @Test
    void findById() {
        Ticket ticket = new Ticket
                ("TestSeat", "TestName",
                        testFlight, "TEST", BigDecimal.valueOf(22.22));
        Ticket savedTicket = ticketDao.save(ticket);

        Optional<Ticket> optionalTicket = ticketDao.findById(savedTicket.id());

        assertTrue(optionalTicket.isPresent());
        Ticket ticketFind = optionalTicket.get();
        assertEquals("TestSeat", ticketFind.passportNo());
        assertEquals("TestName", ticketFind.passengerName());
        assertEquals(testFlight, ticketFind.flight());
        assertEquals("TEST", ticketFind.seatNo());
        assertEquals(BigDecimal.valueOf(22.22), ticketFind.cost());
        testKey = ticketFind.id();
        flightTestKey = testFlight.id();
    }

    @Test
    void update() {
        Ticket ticket = new Ticket
                ("TestSeat", "TestName",
                        testFlight, "TEST", BigDecimal.valueOf(22.22));
        Ticket savedTicket = ticketDao.save(ticket);

        Ticket updateTicket = new Ticket
                (savedTicket.id(),"UpdateSeat", "UpdateName",
                        testFlight, "TEST", BigDecimal.valueOf(33.33));

        boolean updated = ticketDao.update(updateTicket);
        assertTrue(updated);

        Optional<Ticket> newTicket = ticketDao.findById(updateTicket.id());
        assertTrue(newTicket.isPresent());
        Ticket ticketFind = newTicket.get();
        assertEquals("UpdateSeat", ticketFind.passportNo());
        assertEquals("UpdateName", ticketFind.passengerName());
        assertEquals(testFlight, ticketFind.flight());
        assertEquals("TEST", ticketFind.seatNo());
        assertEquals(BigDecimal.valueOf(33.33), ticketFind.cost());
        testKey = ticketFind.id();
        flightTestKey = testFlight.id();
    }

    @Test
    void delete() {
        Ticket ticket = new Ticket
                ("TestSeat", "TestName",
                        testFlight, "TEST", BigDecimal.valueOf(22.22));
        Ticket savedTicket = ticketDao.save(ticket);
        testKey = savedTicket.id();

        boolean deleted = ticketDao.delete(testKey);
        assertTrue(deleted);

        // Verify that the airport has been deleted
        Optional<Ticket> deletedAirport = ticketDao.findById(testKey);
        assertTrue(deletedAirport.isEmpty());
        testKey = savedTicket.id();
        flightTestKey = testFlight.id();
    }
}