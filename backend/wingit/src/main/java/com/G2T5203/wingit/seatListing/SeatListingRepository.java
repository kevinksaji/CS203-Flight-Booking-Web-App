package com.G2T5203.wingit.seatListing;

import com.G2T5203.wingit.routeListing.RouteListingPk;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeatListingRepository extends JpaRepository<SeatListing, SeatListingPk> {
    boolean existsBySeatListingPkRouteListingRouteListingPk(RouteListingPk routeListingPk);
    List<SeatListing> findBySeatListingPkRouteListingRouteListingPk(RouteListingPk routeListingPk);
    List<SeatListing> findBySeatListingPkRouteListingRouteListingPkAndBookingIsNull(RouteListingPk routeListingPk);
}
