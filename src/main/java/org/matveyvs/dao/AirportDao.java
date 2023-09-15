package org.matveyvs.dao;

import org.matveyvs.entity.Airport;
import org.matveyvs.exception.DaoException;
import org.matveyvs.utils.ConnectionManager;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AirportDao implements Dao<String, Airport> {
    private static final AirportDao INSTANCE = new AirportDao();
    private static final String SAVE_SQL = """
            INSERT INTO airport
            (code,country, city) 
            VALUES (?,?,?)
            """;
    private static final String FIND_ALL_SQL = """
            SELECT 
            code, country, city
            FROM airport;
            """;
    private static final String FIND_BY_ID_SQL = """
            SELECT code, country, city
            FROM  airport WHERE code = ?;
            """;
    private static final String UPDATE_FLIGHT_BY_ID = """
            UPDATE airport
            SET  country = ?,
                 city = ?
            WHERE code = ?;
            """;
    private static final String DELETE_SQL = """
            DELETE FROM airport
            WHERE code = ?
            """;

    @Override
    public Airport save(Airport airport) {
        try (var connection = ConnectionManager.open();
             var statement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {
            setAirportIntoStatement(airport, statement);
            statement.executeUpdate();
            var keys = statement.getGeneratedKeys();
            String code = null;
            if (keys.next()) {
                code = keys.getString("code");
            }
            return new Airport(code, airport.country(), airport.city());
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public List<Airport> findAll() {
        try (var connection = ConnectionManager.open();
             var statement = connection.prepareStatement(FIND_ALL_SQL)) {
            List<Airport> list = new ArrayList<>();
            var result = statement.executeQuery();
            while (result.next()) {
                list.add(buildAirport(result));
            }
            return list;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public Optional<Airport> findById(String code) {
        try (var connection = ConnectionManager.open();
             var statement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            statement.setString(1, code);
            var result = statement.executeQuery();
            Airport airport = null;
            if (result.next()) {
                airport = buildAirport(result);
            }
            return Optional.ofNullable(airport);
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public boolean update(Airport airport) {
        try (var connection = ConnectionManager.open();
             var statement = connection.prepareStatement(UPDATE_FLIGHT_BY_ID)) {
            statement.setString(1, airport.country());
            statement.setString(2, airport.city());
            statement.setString(3, airport.code());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public boolean delete(String code) {
        try (var connection = ConnectionManager.open();
             var statement =
                     connection.prepareStatement(DELETE_SQL)) {
            statement.setString(1, code);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    private Airport buildAirport(ResultSet result) throws SQLException {
        return new Airport(result.getString("code"),
                result.getString("country"),
                result.getString("city"));
    }

    private static void setAirportIntoStatement(Airport airport, PreparedStatement statement) throws SQLException {
        statement.setString(1, airport.code());
        statement.setString(2, airport.country());
        statement.setString(3, airport.city());
    }

    private AirportDao() {

    }

    public static AirportDao getInstance() {
        return AirportDao.INSTANCE;
    }
}
