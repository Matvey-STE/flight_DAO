package org.matveyvs.entity;

public class PassengerTicket {
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
