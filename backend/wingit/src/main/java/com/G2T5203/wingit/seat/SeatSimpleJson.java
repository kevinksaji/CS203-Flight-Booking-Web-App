package com.G2T5203.wingit.seat;

public class SeatSimpleJson {
    private String planeId;
    private String seatNumber;
    private String seatClass;
    private double priceFactor;

    public SeatSimpleJson(String planeId, String seatNumber, String seatClass, double priceFactor) {
        this.planeId = planeId;
        this.seatNumber = seatNumber;
        this.seatClass = seatClass;
        this.priceFactor = priceFactor;
    }
    public SeatSimpleJson(Seat seat) {
        this(
                seat.getSeatPk().getPlane().getPlaneId(),
                seat.getSeatPk().getSeatNumber(),
                seat.getSeatClass(),
                seat.getPriceFactor());
    }
    public SeatSimpleJson() {};

    public String getPlaneId() {
        return planeId;
    }

    public void setPlaneId(String planeId) {
        this.planeId = planeId;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(String seatNumber) {
        this.seatNumber = seatNumber;
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
}
