package com.G2T5203.wingit.seat;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;

@Entity
public class Seat {
    @EmbeddedId
    private SeatPk seatPk;
    @NotEmpty @Pattern(regexp = "First|Business|Economy", message = "Seat Class can only be First, Business, or Economy")
    private String seatClass;
    @DecimalMin(value = "1.0", message = "The smallest price factor is 1.0")
    private double priceFactor;

    public Seat(SeatPk seatPk, String seatClass, double priceFactor) {
        this.seatPk = seatPk;
        this.seatClass = seatClass;
        this.priceFactor = priceFactor;
    }

    public Seat() {
    }

    public SeatPk getSeatPk() {
        return seatPk;
    }

    public void setSeatPk(SeatPk seatPk) {
        this.seatPk = seatPk;
    }

    public String getSeatClass() {
        return seatClass;
    }

    public void setSeatClass(String seatClass) {
        this.seatClass = seatClass;
    }

    public double getPriceFactor() {
        return priceFactor;
    }

    public void setPriceFactor(double priceFactor) {
        this.priceFactor = priceFactor;
    }

    @Override
    public String toString() {
        return "Seat{" +
                "seatPk=" + seatPk +
                ", seatClass='" + seatClass + '\'' +
                ", priceFactor=" + priceFactor +
                '}';
    }
}
