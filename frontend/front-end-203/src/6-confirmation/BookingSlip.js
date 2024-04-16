import React from "react";
import { Typography, Grid, Paper } from "@mui/material";
import FlightConfirm from "./FlightConfirmationDetails";

export default function BookingSlip({
  outboundDepartureTime,
  outboundDepartureLocation,
  outboundArrivalTime,
  outboundArrivalLocation,
  inboundDepartureTime,
  inboundDepartureLocation,
  inboundArrivalTime,
  inboundArrivalLocation,
  bookingID,
  passengerName,
  outboundFlightNumber,
  inboundFlightNumber,
  classType,
  outboundSeat,
  inboundSeat,
}) {
  return (
    <>
      <Paper
        elevation={3}
        sx={{
          height: "100%",
          display: "flex",
          borderRadius: "16px",
          overflow: "hidden",
        }}
      >
        <Grid
          container
          item
          xs={8}
          sx={{ alignItems: "center", justifyContent: "center" }}
        >
          <div
            style={{
              width: "100%",
              height: "100%",
              backgroundColor: "#143965",
              borderRight: "2px solid black", // Vertical line
            }}
          >
            <Grid
              container
              item
              direction="column"
              justifyContent="space-between"
              alignItems="center"
              sx={{ height: "100%", padding: "16px" }}
            >
              <FlightConfirm
                departureTime={outboundDepartureTime}
                departureLocation={outboundDepartureLocation}
                arrivalTime={outboundArrivalTime}
                arrivalLocation={outboundArrivalLocation}
              />
              {inboundDepartureTime &&
                inboundDepartureLocation &&
                inboundArrivalTime &&
                inboundArrivalLocation && (
                  <FlightConfirm
                    departureTime={inboundDepartureTime}
                    departureLocation={inboundDepartureLocation}
                    arrivalTime={inboundArrivalTime}
                    arrivalLocation={inboundArrivalLocation}
                  />
                )}
              <Typography
                variant="caption"
                color="white"
                sx={{ textAlign: "center", pl: "60px" }}
              >
                Booking ID: {bookingID}
              </Typography>
            </Grid>
          </div>
        </Grid>

        <Grid container item xs={4}>
          <Grid
            container
            item
            direction="column"
            justifyContent="space-between"
            alignItems="center"
            sx={{ height: "100%", padding: "16px", backgroundColor: "#F9AB55" }}
          >
            <Typography variant="h5" fontFamily={"Merriweather"}>
              {passengerName}
            </Typography>
            <div style={{ marginTop: "16px" }}>
              <Typography variant="body1" fontFamily={"Noto Sans"}>
                Outbound Flight Number: {outboundFlightNumber}
              </Typography>
              <Typography variant="body1" fontFamily={"Noto Sans"}>
                {classType}
              </Typography>
              <Typography variant="body1" fontFamily={"Noto Sans"}>
                Outbound Seat Number: {outboundSeat}
              </Typography>
              {inboundDepartureTime && (
                  <>
                  <Typography variant="body1" fontFamily={"Noto Sans"}>
                    Inbound Flight Number: {inboundFlightNumber}
                  </Typography>
                  <Typography variant="body1" fontFamily={"Noto Sans"}>
                    Inbound Seat Number: {inboundSeat}
                  </Typography>
                  </>
            )}
            </div>
          </Grid>
        </Grid>
      </Paper>
    </>
  );
}
