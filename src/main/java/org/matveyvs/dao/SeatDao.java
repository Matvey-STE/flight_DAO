package org.matveyvs.dao;

import org.matveyvs.entity.Seat;
import org.matveyvs.exception.DaoException;
import org.matveyvs.utils.ConnectionManager;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SeatDao {
    private static final SeatDao INSTANCE = new SeatDao();
    private static final String SAVE_SQL = """
            INSERT INTO seat
            (aircraft_id, seat_no) 
            VALUES (?,?)
            """;
    private static final String FIND_ALL_SQL = """
            SELECT 
            aircraft_id, seat_no
            FROM seat;
            """;
    private static final String FIND_BY_SEAT_SQL = """
            SELECT aircraft_id, seat_no
            FROM seat 
            WHERE aircraft_id = ? AND seat_no = ?;
            """;
    private static final String UPDATE_FLIGHT_BY_ID = """
            UPDATE seat
            SET  seat_no = ?
            WHERE aircraft_id = ? AND seat_no = ?;
            """;
    private static final String DELETE_SQL = """
            DELETE FROM seat
            WHERE aircraft_id = ? AND seat_no = ?
            """;

    public Seat save(Seat seat) {
        try (var connection = ConnectionManager.open();
             var statement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, seat.aircraftId());
            statement.setString(2, seat.seatNo());
            statement.executeUpdate();
            var keys = statement.getGeneratedKeys();
            Integer aircraftId = null;
            String seatNo = null;
            while (keys.next()) {
                if (keys.getInt("aircraft_id") == seat.aircraftId() &&
                    keys.getString("seat_no").equals(seat.seatNo())) {
                    aircraftId = keys.getInt("aircraft_id");
                    seatNo = keys.getString("seat_no");
                }
            }
            return new Seat(aircraftId, seatNo);
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    public List<Seat> findAll() {
        try (var connection = ConnectionManager.open();
             var statement = connection.prepareStatement(FIND_ALL_SQL)) {
            List<Seat> list = new ArrayList<>();
            var result = statement.executeQuery();
            while (result.next()) {
                list.add(new Seat(result.getInt("aircraft_id"),
                        result.getString("seat_no")));
            }
            return list;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    public Optional<Seat> findById(Seat id) {
        try (var connection = ConnectionManager.open();
             var statement = connection.prepareStatement(FIND_BY_SEAT_SQL)) {
            statement.setInt(1, id.aircraftId());
            statement.setString(2, id.seatNo());
            var result = statement.executeQuery();
            Seat seat = null;
            while (result.next()) {
                if (result.getInt("aircraft_id") == id.aircraftId() &&
                    result.getString("seat_no").equals(id.seatNo())) {
                    seat = new Seat(result.getInt("aircraft_id"),
                            result.getString("seat_no"));
                }
            }
            return Optional.ofNullable(seat);
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    public boolean update(Seat seat, String seatNo) {
        try (var connection = ConnectionManager.open();
             var statement = connection.prepareStatement(UPDATE_FLIGHT_BY_ID)) {
            statement.setString(1, seatNo);
            statement.setInt(2, seat.aircraftId());
            statement.setString(3, seat.seatNo());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    public boolean delete(Seat seat) {
        try (var connection = ConnectionManager.open();
             var statement =
                     connection.prepareStatement(DELETE_SQL)) {
            statement.setInt(1, seat.aircraftId());
            statement.setString(2, seat.seatNo());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    private SeatDao() {

    }

    public static SeatDao getInstance() {
        return SeatDao.INSTANCE;
    }
}
