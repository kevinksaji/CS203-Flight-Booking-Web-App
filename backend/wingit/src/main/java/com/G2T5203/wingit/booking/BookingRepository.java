package com.G2T5203.wingit.booking;

import com.G2T5203.wingit.routeListing.RouteListingPk;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer> {
    List<Booking> findAllByWingitUserUsername(String username);
    List<Booking> findAllByWingitUserUsernameNot(String username);
    List<Booking> findAllByOutboundRouteListingRouteListingPkAndIsPaidFalse(RouteListingPk routeListingPk);
    List<Booking> findAllByInboundRouteListingRouteListingPkAndIsPaidFalse(RouteListingPk routeListingPk);
}
