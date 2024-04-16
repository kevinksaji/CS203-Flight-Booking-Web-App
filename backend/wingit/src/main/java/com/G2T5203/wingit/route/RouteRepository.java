package com.G2T5203.wingit.route;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RouteRepository extends JpaRepository<Route, Integer> {
    List<Route> findAllByDepartureDest(String departureDest);
    List<Route> findAllByArrivalDest(String arrivalDest);
    List<Route> findAllByDepartureDestAndArrivalDest(String departureDest, String arrivalDest);
}
