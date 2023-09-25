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
import org.matveyvs.entity.Aircraft;
import org.matveyvs.entity.Airport;
import org.matveyvs.entity.Flight;
import org.matveyvs.entity.FlightStatus;

import javax.persistence.NoResultException;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class FlightDaoTest {
    private SessionFactory sessionFactory;
    private AircraftDao aircraftDao;
    private FlightDao flightDao;
    private static Aircraft aircraft;
    private Flight savedFlight;
    private Integer aircraftDbSize;
    private Integer flightDbSize;


    private String getResetFlightIdSql() {
        return "ALTER SEQUENCE flight_repo.public.flight_id_seq RESTART WITH " + flightDbSize;
    }

    private String getResetAircraftIdSql() {
        return "ALTER SEQUENCE flight_repo.public.aircraft_id_seq RESTART WITH " + aircraftDbSize;
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

    private static Airport getAirport() {
        return new Airport("MNK", "TestCountry", "TestCity");
    }

    private Aircraft getAircraft() {
        return Aircraft.builder().model("TEST").build();
    }


    @BeforeEach
    void setUp() {
        aircraftDao = AircraftDao.getInstance();
        flightDao = FlightDao.getInstance();
        aircraftDbSize = aircraftDao.findAll().size() + 1;
        flightDbSize = flightDao.findAll().size() + 1;

        aircraft = aircraftDao.save(getAircraft());
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
            flightDao.delete(savedFlight.getId());
        } catch (Exception e) {
            log.info("Entity was deleted earlier " + e);
        }
        aircraftDao.delete(aircraft.getId());
        if (sessionFactory != null) {
            try (Session session = sessionFactory.openSession()) {
                session.beginTransaction();
                NativeQuery<?> nativeQuery = session.createNativeQuery(getResetAircraftIdSql());
                nativeQuery.executeUpdate();
                NativeQuery<?> nativeQuery2 = session.createNativeQuery(getResetFlightIdSql());
                nativeQuery2.executeUpdate();
                session.getTransaction().commit();
            } catch (Exception e) {
                log.info("Information: " + e);
            } finally {
                sessionFactory.close();
            }
        }
    }


    @Test
    void save() {
        Flight testFlight = getFlight();
        savedFlight = flightDao.save(testFlight);

        assertNotNull(savedFlight);
        assertEquals(testFlight.getFlightNo(), savedFlight.getFlightNo());
        assertEquals(testFlight.getAircraft(), savedFlight.getAircraft());
        assertEquals(testFlight.getStatus(), savedFlight.getStatus());
    }

    @Test
    void findAll() {
        Flight testFlight = getFlight();
        savedFlight = flightDao.save(testFlight);

        List<Flight> flights = flightDao.findAll();

        assertNotNull(flights);
        assertTrue(flights.size() > 0);
    }

    @Test
    void findById() {
        Flight testFlight = getFlight();
        savedFlight = flightDao.save(testFlight);
        Optional<Flight> optionalAircraft = flightDao.findById(savedFlight.getId());

        assertTrue(optionalAircraft.isPresent());
        Flight flightFind = optionalAircraft.get();
        assertEquals(testFlight.getId(), flightFind.getId());
        assertEquals(testFlight.getArrivalDate(), flightFind.getArrivalDate());
        assertEquals(testFlight.getFlightNo(), flightFind.getFlightNo());
    }

    @Test
    void update() {
        Flight testFlight = getFlight();
        savedFlight = flightDao.save(testFlight);
        String flightNo = "update";
        savedFlight.setFlightNo(flightNo);

        boolean updated = flightDao.update(savedFlight);

        assertTrue(updated);
        Optional<Flight> findFlight = flightDao.findById(savedFlight.getId());
        assertTrue(findFlight.isPresent());
        assertEquals(flightNo, findFlight.get().getFlightNo());
    }

    @Test
    void delete() {
        Flight testFlight = getFlight();
        try {
            savedFlight = flightDao.save(testFlight);
            boolean deleted = flightDao.delete(savedFlight.getId());
            assertTrue(deleted);
            Optional<Flight> deletedAirport = flightDao.findById(savedFlight.getId());
            assertFalse(deletedAirport.isPresent());
        } catch (NoResultException e) {
            log.warn("Object not found after deletion" + e);
        }
    }
}
