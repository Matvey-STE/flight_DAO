package org.matveyvs.dao;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.query.NativeQuery;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.matveyvs.entity.*;

import javax.persistence.NoResultException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
@Slf4j
class TicketDaoTest {
    private SessionFactory sessionFactory;
    private AircraftDao aircraftDao = AircraftDao.getInstance();
    private FlightDao flightDao = FlightDao.getInstance();
    private TicketDao ticketDao = TicketDao.getInstance();
    private static Aircraft aircraft;
    private static Flight savedFlight;
    private Ticket savedTicket;
    private Integer ticketDbSize;
    private Integer aircraftDbSize;
    private Integer flightDbSize;

    private String getResetIdTableSql() {
        return "ALTER SEQUENCE flight_repo.public.ticket_id_seq RESTART WITH " + ticketDbSize;
    }
    private String getResetFlightIdSql() {
        return "ALTER SEQUENCE flight_repo.public.flight_id_seq RESTART WITH " + flightDbSize;
    }
    private String getResetAircraftIdSql() {
        return "ALTER SEQUENCE flight_repo.public.aircraft_id_seq RESTART WITH " + aircraftDbSize;
    }

    @BeforeEach
    void setUp() {
        aircraft = aircraftDao.save(getAircraft());
        savedFlight = flightDao.save(getFlight());

        ticketDbSize = ticketDao.findAll().size() + 1;
        flightDbSize = flightDao.findAll().size() + 1;
        aircraftDbSize = aircraftDao.findAll().size() + 1;

        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure() // configures settings from hibernate.cfg.xml
                .build();
        try {
            sessionFactory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
        } catch (Exception e) {
            // The registry would be destroyed by the SessionFactory, but we had trouble building the SessionFactory
            // so destroy it manually.
            StandardServiceRegistryBuilder.destroy(registry);
        }
    }

    @AfterEach
    void tearDown() throws SQLException {
        try {
            ticketDao.delete(savedTicket.getId());
        } catch (Exception e){
            log.info("Entity was not found " + e);
        }
        flightDao.delete(savedFlight.getId());
        aircraftDao.delete(aircraft.getId());
        if (sessionFactory != null) {
            try (Session session = sessionFactory.openSession()) {
                session.beginTransaction();
                NativeQuery<?> nativeQuery = session.createNativeQuery(getResetAircraftIdSql());
                nativeQuery.executeUpdate();
                NativeQuery<?> nativeQuery2 = session.createNativeQuery(getResetFlightIdSql());
                nativeQuery2.executeUpdate();
                NativeQuery<?> nativeQuery3 = session.createNativeQuery(getResetIdTableSql());
                nativeQuery3.executeUpdate();
                session.getTransaction().commit();
            } catch (Exception e) {
                log.info("Information: " + e);
            } finally {
                sessionFactory.close();
            }
        }


    }
    private static Airport getAirport() {
        return new Airport("MNK", "TestCountry", "TestCity");
    }

    private Aircraft getAircraft() {
        return Aircraft.builder().model("TEST").build();
    }

    private static Flight getFlight() {
        return Flight.builder()
                .flightNo("Test")
                .departureDate(LocalDateTime.now())
                .departureAirport(getAirport())
                .arrivalDate(LocalDateTime.now())
                .arrivalAirport(getAirport())
                .aircraft(aircraft)
                .status(FlightStatus.SCHEDULED)
                .build();
    }
    private static Ticket getTicket(){
       return Ticket.builder()
                .passportNo("TestPassport")
                .passengerName("TestName")
                .flight(savedFlight)
                .seatNo("TEST")
                .cost(22.22)
                .build();
    }

    @Test
    void save() {
        Ticket ticket = getTicket();
        savedTicket = ticketDao.save(ticket);

        assertNotNull(savedTicket);
        assertEquals(ticket.getSeatNo(), savedTicket.getSeatNo());
        assertEquals(ticket.getPassengerName(), savedTicket.getPassengerName());
        assertEquals(ticket.getCost(), savedTicket.getCost());
    }

    @Test
    void findAll() {
        Ticket ticket = getTicket();
        savedTicket = ticketDao.save(ticket);

        List<Ticket> tickets = ticketDao.findAll();

        assertNotNull(tickets);
        assertTrue(tickets.size() > 0);
    }

    @Test
    void findById() {
        Ticket ticket = getTicket();
        savedTicket = ticketDao.save(ticket);

        Optional<Ticket> optionalTicket = ticketDao.findById(savedTicket.getId());
        assertTrue(optionalTicket.isPresent());
        Ticket ticketFind = optionalTicket.get();
        assertEquals(savedTicket.getPassportNo(), ticketFind.getPassportNo());
        assertEquals(savedTicket.getPassengerName(), ticketFind.getPassengerName());
        assertEquals(savedTicket.getSeatNo(), ticketFind.getSeatNo());
        assertEquals(savedTicket.getCost(), ticketFind.getCost());
    }

    @Test
    void update() {
        Ticket ticket = getTicket();
        savedTicket = ticketDao.save(ticket);

        String updatePassenger = "UpdateSeat";

        savedTicket.setPassengerName(updatePassenger);

        boolean updated = ticketDao.update(savedTicket);
        assertTrue(updated);

        Optional<Ticket> newTicket = ticketDao.findById(savedTicket.getId());
        assertTrue(newTicket.isPresent());
        Ticket ticketFind = newTicket.get();
        assertEquals(updatePassenger, ticketFind.getPassengerName());
    }

    @Test
    void delete() {
        Ticket ticket = getTicket();
        try {
            savedTicket = ticketDao.save(ticket);
            boolean deleted = ticketDao.delete(savedTicket.getId());
            assertTrue(deleted);

            Optional<Ticket> deletedAirport = ticketDao.findById(savedTicket.getId());
            assertFalse(deletedAirport.isPresent());
        } catch (NoResultException e) {
            log.warn("Object not found after deletion" + e);
        }
    }
}
