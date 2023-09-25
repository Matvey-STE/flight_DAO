package org.matveyvs.dao;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.HibernateException;
import org.hibernate.cfg.Configuration;
import org.matveyvs.entity.Flight;

import javax.persistence.Query;
import java.util.List;
import java.util.Optional;

@Slf4j
public class FlightDao implements Dao<Long, Flight> {
    private static final FlightDao INSTANCE = new FlightDao();

    @Override
    public Flight save(Flight flight) {
        Configuration configuration = getConfiguration();
        Flight flightFromDb = null;
        try (var sessionFactory = configuration.buildSessionFactory();
             var session = sessionFactory.openSession()) {
            session.beginTransaction();
            Long objectId = (Long) session.save(flight);
            flightFromDb = session.get(Flight.class, objectId);
            session.getTransaction().commit();
            log.info("The entity {} was saved in database", flightFromDb);
            return flightFromDb;
        } catch (HibernateException e) {
            e.printStackTrace();
            log.error("An exception was thrown {}", e);
        }
        return flightFromDb;
    }

    @Override
    public List<Flight> findAll() {
        Configuration configuration = getConfiguration();
        List<Flight> flights = null;
        try (var sessionFactory = configuration.buildSessionFactory();
             var session = sessionFactory.openSession()) {
            session.beginTransaction();
            flights = session
                    .createQuery("select f from Flight f", Flight.class).list();
            session.getTransaction().commit();
            log.info("The entities size of {} was found in database", flights.size());
        } catch (HibernateException e) {
            e.printStackTrace();
            log.error("An exception was thrown {}", e);
        }
        return flights;
    }

    @Override
    public Optional<Flight> findById(Long id) {
        Configuration configuration = getConfiguration();
        Flight flight = null;
        try (var sessionFactory = configuration.buildSessionFactory();
             var session = sessionFactory.openSession()) {
            session.beginTransaction();
            Query query = session.createQuery("SELECT f FROM Flight f WHERE id = :id", Flight.class);
            query.setParameter("id", id);
            flight = (Flight) query.getSingleResult();
            session.getTransaction().commit();
            log.info("The entity {} was found in database", flight);
        } catch (HibernateException e) {
            e.printStackTrace();
            log.error("An exception was thrown {}", e);
        }
        return Optional.ofNullable(flight);
    }

    @Override
    public boolean update(Flight flight) {
        Configuration configuration = getConfiguration();
        try (var sessionFactory = configuration.buildSessionFactory();
             var session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.update(flight);
            session.getTransaction().commit();
            log.info("The entity {} was updated in database", flight);
            return true;
        } catch (HibernateException e) {
            e.printStackTrace();
            log.error("An exception was thrown {}", e);
            return false;
        }
    }

    @Override
    public boolean delete(Long id) {
        Configuration configuration = getConfiguration();
        try (var sessionFactory = configuration.buildSessionFactory();
             var session = sessionFactory.openSession()) {
            session.beginTransaction();
            Flight flight = session.get(Flight.class, id);
            session.delete(flight);
            session.getTransaction().commit();
            log.info("The entity {} was deleted from database", flight);
            return true;
        } catch (HibernateException e) {
            e.printStackTrace();
            log.error("An exception was thrown {}", e);
            return false;
        }
    }

    private static Configuration getConfiguration() {
        Configuration configuration = new Configuration();
        configuration.configure();
        return configuration;
    }

    private FlightDao() {
    }

    public static FlightDao getInstance() {
        return FlightDao.INSTANCE;
    }
}
