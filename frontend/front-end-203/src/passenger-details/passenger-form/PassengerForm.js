import React, { useState, useEffect } from 'react';
import { Box, Typography, TextField, Select, MenuItem, FormControl, InputLabel } from '@mui/material';

const PassengerForm = ({ numGuests, tripType, outboundSeats, inboundSeats, onPassengerDataChange }) => {

    const [passengers, setPassengers] = useState(Array.from({ length: parseInt(numGuests) }).map(() => ({
        salutation: '',
        firstName: '',
        lastName: '',
        outboundSeat: '',
        returnSeat: '',
    })));

    const [selectedOutboundSeats, setSelectedOutboundSeats] = useState([]);
    const [selectedInboundSeats, setSelectedInboundSeats] = useState([]);


    useEffect(() => {
        setSelectedOutboundSeats(passengers.map(p => p.outboundSeat));
        setSelectedInboundSeats(passengers.map(p => p.returnSeat));
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, []);

    const areFieldsValid = () => {
        for (let passenger of passengers) {
          if (!passenger.salutation || !passenger.firstName || !passenger.lastName || !passenger.outboundSeat || (tripType === "Return" && !passenger.returnSeat)) {
            return false;
          }
        }
        return true;
      };
      

    const handleInputChange = (index, field, value) => {
        const updatedPassengers = [...passengers];
        updatedPassengers[index][field] = value;
        setPassengers(updatedPassengers);

        if (field === 'outboundSeat') {
            setSelectedOutboundSeats(prevSeats => {
                const newSeats = [...prevSeats];
                newSeats[index] = value;
                return newSeats;
            });
        }
    
        if (tripType === "Return" && field === 'returnSeat') {
            setSelectedInboundSeats(prevSeats => {
                const newSeats = [...prevSeats];
                newSeats[index] = value;
                return newSeats;
            });
        }

        // inform parent component
        if (onPassengerDataChange) onPassengerDataChange(updatedPassengers, areFieldsValid());
    };
    
    const renderPassengerFields = (index) => (
        <Box key={index}>
            <Typography variant="body1" gutterBottom>
                Passenger {index + 1} (Adult)
            </Typography>
            <Box sx={{ display: 'flex', gap: 2, marginBottom: 5 }}>
                <FormControl variant="filled" style={{ backgroundColor: 'white' }} fullWidth>
                    <InputLabel>Salutation</InputLabel>
                    <Select label="Salutation" 
                            value={passengers[index].salutation}
                            onChange={(e) => handleInputChange(index, 'salutation', e.target.value)}
                        >
                        <MenuItem value={"Mr"}>Mr</MenuItem>
                        <MenuItem value={"Mrs"}>Mrs</MenuItem>
                        <MenuItem value={"Miss"}>Ms</MenuItem>
                        <MenuItem value={"Mdm"}>Mdm</MenuItem>
                        <MenuItem value={"Master"}>Master</MenuItem>
                    </Select>
                </FormControl>
                <TextField 
                    variant="filled" 
                    label="First name" 
                    style={{ backgroundColor: 'white' }} 
                    fullWidth  
                    value={passengers[index].firstName}
                    onChange={(e) => handleInputChange(index, 'firstName', e.target.value)}
                    />
                <TextField 
                    variant="filled" 
                    label="Last name" 
                    style={{ backgroundColor: 'white' }} 
                    fullWidth  
                    value={passengers[index].lastName}
                    onChange={(e) => handleInputChange(index, 'lastName', e.target.value)}
                    />
                <FormControl variant="filled" style={{ backgroundColor: 'white' }} fullWidth>
                    <InputLabel>Outbound Seat No.</InputLabel>
                    <Select label="Outbound Seat No." 
                             value={passengers[index].outboundSeat}
                             onChange={(e) => handleInputChange(index, 'outboundSeat', e.target.value)}
                    > 
                    {/*filter out seats that have been selected */}
                    {outboundSeats.filter(seat => !selectedOutboundSeats.includes(seat) || seat === passengers[index].outboundSeat).map(seat => (
                <MenuItem key={seat} value={seat}>{seat}</MenuItem>
            ))}
                    </Select>
                </FormControl>
                {tripType ==="Return" && (
                    <FormControl variant="filled" style={{ backgroundColor: 'white' }} fullWidth>
                        <InputLabel>Return Seat No.</InputLabel>
                        <Select label="Return Seat No." 
                                value={passengers[index].returnSeat}
                                onChange={(e) => handleInputChange(index, 'returnSeat', e.target.value)}
                        >
                            {inboundSeats.filter(seat => !selectedInboundSeats.includes(seat) || seat === passengers[index].returnSeat).map(seat => (
                        <MenuItem key={seat} value={seat}>{seat}</MenuItem>
                    ))}
                        </Select>
                    </FormControl>
                )}
            </Box>
        </Box>
    );

    return (
        <Box sx={{ width: '100%', maxWidth: 800, backgroundColor: '#223662', padding: 4, borderRadius: 2, color: 'white', margin: 'auto', marginTop: 5 }}>
            <Typography variant="h6" gutterBottom sx={{ fontFamily: 'Merriweather', fontSize: 20, fontWeight: '400' }}>
                Passenger Information
            </Typography>
            <Typography variant="body2" gutterBottom>
                Enter the required information for each traveler and be sure that it exactly matches the government-issued ID presented at the airport.
            </Typography>
            
            {/* Dynamically rendering passenger fields */}
            {Array.from({ length: parseInt(numGuests) }).map((_, index) => renderPassengerFields(index))}
        </Box>
    );
}

export default PassengerForm;
