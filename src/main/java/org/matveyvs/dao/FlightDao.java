package org.matveyvs.dao;

import org.matveyvs.entity.Flight;
import org.matveyvs.entity.FlightStatus;
import org.matveyvs.exception.DaoException;
import org.matveyvs.utils.ConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FlightDao implements Dao<Long, Flight> {
    private static final FlightDao INSTANCE = new FlightDao();
    private static final String SAVE_SQL = """
            INSERT INTO flight
            (flight_no, departure_date, departure_airport_code, 
            arrival_date, arrival_airport_code, aircraft_id, status) 
            VALUES (?,?,?,?,?,?,?)
            """;
    private static final String FIND_ALL_SQL = """
            SELECT 
            id, flight_no, departure_date, departure_airport_code, 
            arrival_date, arrival_airport_code, aircraft_id, status
            FROM flight;
            """;
    private static final String FIND_BY_ID_SQL = """
            SELECT id, flight_no, departure_date, departure_airport_code,
            arrival_date, arrival_airport_code, aircraft_id, status
            FROM  flight WHERE id = ?;
            """;
    private static final String UPDATE_FLIGHT_BY_ID = """
            UPDATE flight
            SET  flight_no= ?, 
            departure_date = ?, 
            departure_airport_code = ?, 
            arrival_date = ?, 
            arrival_airport_code = ?, 
            aircraft_id = ?, 
            status = ? 
            WHERE id = ?;
            """;
    private static final String DELETE_SQL = """
            DELETE FROM flight
            WHERE id = ?
            """;


    @Override
    public Flight save(Flight flight) {
        try (var connection = ConnectionManager.open();
             var statement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {
            setFlightIntoStatement(flight, statement);
            statement.executeUpdate();
            var keys = statement.getGeneratedKeys();
            long id = 0;
            if (keys.next()) {
                id = keys.getLong("id");
            }
            return new Flight(id,
                    flight.flightNo(),
                    flight.departureDate(),
                    flight.departureAirportCode(),
                    flight.arrivalDate(),
                    flight.arrivalAirportCode(),
                    flight.aircraftId(),
                    flight.status());
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public List<Flight> findAll() {
        try (var connection = ConnectionManager.open();
             var statement = connection.prepareStatement(FIND_ALL_SQL)) {
            List<Flight> list = new ArrayList<>();
            var result = statement.executeQuery();
            while (result.next()) {
                list.add(buildFlight(result));
            }
            return list;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public Optional<Flight> findById(Long id) {
        try (var connectino = ConnectionManager.open()) {
            return findById(id, connectino);
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    public Optional<Flight> findById(Long id, Connection connection) {
        try (var statement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            statement.setLong(1, id);
            var result = statement.executeQuery();
            Flight flight = null;
            if (result.next()) {
                flight = buildFlight(result);
            }
            return Optional.ofNullable(flight);
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public boolean update(Flight flight) {
        try (var connection = ConnectionManager.open();
             var statement = connection.prepareStatement(UPDATE_FLIGHT_BY_ID)) {
            setFlightIntoStatement(flight, statement);
            statement.setLong(8, flight.id());
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

    private Flight buildFlight(ResultSet result) throws SQLException {
        return new Flight(result.getLong("id"),
                result.getString("flight_no"),
                result.getTimestamp("departure_date").toLocalDateTime(),
                result.getString("departure_airport_code"),
                result.getTimestamp("arrival_date").toLocalDateTime(),
                result.getString("arrival_airport_code"),
                result.getInt("aircraft_id"),
                FlightStatus.valueOf(result.getString("status")));
    }

    private static void setFlightIntoStatement(Flight flight, PreparedStatement statement) throws SQLException {
        statement.setString(1, flight.flightNo());
        statement.setTimestamp(2, Timestamp.valueOf(flight.departureDate()));
        statement.setString(3, flight.departureAirportCode());
        statement.setTimestamp(4, Timestamp.valueOf(flight.arrivalDate()));
        statement.setString(5, flight.arrivalAirportCode());
        statement.setInt(6, flight.aircraftId());
        statement.setString(7, String.valueOf(flight.status()));
    }

    private FlightDao() {
    }

    public static FlightDao getInstance() {
        return FlightDao.INSTANCE;
    }
}
