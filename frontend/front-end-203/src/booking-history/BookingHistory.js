import React from "react";
import NavBar from "../nav-bar/NavigationBar";
import BookingSummary from "./BookingSummary";
import Banner from "./Banner";



const BookingHistory = () => {

    

  
  return (
    <div>
      <div className="nav">
        <NavBar />
      </div>

      <Banner currentStep={"Booking History "} />

      <BookingSummary />

      </div>
  );
};

export default BookingHistory;
