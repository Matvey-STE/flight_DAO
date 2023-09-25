package org.matveyvs.dao;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.HibernateException;
import org.hibernate.cfg.Configuration;
import org.matveyvs.entity.Ticket;

import javax.persistence.Query;
import java.util.List;
import java.util.Optional;
@Slf4j
public class TicketDao implements Dao<Long, Ticket> {
    private static final TicketDao INSTANCE = new TicketDao();

    @Override
    public Ticket save(Ticket ticket) {
        Configuration configuration = getConfiguration();
        Ticket ticketFromDb = null;
        try (var sessionFactory = configuration.buildSessionFactory();
             var session = sessionFactory.openSession()) {
            session.beginTransaction();
            Long objectId = (Long) session.save(ticket);
            ticketFromDb = session.get(Ticket.class, objectId);
            session.getTransaction().commit();
            log.info("The entity {} was saved in database", ticketFromDb);
        } catch (HibernateException e) {
            e.printStackTrace();
        }
        return ticketFromDb;
    }

    @Override
    public List<Ticket> findAll() {
        Configuration configuration = getConfiguration();
        List<Ticket> tickets = null;
        try (var sessionFactory = configuration.buildSessionFactory();
             var session = sessionFactory.openSession()) {
            session.beginTransaction();
            tickets = session.createQuery("select t from Ticket t", Ticket.class).list();
            session.getTransaction().commit();
            log.info("The entities size of {} was found in database", tickets.size());
        } catch (HibernateException e) {
            e.printStackTrace();
        }
        return tickets;
    }

    @Override
    public Optional<Ticket> findById(Long id) {
        Configuration configuration = getConfiguration();
        Ticket ticket = null;
        try (var sessionFactory = configuration.buildSessionFactory();
             var session = sessionFactory.openSession()) {
            session.beginTransaction();
            Query query = session.createQuery("SELECT t FROM Ticket t WHERE id = :id", Ticket.class);
            query.setParameter("id", id);
            ticket = (Ticket) query.getSingleResult();
            session.getTransaction().commit();
            log.info("The entity {} was found in database", ticket);
        } catch (HibernateException e) {
            e.printStackTrace();
        }
        return Optional.ofNullable(ticket);
    }

    @Override
    public boolean update(Ticket ticket) {
        Configuration configuration = getConfiguration();
        try (var sessionFactory = configuration.buildSessionFactory();
             var session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.update(ticket);
            session.getTransaction().commit();
            log.info("The entity {} was updated in database", ticket);
            return true;
        } catch (HibernateException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(Long id) {
        Configuration configuration = getConfiguration();
        try (var sessionFactory = configuration.buildSessionFactory();
             var session = sessionFactory.openSession()) {
            session.beginTransaction();
            Ticket ticket = session.get(Ticket.class, id);
            session.delete(ticket);
            session.getTransaction().commit();
            log.info("The entity {} was deleted from database", ticket);
            return true;
        } catch (HibernateException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Ticket> findAllByFlightId(Long id) {
        Configuration configuration = getConfiguration();
        List<Ticket> tickets = null;
        try (var sessionFactory = configuration.buildSessionFactory();
             var session = sessionFactory.openSession()) {
            session.beginTransaction();
            tickets = session
                    .createQuery("select t from Ticket t where t.flight.id = :id", Ticket.class)
                    .setParameter("id", id)
                    .list();
            session.getTransaction().commit();
            log.info("The entities size {} were found in database by flightId", tickets.size());
            return tickets;
        } catch (HibernateException e) {
            e.printStackTrace();
        }
        return tickets;
    }

    private static Configuration getConfiguration() {
        Configuration configuration = new Configuration();
        configuration.configure();
        return configuration;
    }
    private TicketDao() {
    }

    public static TicketDao getInstance() {
        return TicketDao.INSTANCE;
    }
}
