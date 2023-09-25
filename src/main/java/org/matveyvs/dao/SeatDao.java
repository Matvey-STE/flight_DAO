package org.matveyvs.dao;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.HibernateException;
import org.hibernate.cfg.Configuration;
import org.matveyvs.entity.Seat;

import javax.persistence.Query;
import java.util.List;
import java.util.Optional;
@Slf4j
public class SeatDao {
    private static final SeatDao INSTANCE = new SeatDao();

    public Seat save(Seat seat) {
        Configuration configuration = getConfiguration();
        try (var sessionFactory = configuration.buildSessionFactory();
             var session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.save(seat);
            session.getTransaction().commit();
            log.info("The entity {} was saved in database", seat);
        } catch (HibernateException e) {
            e.printStackTrace();
            log.error("An exception was thrown {}", e);
        }
        return seat;
    }

    public List<Seat> findAll() {
        Configuration configuration = getConfiguration();
        List<Seat> seats = null;
        try (var sessionFactory = configuration.buildSessionFactory();
             var session = sessionFactory.openSession()) {
            session.beginTransaction();
            seats = session
                    .createQuery("select s from Seat s", Seat.class).list();
            session.getTransaction().commit();
            log.info("The entities size of {} was found in database", seats.size());
        } catch (HibernateException e) {
            e.printStackTrace();
            log.error("An exception was thrown {}", e);
        }
        return seats;
    }

    public Optional<Seat> findBySeat(Seat seat) {
        Configuration configuration = getConfiguration();
        Seat seatDb = null;
        try (var sessionFactory = configuration.buildSessionFactory();
             var session = sessionFactory.openSession()) {
            session.beginTransaction();
            Query query = session
                    .createQuery("SELECT s FROM Seat s WHERE id = :id and seatNo = :seat", Seat.class);
            query.setParameter("id", seat.getAircraftId());
            query.setParameter("seat", seat.getSeatNo());
            seatDb = (Seat) query.getSingleResult();
            session.getTransaction().commit();
            log.info("The entity {} was found in database", seatDb);
        } catch (HibernateException e) {
            e.printStackTrace();
            log.error("An exception was thrown {}", e);
        }
        return Optional.ofNullable(seatDb);
    }

    public boolean update(Seat seat, String seatNo) {
        Configuration configuration = getConfiguration();
        try (var sessionFactory = configuration.buildSessionFactory();
             var session = sessionFactory.openSession()) {
            session.beginTransaction();
            Query query = session
                    .createQuery("UPDATE Seat s SET s.seatNo = :newSeatNo WHERE s.id = :id AND s.seatNo = :seat");
            query.setParameter("id", seat.getAircraftId());
            query.setParameter("seat", seat.getSeatNo());
            query.setParameter("newSeatNo", seatNo);
            int update = query.executeUpdate();
            session.getTransaction().commit();
            log.info("The entity {} was updated in database", seat);
            return update > 0;
        } catch (HibernateException e) {
            e.printStackTrace();
            log.error("An exception was thrown {}", e);
            return false;
        }
    }

    public boolean delete(Seat seat) {
        Configuration configuration = getConfiguration();
        try (var sessionFactory = configuration.buildSessionFactory();
             var session = sessionFactory.openSession()) {
            session.beginTransaction();
            Query query = session
                    .createQuery("DELETE FROM Seat s WHERE id = :id and seatNo = :seat");
            query.setParameter("id", seat.getAircraftId());
            query.setParameter("seat", seat.getSeatNo());
            int deletedCount = query.executeUpdate();
            session.getTransaction().commit();
            log.info("The entity {} was deleted from database", seat);
            return deletedCount > 0;
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

    private SeatDao() {

    }

    public static SeatDao getInstance() {
        return SeatDao.INSTANCE;
    }
}
