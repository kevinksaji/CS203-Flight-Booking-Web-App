package com.G2T5203.wingit.seat;

import com.G2T5203.wingit.plane.Plane;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class SeatPk implements Serializable {
    @ManyToOne
    @JoinColumn(name = "planeId")
    private Plane plane;
    @NotEmpty @Size(min=3, max=3)
    private String seatNumber;

    public SeatPk(Plane plane, String seatNumber) {
        this.plane = plane;
        this.seatNumber = seatNumber;
    }

    public SeatPk() {}

    public Plane getPlane() {
        return plane;
    }

    public void setPlane(Plane plane) {
        this.plane = plane;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(String seatNumber) {
        this.seatNumber = seatNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SeatPk seatPk = (SeatPk) o;
        return Objects.equals(plane, seatPk.plane) && Objects.equals(seatNumber, seatPk.seatNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(plane, seatNumber);
    }
}
