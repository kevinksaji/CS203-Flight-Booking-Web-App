import React from "react";
import { Paper, Container, Typography } from "@mui/material";
import ConstructionIcon from "@mui/icons-material/Construction";

const UnderConstruction = () => {
  return (
    <Container disableGutters={true}>
      <Paper
        elevation={0}
        sx={{
          padding: 1,
          width: "420px",
          alignContent: "center",
          justifyContent: "center",
          display: "flex",
          paddingY: 5,
        }}
      >
        <ConstructionIcon />
        <Typography>Under Construction</Typography>
      </Paper>
    </Container>
  );
};

export default UnderConstruction;
