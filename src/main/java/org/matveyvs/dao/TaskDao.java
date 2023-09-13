package org.matveyvs.dao;

import org.matveyvs.entity.Ticket;
import org.matveyvs.exception.DaoException;
import org.matveyvs.utils.ConnectionManager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class TaskDao {
    private static final TaskDao INSTANCE = new TaskDao();
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

    private static Ticket buildTicket(ResultSet result) throws SQLException {
        return new Ticket(result.getLong("id"),
                result.getString("passport_no"),
                result.getString("passenger_name"),
                result.getLong("flight_id"),
                result.getString("seat_no"),
                result.getBigDecimal("cost"));
    }

    private TaskDao() {

    }

    public static TaskDao getInstance() {
        return TaskDao.INSTANCE;
    }
}

class PassengerTicket {
    private String name;
    private Integer tickets;

    public PassengerTicket(String name, Integer tickets) {
        this.name = name;
        this.tickets = tickets;
    }

    @Override
    public String toString() {
        return "PassengerTicket{" +
               "name='" + name + '\'' +
               ", tickets=" + tickets +
               '}';
    }
}
