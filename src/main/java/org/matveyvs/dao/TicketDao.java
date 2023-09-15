package org.matveyvs.dao;

import org.matveyvs.entity.PassengerTicket;
import org.matveyvs.entity.Ticket;
import org.matveyvs.exception.DaoException;
import org.matveyvs.utils.ConnectionManager;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TicketDao implements Dao<Long, Ticket> {
    private static final TicketDao INSTANCE = new TicketDao();
    private static final FlightDao flightDao = FlightDao.getInstance();
    private static final String SAVE_SQL = """
            INSERT INTO ticket
            (passport_no, passenger_name, flight_id, seat_no, cost) 
            VALUES (?,?,?,?,?)
            """;
    private static final String FIND_ALL_SQL = """
            SELECT id, passport_no, passenger_name, flight_id, seat_no, cost 
            FROM ticket;
            """;
    private static final String FIND_BY_ID_SQL = """
            SELECT id, passport_no, passenger_name, flight_id, seat_no, cost 
            FROM  ticket WHERE id = ?;
            """;
    private static final String UPDATE_SQL = """
            UPDATE ticket
            SET passport_no = ?, 
            passenger_name = ?, 
            flight_id = ?, 
            seat_no = ?, 
            cost = ? 
            WHERE id = ?;
            """;
    private static final String DELETE_SQL = """
            DELETE FROM ticket
            WHERE id = ?
            """;
    private static final String MOST_FREQ_ENCOUNTER = """
            SELECT split_part(passenger_name,' ', 1) name
            FROM ticket
            GROUP BY split_part(passenger_name,' ', 1)
            ORDER BY count(split_part(passenger_name,' ', 1)) DESC
            LIMIT ?
            """;
    private static final String PASSENGER_TICKETS = """
            SELECT passenger_name name, count(passenger_name) tickets
            FROM ticket
            GROUP BY passenger_name
            ORDER BY count(split_part(passenger_name,' ', 1)) DESC
            LIMIT ?;
            """;
    private static final String UPDATE_FLIGHT_BY_ID = """
            UPDATE flight
            SET arrival_airport_code = ?
            WHERE id = ?;
            """;
    private static final String UPDATE_TICKET_BY_FLIGHT_ID = """
            UPDATE ticket
            SET passenger_name = ?
            WHERE flight_id = ?;
            """;

    @Override
    public Ticket save(Ticket ticket) {
        try (var connection = ConnectionManager.open();
             var statement =
                     connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {
            setTicketIntoStatement(ticket, statement);
            statement.executeUpdate();
            var keys = statement.getGeneratedKeys();
            long id = 0;
            if (keys.next()) {
                id = keys.getLong("id");
            }
            return new Ticket(id,
                    ticket.passportNo(),
                    ticket.passengerName(),
                    ticket.flight(),
                    ticket.seatNo(),
                    ticket.cost());

        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public List<Ticket> findAll() {
        try (var connection = ConnectionManager.open();
             var statement = connection.prepareStatement(FIND_ALL_SQL)) {
            List<Ticket> list = new ArrayList<>();
            var result = statement.executeQuery();
            while (result.next()) {
                list.add(buildTicket(result));
            }
            return list;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public Optional<Ticket> findById(Long id) {
        try (var connection = ConnectionManager.open();
             var statement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            statement.setLong(1, id);
            var result = statement.executeQuery();
            Ticket ticket = null;
            if (result.next()) {
                ticket = buildTicket(result);
            }
            return Optional.ofNullable(ticket);
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public boolean update(Ticket ticket) {
        try (var connection = ConnectionManager.open();
             var statement = connection.prepareStatement(UPDATE_SQL)) {
            setTicketIntoStatement(ticket, statement);
            statement.setLong(6, ticket.id());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public boolean delete(Long id) {
        try (var connection = ConnectionManager.open();
             var statement =
                     connection.prepareStatement(DELETE_SQL)) {
            statement.setLong(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    public List<String> mostFreqNames(Integer limit) {
        try (var connection = ConnectionManager.open();
             var statement = connection.prepareStatement(MOST_FREQ_ENCOUNTER)) {
            statement.setInt(1, limit);
            List<String> list = new ArrayList<>();
            var result = statement.executeQuery();
            while (result.next()) {
                list.add(result.getString("name"));
            }
            return list;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    public List<PassengerTicket> passengerTickets(Integer limit) {
        try (var connection = ConnectionManager.open();
             var statement = connection.prepareStatement(PASSENGER_TICKETS)) {
            statement.setInt(1, limit);
            List<PassengerTicket> list = new ArrayList<>();
            var result = statement.executeQuery();
            while (result.next()) {
                list.add(new PassengerTicket(result.getString("name"),
                        result.getInt("tickets")));
            }
            return list;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    public boolean updateTicketAndFlight(Long updateId, String newName, String newAirportCode) {
        try (var connection = ConnectionManager.open()) {
            connection.setAutoCommit(false);
            try {
                var statement = connection.prepareStatement(UPDATE_TICKET_BY_FLIGHT_ID);
                statement.setString(1, newName);
                statement.setLong(2, updateId);

                int ticketUpdate = statement.executeUpdate();

                statement = connection.prepareStatement(UPDATE_FLIGHT_BY_ID);
                statement.setString(1, newAirportCode);
                statement.setLong(2, updateId);

                int flightUpdate = statement.executeUpdate();

                connection.commit();

                return (flightUpdate + ticketUpdate) > 1;
            } catch (SQLException e) {
                connection.rollback();
                throw new DaoException(e);
            } finally {
                connection.setAutoCommit(true);
            }

        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    private static void setTicketIntoStatement(Ticket ticket, PreparedStatement statement) throws SQLException {
        statement.setString(1, ticket.passportNo());
        statement.setString(2, ticket.passengerName());
        statement.setLong(3, ticket.flight().id());
        statement.setString(4, ticket.seatNo());
        statement.setBigDecimal(5, ticket.cost());
    }

    private static Ticket buildTicket(ResultSet result) throws SQLException {
        return new Ticket(result.getLong("id"),
                result.getString("passport_no"),
                result.getString("passenger_name"),
                flightDao.findById(result.getLong("flight_id"),
                        result.getStatement().getConnection()
                ).orElse(null),
                result.getString("seat_no"),
                result.getBigDecimal("cost"));
    }

    private TicketDao() {
    }

    public static TicketDao getInstance() {
        return TicketDao.INSTANCE;
    }
}
