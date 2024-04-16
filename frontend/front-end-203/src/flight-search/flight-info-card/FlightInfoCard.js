import React from "react";
import Typography from "@mui/material/Typography";
import Divider from "@mui/material/Divider";
import Button from "@mui/material/Button";
import "./FlightInfoCard.css"; // Import the CSS file
import { Avatar } from "@mui/material";
import { Stack } from "@mui/material";



const FlightInfoCard = ({
  // parameters
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
  seats,
  isDisabled

  
}) => {
  const cardStyle = isDisabled ? { backgroundColor: 'grey' } : {};
  const buttonProps = isDisabled ? { disabled: true } : { onClick: onSelect };
  
  return (
    <div className="flight-info-card" style={{ ...cardStyle }}>
      {/* Section 1: Airline Name */}
      <div className="section">
        <Avatar
          src={imageURL}
          sx={{width: 70, height: 70 , marginLeft: "25px"}}
        ></Avatar>
      </div>
      <div className="section">
      <Typography
          variant="body1"
          fontSize={30}
          sx={{ fontFamily: "Merriweather Sans",marginTop: "1.25rem"}}
          
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

          <Typography variant="body1" sx={{fontFamily: 'Merriweather Sans'}}>{travelTime}</Typography>

          <Divider className="divider" />

          <Typography variant="body1" sx={{fontFamily: 'Merriweather Sans'}}>{stops}</Typography>
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

      {/* Section 5: Price and Book Now Button */}
      <div className="price-select-seats">
      <Stack direction={"column"} spacing={2} alignItems="center">
      <Typography fontSize={17} sx={{ fontFamily: "Merriweather Sans", textTransform: "none", color: "white"}}>
          Available Seats: {seats}
        </Typography>
        <Typography
          variant="h6"
          fontSize={30}
          sx={{ fontFamily: "Merriweather Sans", color: "white"}}
        >
          ${price}
        </Typography>

        <Button variant="contained" {...buttonProps}
        onClick={onSelect}
        sx={{
          backgroundColor: "darkorange",
          color: "white",
          padding: "5px",
          "&:hover": {
            backgroundColor: "orange"
          }
        }}
      >
        <Typography variant="h6" fontSize={15} sx={{ fontFamily: "Merriweather Sans", textTransform: "none", color: "white"}}>
          {bookNowLabel}
        </Typography>
      </Button>
      </Stack>
      
      
      </div>
    </div>
  );
};

export default FlightInfoCard;
