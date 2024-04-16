package com.G2T5203.wingit.plane;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.Length;

@Entity
public class Plane {
    @Id
    private String planeId;
    @Min(value = 4, message = "Plane capacity cannot be less than 4")
    @Max(value = 1000, message = "Plane capacity cannot be more than 1000")
    private int capacity;
    @NotEmpty @Size(min=3, max=4, message = "The model must be between 3 and 4 characters long")
    private String model;

    public Plane(String planeId, int capacity, String model) {
        this.planeId = planeId;
        this.capacity = capacity;
        this.model = model;
    }

    public Plane() {

    }

    public String getPlaneId() {
        return planeId;
    }

    public void setPlaneId(String planeId) {
        this.planeId = planeId;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    @Override
    public String toString() {
        return "Plane{" +
                "planeId='" + planeId + '\'' +
                ", capacity=" + capacity +
                ", model='" + model + '\'' +
                '}';
    }
}
