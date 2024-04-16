import React from 'react';
import { Box, Typography, Avatar, Divider } from '@mui/material';
import FlightTakeoffIcon from '@mui/icons-material/FlightTakeoff';

const FlightInfoRow = ({ departureTime, departureLocation, arrivalTime, arrivalLocation }) => {
  return (
    <Box
      sx={{
        display: 'flex',
        alignItems: 'center',
      }}
    >
      <Avatar sx={{ marginRight: '25px' }} />
      {/* Box for column text for first row */}
      <Box sx={{ pl: 1 }}>
        <Typography variant="h5" color="white" fontFamily="Noto Sans" fontWeight="bold">
          {departureTime}
        </Typography>
        <Typography variant="body1" color="white" fontFamily="Noto Sans" textAlign="center">
          {departureLocation}
        </Typography>
      </Box>
      {/* Divider (line) */}
      <Divider sx={{ marginLeft: '10px', marginRight: '3px', borderTop: '1px solid white', paddingRight: '70px' }} />
      <FlightTakeoffIcon padding={1} sx={{ color: 'white', fontSize: 'medium' }} />
      {/* Box for second column first row */}
      <Box sx={{ pl: 1 }}>
        <Typography variant="h5" color="white" fontWeight="bold" fontFamily="Noto Sans">
          {arrivalTime}
        </Typography>
        <Typography variant="body1" color="white" fontFamily="Noto Sans" textAlign="center">
          {arrivalLocation}
        </Typography>
      </Box>
    </Box>
  );
};

export default FlightInfoRow;
