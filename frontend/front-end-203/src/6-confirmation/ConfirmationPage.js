import React from "react";
import BookingSlip from "./BookingSlip.js";
import { Box, Container, Typography, Button } from "@mui/material";
import { useNavigate } from "react-router-dom";

import { getCurrentUser } from "../auth.js";
import ProgressBar from "../progress-bar/ProgressBar";
import NavigationBar from "../nav-bar/NavigationBar";
import axios from "axios";

const ConfirmationPage = () => {
  const apiUrl = process.env.REACT_APP_API_BASE_URL;
  const retrievedData = JSON.parse(sessionStorage.getItem('selectedFlights'));
  const passengerData = JSON.parse(sessionStorage.getItem('passengerData'));

  const tripType = sessionStorage.getItem('tripType') || "One way";
  const bookingId = sessionStorage.getItem('bookingId')
  const username = getCurrentUser.username;
  console.log("This is booking id: " + bookingId);
  console.log("This is triptype: " + tripType);
  console.log("This is passengerData")
  console.log(passengerData);
  console.log("This is retrieveData")
  console.log(retrievedData);
  console.log("This is username")
  console.log(username);



  const navigate = useNavigate();

  const handleClickReturn = (e) => {
    navigate("/");
  }

  function downloadCalendarFile(inBookingId) {
    console.log("Downloading calendar file for booking ID: " + inBookingId)
    axios.get(apiUrl + "rest/api/generate-calendar/" + inBookingId, {
      responseType: 'blob'
    })
      .then((response) => {
        if (response.status === 200) {
          // return response.blob();
          const blobData = response.data;

          const blobUrl = window.URL.createObjectURL(blobData);
          const a = document.createElement('a');
          a.style.display = 'none';
          a.href = blobUrl;
          a.download = 'mycalendar.ics';
          document.body.appendChild(a);
          a.click();
          window.URL.revokeObjectURL(blobUrl);
        } else {
          throw new Error("Something went wrong: " + response.status);
        }
      })
      .catch((error) => {
        console.log(error);
        alert("Error downloading calendar file (.ics)");
      });
  }

  return (
    <>
        <div>
            <NavigationBar />
        </div>
        <ProgressBar currentStep={"Confirmation"} number={5} deadline={new Date(sessionStorage.getItem('endTime'))} />
      <Box
        sx={{
          display: "flex",
          flexDirection: "column",
          alignItems: "center",
          justifyContent: "center",
          textAlign: "center",
        }}
      >
        <Typography
          variant="h4"
          fontWeight={"bold"}
          sx={{ textDecoration: "underline", p: 3 }}
        >
          Booking Success!
        </Typography>
        <Button
          variant={"contained"}
          sx={{ mb: 3, color: "white", backgroundColor: "#F9AB55" }}
          onClick={() => { downloadCalendarFile(bookingId); }}
        >
          Download Calendar File
        </Button>
      </Box>
      <Container>
      {
          passengerData && passengerData.map((passenger, index) => (
            <Box mb={4} key={index}> {/* mb is for margin-bottom */}
            <BookingSlip
              key={index}
              outboundDepartureTime={retrievedData.departureFlight.departureDatetime?.split("T")[0]}  // You'll need to adjust these according to how you've stored them
              outboundDepartureLocation={retrievedData.departureFlight.departureLocation}
              outboundArrivalTime={retrievedData.departureFlight.departureDatetime?.split("T")[0]}
              outboundArrivalLocation={retrievedData.departureFlight.arrivalLocation}
              inboundDepartureTime={retrievedData.returnFlight.departureDatetime?.split("T")[0]}
              inboundDepartureLocation={retrievedData.returnFlight.departureLocation}
              inboundArrivalTime={retrievedData.returnFlight.departureDatetime?.split("T")[0]}
              inboundArrivalLocation={retrievedData.returnFlight.arrivalLocation}
              bookingID={bookingId}
              passengerName={passenger.salutation + " " + passenger.firstName + " " + passenger.lastName}  // Assumes 'name' is a field in each object in passengerData
              inboundFlightNumber ={retrievedData.returnFlight.planeId}
              outboundFlightNumber={retrievedData.departureFlight.planeId}  // Adjust this as well
              classType={retrievedData.classType}  // Adjust this as well
              outboundSeat={passenger.outboundSeat}  // Assumes 'outboundSeat' is a field in each object in passengerData
              inboundSeat={passenger.returnSeat}  // Assumes 'inboundSeat' is a field in each object in passengerData
            />
            </Box>
          ))
        }
      </Container>
      <Box
        sx={{
          display: "flex",
          flexDirection: "column",
          alignItems: "center",
          justifyContent: "center",
          textAlign: "center",
        }}
      >
        <Button variant={"contained"} sx={{ mt: 3 }} onClick={handleClickReturn}>
          Return to home
        </Button>
      </Box>
    </>
  );
};

export default ConfirmationPage;
