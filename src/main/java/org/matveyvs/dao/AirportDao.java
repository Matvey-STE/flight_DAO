package org.matveyvs.dao;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.HibernateException;
import org.hibernate.cfg.Configuration;
import org.matveyvs.entity.Airport;

import javax.persistence.Query;
import java.util.List;
import java.util.Optional;

@Slf4j
public class AirportDao implements Dao<String, Airport> {
    private static final AirportDao INSTANCE = new AirportDao();

    @Override
    public Airport save(Airport airport) {
        Configuration configuration = getConfiguration();
        Airport airportFromDb = null;
        try (var sessionFactory = configuration.buildSessionFactory();
             var session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.save(airport);
            airportFromDb = session.get(Airport.class, airport.getCode());
            session.getTransaction().commit();
            log.info("The entity {} was saved in database", airportFromDb);
        } catch (HibernateException e) {
            e.printStackTrace();
            log.error("An exception was thrown {}", e);
        }
        return airportFromDb;
    }

    @Override
    public List<Airport> findAll() {
        Configuration configuration = getConfiguration();
        List<Airport> airports = null;
        try (var sessionFactory = configuration.buildSessionFactory();
             var session = sessionFactory.openSession()) {
            session.beginTransaction();
            airports = session
                    .createQuery("select ap from Airport ap", Airport.class).list();
            session.getTransaction().commit();
            log.info("The entities size of {} was found in database", airports.size());
        } catch (HibernateException e) {
            e.printStackTrace();
            log.error("An exception was thrown {}", e);
        }
        return airports;
    }

    @Override
    public Optional<Airport> findById(String code) {
        Configuration configuration = getConfiguration();
        Airport airport = null;
        try (var sessionFactory = configuration.buildSessionFactory();
             var session = sessionFactory.openSession()) {
            session.beginTransaction();
            Query query = session
                    .createQuery("SELECT ap FROM Airport ap WHERE id = :code", Airport.class);
            query.setParameter("code", code);
            airport = (Airport) query.getSingleResult();
            session.getTransaction().commit();
            log.info("The entity {} was found in database", airport);
        } catch (HibernateException e) {
            e.printStackTrace();
            log.error("An exception was thrown {}", e);
        }
        return Optional.ofNullable(airport);
    }

    @Override
    public boolean update(Airport airport) {
        Configuration configuration = getConfiguration();
        try (var sessionFactory = configuration.buildSessionFactory();
             var session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.update(airport);
            session.getTransaction().commit();
            log.info("The entity {} was updated in database", airport);
            return true;
        } catch (HibernateException e) {
            e.printStackTrace();
            log.error("An exception was thrown {}", e);
            return false;
        }
    }

    @Override
    public boolean delete(String code) {
        Configuration configuration = getConfiguration();
        try (var sessionFactory = configuration.buildSessionFactory();
             var session = sessionFactory.openSession()) {
            session.beginTransaction();
            Airport airport = session.get(Airport.class, code);
            session.delete(airport);
            session.getTransaction().commit();
            log.info("The entity {} was deleted from database", airport);
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

    private AirportDao() {

    }

    public static AirportDao getInstance() {
        return AirportDao.INSTANCE;
    }
}
