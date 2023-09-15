package org.matveyvs;

import org.matveyvs.dao.TicketDao;

import java.sql.SQLException;

public class JdbcRunner {
    public static void main(String[] args) throws SQLException {
        TicketDao ticketDao = TicketDao.getInstance();
        System.out.println(ticketDao.passengerTickets(5));
        System.out.println(ticketDao.mostFreqNames(5));
        boolean test = ticketDao.updateTicketAndFlight(1L, "TEST2", "BSL");
        System.out.println(test);
    }
}
