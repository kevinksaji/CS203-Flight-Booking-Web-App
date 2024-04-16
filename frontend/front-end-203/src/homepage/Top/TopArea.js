import React from "react";
import { Box, Grid } from "@mui/material";
import HeroImage from "./planeWing.jpg";
import SearchingBox from "./TopIndexContainer";
import Bookingad from "./Bookingad";

const HomeTop = () => {
  return (
    <Box
      sx={{
        backgroundImage: `url(${HeroImage})`,
        display: "flex",
        backgroundRepeat: "no-repeat",
        backgroundPosition: "center",
        backgroundSize: "100% 100%",
        height: "25vh",
        width: "100%",
        paddingBottom: "300px",
        paddingX: "0px",
        alignItems: "center",
        paddingTop: 4,
      }}
    >
      <Grid
        container
        direction="row"
        justifyContent="space-around"
        alignItems="center"
        pt={6}
      >
        <Grid item pt={6} mt={6}>
            <SearchingBox />
        </Grid>
        <Grid item>
            <Bookingad />
        </Grid>
      </Grid>
    </Box>
  );
};

export default HomeTop;
