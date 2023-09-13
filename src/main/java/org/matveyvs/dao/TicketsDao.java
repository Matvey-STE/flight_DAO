package org.matveyvs.dao;

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

public class TicketsDao implements Dao<Long, Ticket> {
    private static final TicketsDao INSTANCE = new TicketsDao();
    private static final String SAVE_SQL = """
            INSERT INTO ticket
            (passport_no, passenger_name, flight_id, seat_no, cost) 
            VALUES (?,?,?,?,?)
            """;
    private static final String DELETE_SQL = """
            DELETE FROM ticket
            WHERE id = ?
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

    private static Ticket buildTicket(ResultSet result) throws SQLException {
        return new Ticket(result.getLong("id"),
                result.getString("passport_no"),
                result.getString("passenger_name"),
                result.getLong("flight_id"),
                result.getString("seat_no"),
                result.getBigDecimal("cost"));
    }
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
                    ticket.flightId(),
                    ticket.seatNo(),
                    ticket.cost());

        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    private static void setTicketIntoStatement(Ticket ticket, PreparedStatement statement) throws SQLException {
        statement.setString(1, ticket.passportNo());
        statement.setString(2, ticket.passengerName());
        statement.setLong(3, ticket.flightId());
        statement.setString(4, ticket.seatNo());
        statement.setBigDecimal(5, ticket.cost());
    }

    @Override
    public List<Ticket> findAll() {
        try(var connection = ConnectionManager.open();
            var statement = connection.prepareStatement(FIND_ALL_SQL)) {
            List<Ticket> list = new ArrayList<>();
             var result= statement.executeQuery();
             while (result.next()){
                 list.add(buildTicket(result));
             }
             return list;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public Optional<Ticket> findById(Long id) {
        try(var connection = ConnectionManager.open();
        var statement = connection.prepareStatement(FIND_BY_ID_SQL)){
            statement.setLong(1, id);
            var result = statement.executeQuery();
            Ticket ticket = null;
            if (result.next()){
                ticket = buildTicket(result);
            }
            return Optional.ofNullable(ticket);
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public boolean update(Ticket ticket) {
        try(var connection = ConnectionManager.open();
        var statement = connection.prepareStatement(UPDATE_SQL)){
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

    private TicketsDao() {
    }

    public static TicketsDao getInstance() {
        return TicketsDao.INSTANCE;
    }
}
