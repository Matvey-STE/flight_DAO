package org.matveyvs.dao;

import org.matveyvs.entity.Flight;
import org.matveyvs.exception.DaoException;
import org.matveyvs.utils.ConnectionManager;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

public class FlightsDao implements Dao<Long, Flight> {
    private static final FlightsDao INSTANCE = new FlightsDao();
    private static final String SAVE_SQL = """
            INSERT INTO ticket
            (passport_no, passenger_name, flight_id, seat_no, cost) 
            VALUES (?,?,?,?,?)
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
    public List<Flight> findAll() {
        return null;
    }

    @Override
    public Optional<Flight> findById(Long id) {
        return Optional.empty();
    }

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

    private static void setFlightIntoStatement(Flight flight, PreparedStatement statement) throws SQLException {
        statement.setString(1, flight.flightNo());
        statement.setTimestamp(2, Timestamp.valueOf(flight.departureDate()));
        statement.setString(3, flight.departureAirportCode());
        statement.setTimestamp(4, Timestamp.valueOf(flight.arrivalDate()));
        statement.setString(5, flight.arrivalAirportCode());
        statement.setInt(6, flight.aircraftId());
        statement.setString(7, String.valueOf(flight.status()));
    }

    @Override
    public boolean delete(Long id) {
        return false;
    }

    private FlightsDao() {
    }

    public static FlightsDao getInstance() {
        return FlightsDao.INSTANCE;
    }
}
