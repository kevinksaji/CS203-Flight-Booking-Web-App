import React from "react";
import { Box, Typography, Stack } from "@mui/material";
import footerimage from "./footerimage.jpg";
const Footer = () => {
  return (
    <Box
      sx={{
        backgroundImage: `url(${footerimage})`, // Replace with your image URL
        backgroundRepeat: "no-repeat",
        backgroundPosition: "center",
        padding: "20px",
        justifyContent: "space-between",
        backgroundSize: "100% 100%",
        height: "50vh",
      }}
    >
      <Stack
        sx={{
          alignItems: "flex-end",
          justifyContent: "center",
          height: "50vh",
          mr: 5,
          pr: 5,
        }}
        spacing={3}
      >
        <Typography variant="h4" sx={{ fontWeight: "bold", color: "#223662" }}>
          Captivate the moment
        </Typography>
        <Typography variant="h6" color={"#223662"}>
          Discover all destinations
        </Typography>
      </Stack>
    </Box>
  );
};

export default Footer;
