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

import javax.persistence.NoResultException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class AircraftDaoTest {
    private final AircraftDao aircraftDao = AircraftDao.getInstance();
    private Aircraft savedAircraft;
    private SessionFactory sessionFactory;
    private int dBSize;

    private String getResetIdTableSql() {
        return "ALTER SEQUENCE flight_repo.public.aircraft_id_seq RESTART WITH " + dBSize;
    }

    @BeforeEach
    void setUp() {
        dBSize = aircraftDao.findAll().size() + 1;
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
    void tearDown() {
        if (sessionFactory != null) {
            try (Session session = sessionFactory.openSession()) {
                session.beginTransaction();

                session.delete(savedAircraft);

                NativeQuery<?> nativeQuery = session.createNativeQuery(getResetIdTableSql());
                nativeQuery.executeUpdate();

                session.getTransaction().commit();
            } catch (Exception e) {
                log.warn("Information: " + e);
            } finally {
                sessionFactory.close();
            }
        }
    }

    private static Aircraft getAircraft() {
        return Aircraft.builder().model("TEST").build();
    }

    @Test
    void save() {
        Aircraft aircraft = getAircraft();
        savedAircraft = aircraftDao.save(aircraft);

        assertNotNull(savedAircraft);
        assertEquals(aircraft.getModel(), savedAircraft.getModel());
    }

    @Test
    void findAll() {
        Aircraft aircraft = getAircraft();
        savedAircraft = aircraftDao.save(aircraft);

        List<Aircraft> aircrafts = aircraftDao.findAll();
        assertNotNull(aircrafts);
        assertTrue(aircrafts.size() > 0);
    }

    @Test
    void findById() {
        Aircraft aircraft = getAircraft();
        savedAircraft = aircraftDao.save(aircraft);
        Optional<Aircraft> optionalAircraft = aircraftDao.findById(savedAircraft.getId());
        assertTrue(optionalAircraft.isPresent());
        Aircraft aircraftFind = optionalAircraft.get();
        assertEquals(aircraft.getModel(), aircraftFind.getModel());
    }

    @Test
    void update() {
        Aircraft aircraft = getAircraft();
        savedAircraft = aircraftDao.save(aircraft);

        String updateModel = "UPDATE";
        savedAircraft.setModel(updateModel);

        boolean updated = aircraftDao.update(savedAircraft);
        assertTrue(updated);

        Optional<Aircraft> updatedAirport = aircraftDao.findById(savedAircraft.getId());
        assertTrue(updatedAirport.isPresent());
        assertEquals(updateModel, updatedAirport.get().getModel());
    }

    @Test
    void delete() {
        Aircraft aircraft = getAircraft();
        savedAircraft = aircraftDao.save(aircraft);
        // Verify that the airport has been deleted
        try {
            boolean deleted = aircraftDao.delete(savedAircraft.getId());
            assertTrue(deleted);
            Optional<Aircraft> deletedAirport = aircraftDao.findById(savedAircraft.getId());
            assertFalse(deletedAirport.isPresent());
        } catch (NoResultException e) {
            log.warn("Aircraft not found after deletion" + e);
        }
    }
}