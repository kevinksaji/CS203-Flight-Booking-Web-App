import React from "react";
import { Button } from "@mui/material";
import ProgressBar from "../progress-bar/ProgressBar";
import PassengerForm from "./passenger-form/PassengerForm";
import NavBar from "../nav-bar/NavigationBar"; // Import the Navbar component
import {useLocation, useNavigate} from "react-router-dom";


const PassengerDetails = () => {
    const location = useLocation();
    const {inboundSeats, outboundSeats, numGuest, departureFlight, returnFlight, bookingId} = location.state;
    // console.log(inboundSeats + " " + outboundSeats + ' ' + numGuest);
    const numGuestSelected = numGuest;
    const tripType = (returnFlight === null) ? "One way" : "Return";
  // const noGuestSelected = sessionStorage.getItem('noGuestSelected') || "0";
  // const tripType = sessionStorage.getItem('tripType') || "One way";

  const navigate = useNavigate();
  const [passengers, setPassengers] = React.useState([]);
  const [isFormValid, setFormValid] = React.useState(false);


  const handlePassengerDataChange = (passengerData, isValid) => {
    // console.log(passengerData); 
    setPassengers(passengerData);
    setFormValid(isValid);

}
const handleProceedToReview = () => {
      console.log(passengers)
  // Save to sessionStorage
  sessionStorage.setItem('passengerData', JSON.stringify(passengers));
  console.log("navigating to review details");

  const data = {
    "numGuest": numGuest,
    "departureFlight": departureFlight,
    "returnFlight": returnFlight,
    "bookingId": bookingId
}
    navigate("/reviewdetails", {state: data});
}

  
  return (
    <div>
      <div className="nav">
        <NavBar />
      </div>

      <ProgressBar currentStep={"Passenger Details"} number={2} deadline={new Date(sessionStorage.getItem('endTime'))}/>

      <PassengerForm numGuests={numGuestSelected} tripType={tripType} outboundSeats={outboundSeats} inboundSeats={inboundSeats} onPassengerDataChange={handlePassengerDataChange}/>

      <div
        style={{
          display: "flex",
          justifyContent: "center",
          alignItems: "center",
          height: "25vh",
        }}
      >
        <Button variant="contained" color="primary" onClick={handleProceedToReview} disabled={!isFormValid}>
          Proceed to Review
        </Button>
      </div>
    </div>
  );
};

export default PassengerDetails;
