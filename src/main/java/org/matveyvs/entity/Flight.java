package org.matveyvs.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "flight", schema = "public")
public class  Flight{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "flight_no")
    private String flightNo;
    @Column(name = "departure_date")
    private LocalDateTime departureDate;
    @ManyToOne
    @JoinColumn(name = "departure_airport_code")
    private Airport departureAirport;
    @Column(name = "arrival_date")
    private LocalDateTime arrivalDate;
    @ManyToOne
    @JoinColumn(name = "arrival_airport_code")
    private Airport arrivalAirport;
    @ManyToOne
    @JoinColumn(name = "aircraft_id")
    private Aircraft aircraft;
    @Enumerated(EnumType.STRING)
    private FlightStatus status;
}
