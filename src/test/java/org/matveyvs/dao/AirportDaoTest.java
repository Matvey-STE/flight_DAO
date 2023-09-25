package org.matveyvs.dao;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.matveyvs.entity.Airport;

import javax.persistence.NoResultException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
@Slf4j
class AirportDaoTest {
    private final AirportDao airportDao = AirportDao.getInstance();
    private Airport savedAirport;
    private SessionFactory sessionFactory;

    @BeforeEach
    void setUp() {
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
    void tearDown(){
        if (sessionFactory != null) {
            Session session = sessionFactory.openSession();
            session.beginTransaction();

            session.delete(savedAirport);

            session.getTransaction().commit();
            sessionFactory.close();
        }
    }

    private static Airport getAirport() {
        Airport airport = new Airport("TST", "TestCountry", "TestCity");
        return airport;
    }

    @Test
    void save() {
        Airport airport = getAirport();
        savedAirport = airportDao.save(airport);
        assertNotNull(savedAirport);
        assertEquals(airport.getCode(), savedAirport.getCode());
        assertEquals(airport.getCountry(), savedAirport.getCountry());
        assertEquals(airport.getCity(), savedAirport.getCity());
    }

    @Test
    void findAllAirports() {
        Airport airport = getAirport();
        savedAirport = airportDao.save(airport);
        List<Airport> airports = airportDao.findAll();
        assertNotNull(airports);
        assertTrue(airports.size() > 0);
    }

    @Test
    void findAirportById() {
        Airport airport = getAirport();
        savedAirport = airportDao.save(airport);

        Optional<Airport> optionalAirport = airportDao.findById(savedAirport.getCode());
        assertTrue(optionalAirport.isPresent());
        Airport foundAirport = optionalAirport.get();
        assertEquals(airport.getCity(), foundAirport.getCity());
        assertEquals(airport.getCity(), foundAirport.getCity());
    }
    @Test
    void update() {
        Airport airport = getAirport();
        savedAirport = airportDao.save(airport);

        String testCity = "Belgrad";
        String testCountry = "Serbia";
        savedAirport.setCity(testCity);
        savedAirport.setCountry(testCountry);

        boolean updated = airportDao.update(savedAirport);
        assertTrue(updated);

        Optional<Airport> updatedAirport = airportDao.findById(savedAirport.getCode());
        assertTrue(updatedAirport.isPresent());
        assertEquals(testCity, updatedAirport.get().getCity());
        assertEquals(testCountry, updatedAirport.get().getCountry());
    }
    @Test
    void delete(){
        Airport airport = getAirport();
        savedAirport = airportDao.save(airport);

        boolean deleted = airportDao.delete(savedAirport.getCode());
        assertTrue(deleted);

        try {
            Optional<Airport> deletedAirport = airportDao.findById(savedAirport.getCode());
            assertFalse(deletedAirport.isPresent());
        } catch (NoResultException e) {
            log.warn("Airport not found after deletion" + e);
        }
    }

}
