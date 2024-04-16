import React from "react";
import { Box, Typography, Stepper, StepLabel, Step } from "@mui/material";
import Timer from "./Timer.js"

const ProgressBar = ({ currentStep, number, deadline }) => {
  const steps = [
    "Flights",
    "Seat Selection",
    "Passenger Details",
    "Review",
    "Payment",
    "Confirmation",
  ];

//   console.log(deadline + "from prog")


    return (
        <>
            <Box
                style={{
                    position: "relative",
                    height: "250px", // 1/4th of the viewport height
                    width: "100%", // 100% of the viewport width
                    background: `url('https://media.npr.org/assets/img/2021/10/06/gettyimages-1302813215_wide-6c48e5a6aff547d2703693450c4805978de47435.jpg')`, // Replace with your image URL
                    backgroundSize: "cover", // Scale the image to cover the entire container
                    backgroundPosition: "10% 40%", // Center the image
                    alignItems: "flex-end",
                    display: "flex",
                }}
            >
                <Box
                    sx={{
                        paddingX: "30px",
                        paddingBottom: "10px",
                        display: "flex",
                        justifyContent: "space-between",
                        alignItems: "center", // Center vertically
                        width: "100%"
                    }}
                >
                    <div>
                        <Typography
                            variant="h4"
                            style={{
                                fontFamily: "Merriweather, sans-serif",
                                fontWeight: "bold",
                                color: "white",
                            }}
                        >
                            {currentStep}
                        </Typography>
                    </div>
                    <div>
                        {(number < 4) ? (
                        <Timer deadline={deadline} />) : (<Typography></Typography>)
                    }
                    </div>
                </Box>
            </Box>
            <Box
                sx={{ borderBottom: 1, borderColor: "divider", marginBottom: "20px" }}
            >
                <Box display="flex" justifyContent="center" sx={{ p: 2 }}>
                    <Stepper activeStep={number}>
                        {steps.map((label) => (
                            <Step key={label} sx={{ px: 5 }}>
                                <StepLabel>{label}</StepLabel>
                            </Step>
                        ))}
                    </Stepper>
                </Box>
            </Box>
        </>
    );


};

export default ProgressBar;
