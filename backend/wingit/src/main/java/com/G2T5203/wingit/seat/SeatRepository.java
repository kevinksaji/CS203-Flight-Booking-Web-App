package com.G2T5203.wingit.seat;

import com.G2T5203.wingit.route.Route;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, SeatPk> {

    public List<Seat> findAllBySeatPkPlanePlaneId(String planeId);
    public boolean existsBySeatPkPlanePlaneId(String planeId);
}
