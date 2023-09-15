package org.matveyvs.dao;

import org.matveyvs.entity.Aircraft;
import org.matveyvs.exception.DaoException;
import org.matveyvs.utils.ConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AircraftDao implements Dao<Long, Aircraft> {
    private static final AircraftDao INSTANCE = new AircraftDao();
    private static final String SAVE_SQL = """
            INSERT INTO aircraft
            (model) 
            VALUES (?)
            """;
    private static final String FIND_ALL_SQL = """
            SELECT 
            id, model
            FROM aircraft;
            """;
    private static final String FIND_BY_ID_SQL = """
            SELECT id, model
            FROM  aircraft WHERE id = ?;
            """;
    private static final String UPDATE_FLIGHT_BY_ID = """
            UPDATE aircraft
            SET  model = ?
            WHERE id = ?;
            """;
    private static final String DELETE_SQL = """
            DELETE FROM aircraft
            WHERE id = ?
            """;

    @Override
    public Aircraft save(Aircraft aircraft) {
        try (var connection = ConnectionManager.open();
             var statement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {
            setAircraftIntoStatement(aircraft, statement);
            statement.executeUpdate();
            var keys = statement.getGeneratedKeys();
            long id = 0;
            if (keys.next()) {
                id = keys.getLong("id");
            }
            return new Aircraft(id, aircraft.model());
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public List<Aircraft> findAll() {
        try (var connection = ConnectionManager.open();
             var statement = connection.prepareStatement(FIND_ALL_SQL)) {
            List<Aircraft> list = new ArrayList<>();
            var result = statement.executeQuery();
            while (result.next()) {
                list.add(buildAircraft(result));
            }
            return list;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public Optional<Aircraft> findById(Long id) {
        try (var connection = ConnectionManager.open();
             var statement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            statement.setLong(1, id);
            var result = statement.executeQuery();
            Aircraft aircraft = null;
            if (result.next()) {
                aircraft = buildAircraft(result);
            }
            return Optional.ofNullable(aircraft);
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public boolean update(Aircraft aircraft) {
        try (var connection = ConnectionManager.open();
             var statement = connection.prepareStatement(UPDATE_FLIGHT_BY_ID)) {
            setAircraftIntoStatement(aircraft, statement);
            statement.setLong(2, aircraft.id());
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

    private Aircraft buildAircraft(ResultSet result) throws SQLException {
        return new Aircraft(result.getLong("id"),
                result.getString("model"));
    }

    private static void setAircraftIntoStatement(Aircraft aircraft, PreparedStatement statement) throws SQLException {
        statement.setString(1, aircraft.model());
    }

    private AircraftDao() {

    }

    public static AircraftDao getInstance() {
        return AircraftDao.INSTANCE;
    }
}
