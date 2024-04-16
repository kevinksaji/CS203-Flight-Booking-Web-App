import React from "react";
import Typography from "@mui/material/Typography";
import Divider from "@mui/material/Divider";
import "./FlightInfoCard.css"; // Import the CSS file
import { Avatar } from "@mui/material";
import { Stack } from "@mui/material";



const FlightInfoCard = ({
  // parameters
  flightType,
  imageURL,
  departureAirport,
  departureDate,
  departureTime,
  arrivalAirport,
  arrivalDate,
  arrivalTime,
  stops,
  travelTime,
  price,
  flightNumber,
  onSelect,
  bookNowLabel = "Select",
  seats
}) => {

  console.log(departureAirport, departureDate, departureTime, arrivalAirport, arrivalDate, arrivalTime);


  return (
    <div className="flight-info-card">
      {/* New Section: Flight Type Header */}
      <div className="flight-type-header">
        {flightType}
      </div>
      <div className="flight-content">
        {/* Section 1: Airline Name */}
        <div className="section">
          <Avatar
            src={imageURL}
            sx={{ width: 70, height: 70, marginLeft: "25px" }}
          ></Avatar>
        </div>
        <div className="section">
          <Typography
            variant="body1"
            fontSize={30}
            sx={{ fontFamily: "Merriweather Sans", marginTop: "1.25rem" }}

          >
            {flightNumber} {/* Display flight number here */}
          </Typography>
        </div>

        {/* Section 2: Departure Information */}
        <div className="section">
          <Typography
            variant="body1"
            fontSize={20}
            sx={{ fontFamily: "Merriweather Sans" }}
          >
            {departureAirport}
          </Typography>
          <Typography variant="body1" sx={{ fontFamily: "Merriweather Sans" }}>
            {departureDate}
          </Typography>
          <Typography variant="body1" sx={{ fontFamily: "Merriweather Sans" }}>
            {departureTime}
          </Typography>
        </div>

        {/* Section 3: Dotted Line and Plane Logo */}
        <div className="section">
          <Stack direction="column" spacing={1} alignItems="center">

            <Typography variant="body1" sx={{ fontFamily: 'Merriweather Sans' }}>{travelTime}</Typography>

            <Divider className="divider" />

            <Typography variant="body1" sx={{ fontFamily: 'Merriweather Sans' }}>{stops}</Typography>
          </Stack>
        </div>

        {/* Section 4: Arrival Information */}
        <div className="section">
          <Typography
            variant="body1"
            fontSize={20}
            sx={{ fontFamily: "Merriweather Sans" }}
          >
            {arrivalAirport}
          </Typography>

          <Typography variant="body1" sx={{ fontFamily: "Merriweather Sans" }}>
            {arrivalDate}
          </Typography>

          <Typography variant="body1" sx={{ fontFamily: "Merriweather Sans" }}>
            {arrivalTime}
          </Typography>
        </div>

        <div>

        </div>

      </div>
    </div>
  );
};

export default FlightInfoCard;
