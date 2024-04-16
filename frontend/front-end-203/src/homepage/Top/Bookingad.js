import React from "react";
import { Typography, Paper, createTheme, ThemeProvider } from "@mui/material";

const customTheme = createTheme({
  typography: {
    h5: {
      fontWeight: "bold",
      color: "white",
    },
  },
});

const BlueRoundedRectangle = () => {
  return (
    <ThemeProvider theme={customTheme}>
      <Paper
        sx={{
          background: "rgba(37, 78, 158, .8)", // Blue translucent background
          borderRadius: "16px", // Rounded corners
          padding: "16px", // Padding around content
          paddingX: "20px",
          paddingRight: "70px",
          textAlign: "left", // Center the content
          mt: 5,
        }}
      >
        <Typography variant="h5" pb={3}>
          Where to next?
        </Typography>
        <Typography variant="h5">Get ready for your</Typography>
        <Typography variant="h5">
          {" "}
          next adventure...
        </Typography>
      </Paper>
    </ThemeProvider>
  );
};

export default BlueRoundedRectangle;
