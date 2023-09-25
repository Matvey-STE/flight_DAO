package org.matveyvs.dao;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.HibernateException;
import org.hibernate.cfg.Configuration;
import org.matveyvs.entity.Aircraft;

import javax.persistence.Query;
import java.util.List;
import java.util.Optional;

@Slf4j
public class AircraftDao implements Dao<Long, Aircraft> {
    private static final AircraftDao INSTANCE = new AircraftDao();

    @Override
    public Aircraft save(Aircraft aircraft) {
        Configuration configuration = getConfiguration();
        Aircraft aircraftFromDb = null;
        try (var sessionFactory = configuration.buildSessionFactory();
             var session = sessionFactory.openSession()) {
            session.beginTransaction();
            Long objectId = (Long) session.save(aircraft);
            aircraftFromDb = session.get(Aircraft.class, objectId);
            session.getTransaction().commit();
            log.info("The entity {} was saved in database", aircraftFromDb);
        } catch (HibernateException e) {
            e.printStackTrace();
            log.error("An exception was thrown {}", e);
        }
        return aircraftFromDb;
    }

    @Override
    public List<Aircraft> findAll() {
        Configuration configuration = getConfiguration();
        List<Aircraft> aircrafts = null;
        try (var sessionFactory = configuration.buildSessionFactory();
             var session = sessionFactory.openSession()) {
            session.beginTransaction();
            aircrafts = session.createQuery("select a from Aircraft a", Aircraft.class).list();
            session.getTransaction().commit();
            log.info("The entities size of {} was found in database", aircrafts.size());
        } catch (HibernateException e) {
            e.printStackTrace();
            log.error("An exception was thrown {}", e);
        }
        return aircrafts;
    }

    @Override
    public Optional<Aircraft> findById(Long id) {
        Configuration configuration = getConfiguration();
        Aircraft aircraft = null;
        try (var sessionFactory = configuration.buildSessionFactory();
             var session = sessionFactory.openSession()) {
            session.beginTransaction();
            Query query = session.createQuery("SELECT a FROM Aircraft a WHERE id = :id", Aircraft.class);
            query.setParameter("id", id);
            aircraft = (Aircraft) query.getSingleResult();
            session.getTransaction().commit();
            log.info("The entity {} was found in database", aircraft);
        } catch (HibernateException e) {
            e.printStackTrace();
            log.error("An exception was thrown {}", e);
        }
        return Optional.ofNullable(aircraft);
    }

    @Override
    public boolean update(Aircraft aircraft) {
        Configuration configuration = getConfiguration();
        try (var sessionFactory = configuration.buildSessionFactory();
             var session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.update(aircraft);
            session.getTransaction().commit();
            log.info("The entity {} was updated in database", aircraft);
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
            Aircraft aircraft = session.get(Aircraft.class, id);
            session.delete(aircraft);
            session.getTransaction().commit();
            log.info("The entity {} was deleted from database", aircraft);
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

    private AircraftDao() {

    }

    public static AircraftDao getInstance() {
        return AircraftDao.INSTANCE;
    }
}
