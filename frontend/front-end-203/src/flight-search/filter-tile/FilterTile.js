import React, { useState } from "react";
import {
  Box,
  Typography,
  Slider,
  Stack,
  TextField,
  Autocomplete,
} from "@mui/material";

const FilterTile = ({ airlines, onFilterChange, onPriceChange, onFlightTimeChange}) => {

  // State variables for flight time range, price range, selected airlines, and user input
  const [flightTime, setFlightTime] = useState([0, 24]);
  const [priceRange, setPriceRange] = useState([0, 5000]);
  const [selectedAirlines, setSelectedAirlines] = useState([]);
  const [inputValue, setInputValue] = useState("");
  const [minPrice, setMinPrice] = useState(0);
  const [maxPrice, setMaxPrice] = useState(5000);

  // Handle flight time slider change
  const handleFlightTimeChange = (event, newTime) => {
    setFlightTime(newTime);
    console.log(newTime);

    if (onFlightTimeChange) {
      onFlightTimeChange(newTime);
  }
  };
  // Handle min price input change
  const handleMinPriceChange = (event) => {
    const newMinPrice = parseInt(event.target.value, 10);
    setMinPrice(newMinPrice);
    setPriceRange([newMinPrice, priceRange[1]]);
    onPriceChange(newMinPrice, maxPrice);
  };

  // Handle max price input change
  const handleMaxPriceChange = (event) => {
    const newMaxPrice = parseInt(event.target.value, 10);
    setMaxPrice(newMaxPrice);
    setPriceRange([priceRange[0], newMaxPrice]);
    onPriceChange(minPrice, newMaxPrice);
  };
  return (
    <div
      style={{
        borderTopLeftRadius: "15px",
        borderBottomLeftRadius: "15px",
        paddingTop: "3rem",
        paddingBottom: "3rem",
        padding: "3rem",
        width: "17rem",
        backgroundColor: "#143965", // Background color
        color: "white", // Text color
        fontFamily: "Merriweather Sans, sans-serif", // Font style
        display: "flex",
        flexDirection: "column",
        alignItems: "center",
        gap: "20px", // Increase spacing between elements
      }}
    >
      <Typography variant="h6" sx={{ fontFamily: "Merriweather Sans" }}>
        Filter
      </Typography>

      <div style={{ width: "100%" }}>
        <Typography
          variant="subtitle1"
          sx={{ fontFamily: "Merriweather Sans" }}
        >
          Time of Flight:
        </Typography>
        {/* Flight time slider */}
        <Slider
          value={flightTime}
          onChange={handleFlightTimeChange}
          valueLabelDisplay="auto"
          min={0}
          max={24}
          step={1}
          sx={{
            color: "darkorange",
            "& .MuiSlider-track": {
              backgroundColor: "darkorange", // Orange rail color
            },
            "& .MuiSlider-thumb": {
              backgroundColor: "white", // White thumb background
              boxShadow: "0px 0px 5px 2px rgba(255,255,255,0.75)", // White shadow
            },
            "& .MuiSlider-valueLabel": {
              color: "white", // Change to your desired text color
              backgroundColor: "darkorange", // Change to your desired background color
              fontFamily: "Merriweather Sans",
            },
          }}
        />
        <Box display="flex" justifyContent="space-between">
          <Typography
            variant="caption"
            sx={{ fontFamily: "Merriweather Sans" }}
          >
            {flightTime[0]}:00
          </Typography>
          <Typography
            variant="caption"
            sx={{ fontFamily: "Merriweather Sans" }}
          >
            {flightTime[1]}:00
          </Typography>
        </Box>
      </div>

      {/* Min and Max Price Input Boxes */}
      <div
        style={{
          width: "100%",
          display: "flex",
          justifyContent: "space-between",
        }}
      >
        <div style={{ width: "48%" }}>
          <Typography
            variant="subtitle1"
            sx={{ fontFamily: "Merriweather Sans" }}
          >
            Min Price:
          </Typography>
          <TextField
            type="number"
            value={minPrice}
            onChange={handleMinPriceChange}
            variant="outlined"
            sx={{
              fontFamily: "Merriweather Sans",
              "& input": {
                color: "white", // white input text colour
              },
              "& .MuiOutlinedInput-root": {
                "& fieldset": {
                  borderColor: "white", // Default border color
                },
                "&:hover fieldset": {
                  borderColor: "white", // Border color on hover
                },
                "&.Mui-focused fieldset": {
                  borderColor: "white", // Border color on click/focus
                },
                "& .MuiSvgIcon-root": {
                  color: "white", // Color of the dropdown icon
                },
                "&::selection": {
                  color: "white", // Color of the text cursor (selection color)
                  background: "transparent", // Background color when text is selected
                },
              },
              "& .MuiChip-root": {
                backgroundColor: "darkorange", // Orange chip background color
                color: "white", // White chip text color
                fontFamily: "Merriweather Sans",
                "& .MuiChip-deleteIcon": {
                  color: "white", // White delete icon color
                },
              },
            }}
            InputProps={{
              style: { color: "white", borderColor: "white" }, // White text and outline
            }}
          />
        </div>
        <div style={{ width: "48%" }}>
          <Typography
            variant="subtitle1"
            sx={{ fontFamily: "Merriweather Sans" }}
          >
            Max Price:
          </Typography>
          <TextField
            type="number"
            value={maxPrice}
            onChange={handleMaxPriceChange}
            variant="outlined"
            sx={{
              fontFamily: "Merriweather Sans",
              "& input": {
                color: "white", // white input text colour
              },
              "& .MuiOutlinedInput-root": {
                "& fieldset": {
                  borderColor: "white", // Default border color
                },
                "&:hover fieldset": {
                  borderColor: "white", // Border color on hover
                },
                "&.Mui-focused fieldset": {
                  borderColor: "white", // Border color on click/focus
                },
                "& .MuiSvgIcon-root": {
                  color: "white", // Color of the dropdown icon
                },
                "&::selection": {
                  color: "white", // Color of the text cursor (selection color)
                  background: "transparent", // Background color when text is selected
                },
              },
              "& .MuiChip-root": {
                backgroundColor: "darkorange", // Orange chip background color
                color: "white", // White chip text color
                fontFamily: "Merriweather Sans",
                "& .MuiChip-deleteIcon": {
                  color: "white", // White delete icon color
                },
              },
            }}
            InputProps={{
              style: { color: "white", borderColor: "white" }, // White text and outline
            }}
          />
        </div>
      </div>

      <div style={{ width: "100%" }}>
        <Typography
          variant="subtitle1"
          sx={{ fontFamily: "Merriweather Sans" }}
        >
          Airline Selector:
        </Typography>
        <Stack direction="column" spacing={1}>
          {/* Autocomplete combo box for airline selection */}
          <Autocomplete
            multiple
            id="airline-selector"
            options={airlines.filter(
              (airline) => !selectedAirlines.includes(airline)
            )} // Exclude selected airlines
            value={selectedAirlines}
            inputValue={inputValue}
            onInputChange={(_, newInputValue) => {
              setInputValue(newInputValue);
            }}
            onChange={(_, newValue) => {
              setSelectedAirlines(newValue);
            }}
            sx={{
              "& input": {
                color: "white", // white input text colour
              },
              "& .MuiOutlinedInput-root": {
                "& fieldset": {
                  borderColor: "white", // Default border color
                },
                "&:hover fieldset": {
                  borderColor: "white", // Border color on hover
                },
                "&.Mui-focused fieldset": {
                  borderColor: "white", // Border color on click/focus
                },
                "& .MuiSvgIcon-root": {
                  color: "white", // Color of the dropdown icon
                },
                "&::selection": {
                  color: "white", // Color of the text cursor (selection color)
                  background: "transparent", // Background color when text is selected
                },
              },
              "& .MuiChip-root": {
                backgroundColor: "darkorange", // Orange chip background color
                color: "white", // White chip text color
                fontFamily: "Merriweather Sans",
                "& .MuiChip-deleteIcon": {
                  color: "white", // White delete icon color
                },
              },
            }}
            renderInput={(params) => (
              <TextField {...params} variant="outlined" />
            )}
            getOptionLabel={(option) => option}
            renderOption={(props, option, { selected }) => (
              <li {...props}>{option}</li>
            )}
          />
        </Stack>
      </div>
    </div>
  );
};

export default FilterTile;
