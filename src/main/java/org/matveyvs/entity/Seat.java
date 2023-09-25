package org.matveyvs.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "seat", schema = "public")
public class Seat {
    @Id
    @Column(name = "aircraft_id")
    private Integer aircraftId;
    @Column(name = "seat_no")
    private String seatNo;
}
