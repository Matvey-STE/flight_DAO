package org.matveyvs;

import org.matveyvs.dao.TaskDao;
import java.sql.SQLException;

public class JdbcRunner {
    public static void main(String[] args) throws SQLException {
        TaskDao taskDao = TaskDao.getInstance();
//        System.out.println(taskDao.mostFreqNames(5));
//        System.out.println(taskDao.passengerTickets(5));

//        Before update

/*      select id, flight_no, arrival_airport_code
        from flight
        where id = 1;*/

/*      +--+---------+--------------------+
        |id|flight_no|arrival_airport_code|
        +--+---------+--------------------+
        |1 |MN3002   |LDN                 |
        +--+---------+--------------------+*/

/*      select id, flight_id, passenger_name
        from ticket
        where flight_id = 1;*/

/*      +--+---------+------------------+
        |id|flight_id|passenger_name    |
        +--+---------+------------------+
        |1 |1        |Иван Иванов       |
        |2 |1        |Петр Петров       |
        |3 |1        |Светлана Светикова|
        |4 |1        |Андрей Андреев    |
        |5 |1        |Иван Кожемякин    |
        |6 |1        |Олег Рубцов       |
        +--+---------+------------------+*/

//        Update name to 'Test update' and airport_code to 'BSL'

        taskDao.updateTicketAndFlight(1L, "Test update", "BSL");

//        After update

/*      +--+---------+--------------------+
        |id|flight_no|arrival_airport_code|
        +--+---------+--------------------+
        |1 |MN3002   |BSL                 |
        +--+---------+--------------------+*/

/*      +--+---------+--------------+
        |id|flight_id|passenger_name|
        +--+---------+--------------+
        |1 |1        |Test update   |
        |2 |1        |Test update   |
        |3 |1        |Test update   |
        |4 |1        |Test update   |
        |5 |1        |Test update   |
        |6 |1        |Test update   |
        +--+---------+--------------+*/
    }
}
