import { Box, Divider, Typography } from "@mui/material";
import React from "react";
import img from "./img.jpg";

const SeatListing = ({
  bookedSeatsDep,
  bookedSeatsRet,
  depSeatMap,
  retSeatMap,
}) => {
  return (
    <>
      <Box
        sx={{
          backgroundColor: "#143965",
          padding: 2,
          width: "557px",
          mb: 2,
        }}
      >
        <Box sx={{ display: "flex", alignItems: "center", paddingBottom: 2 }}>
          <Box
            sx={{
              backgroundImage: `url(${img})`,
              height: "120px",
              width: "120px",
              backgroundPosition: "center",
              backgroundColor: "#143965",
              backgroundRepeat: "no-repeat",
            }}
          ></Box>
          {/* Text */}
          <Box
            sx={{
              paddingX: 2,
            }}
          >
            <Typography
              fontFamily={"Merriweather Sans"}
              color={"rgba(179, 186, 201, 1)"}
            >
              Economy
            </Typography>
            <Typography fontFamily={"Merriweather Sans"} color={"white"}>
              Singapore Airlines A380 Airbus
            </Typography>
          </Box>
        </Box>
        <Typography
          fontFamily={"Merriweather Sans"}
          color={"white"}
          variant="caption"
          mt={3}
        >
          Your booking is protected by Wingit
        </Typography>
        <Divider color={"grey"} sx={{ marginY: 2 }} />
        {/* Cost */}
        <Box
          sx={{
            display: "flex",
            flexDirection: "column",
            justifyContent: "space-around",
            alignItems: "flex-start",
          }}
        >
          <Typography
            variant={"body1"}
            color={"white"}
            fontWeight={"bold"}
            fontFamily={"Noto Sans"}
          >
            OutBound Seats
          </Typography>
          {bookedSeatsDep.map((seatNumber) => (
            <Box
              key={seatNumber}
              sx={{
                display: "flex",
                flexDirection: "row",
                justifyContent: "space-between",
                width: "100%",
              }}
            >
              <Typography color={"white"} sx={{ marginRight: 2 }}>
                {seatNumber}
              </Typography>
              <Typography color={"white"}>
                ${depSeatMap[seatNumber].toFixed(2)}
              </Typography>
            </Box>
          ))}

          {bookedSeatsRet != null ? (
            <>
             		<Typography
            variant={"body1"}
            color={"white"}
            fontWeight={"bold"}
            fontFamily={"Noto Sans"}
          >
            InBound Seats
          </Typography>	
{bookedSeatsRet.map((seatNumber) => (
            <Box
              key={seatNumber}
              sx={{
                display: "flex",
                flexDirection: "row",
                justifyContent: "space-between",
                width: "100%",
              }}
            >
              <Typography color={"white"} sx={{ marginRight: 2 }}>
                {seatNumber}
              </Typography>
              <Typography color={"white"}>
                ${retSeatMap[seatNumber].toFixed(2)}
              </Typography>
            </Box>
          ))} 
            </>
          ) : (
            <Typography>No inbound</Typography>
          )}
        </Box>

        <Divider color={"grey"} sx={{ marginY: 2 }} />

        <Box>
          <Box
            sx={{
              display: "flex",
              flexDirection: "row",
              justifyContent: "space-between",
              width: "100%",
              height: "50px",
            }}
          ></Box>
        </Box>
      </Box>
    </>
  );
};

export default SeatListing;
