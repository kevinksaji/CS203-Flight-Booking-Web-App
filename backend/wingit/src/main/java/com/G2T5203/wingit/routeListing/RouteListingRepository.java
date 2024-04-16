package com.G2T5203.wingit.routeListing;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RouteListingRepository extends JpaRepository<RouteListing, RouteListingPk> {
    List<RouteListing> findByRouteListingPkRouteDepartureDest(String departureDest);
    List<RouteListing> findByRouteListingPkRouteDepartureDestAndRouteListingPkRouteArrivalDest(String departureDest, String arrivalDest);
}
