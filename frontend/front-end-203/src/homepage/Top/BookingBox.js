import { React, useState } from "react";
import {
  Paper,
  TextField,
  Button,
  Grid,
  Container,
  Autocomplete,
} from "@mui/material";
import InputLabel from "@mui/material/InputLabel";
import MenuItem from "@mui/material/MenuItem";
import FormControl from "@mui/material/FormControl";
import Select from "@mui/material/Select";
import { AdapterDayjs } from "@mui/x-date-pickers/AdapterDayjs";
import { LocalizationProvider } from "@mui/x-date-pickers/LocalizationProvider";
import { DatePicker } from "@mui/x-date-pickers/DatePicker";
import { useNavigate } from "react-router-dom";
const CompactForm = () => {
  //Trip Options funcs
  const tripOptions = ["One way", "Return"];
  const [tripSelected, setTripSelected] = useState("One way");
  const tripHandleChange = (e) => {
    setTripSelected(e.target.value);
  };
  //Number of guest options
  const noGuestOptions = [1, 2, 3, 4, 5];
  const [noGuestSelected, setGuestSelected] = useState("1");
  const noGuestHandleChange = (e) => {
    const updatedValue = e.target.value; 
    setGuestSelected(updatedValue);
  };

  //flying to Options funcs
  const flyingToOptions = ["Japan", "Singapore", "Taiwan", "India", "China"];
  const [flyingToSelected, setFlyingToSelected] = useState(null);

  //flying from Options funcs
  const flyingFromOptions = ["Japan", "Singapore", "Taiwan", "India", "China"];
  const [flyingFromSelected, setFlyingFromSelected] = useState(null);

  //Depature date
  const [Depdate, setDepDate] = useState("");
  const DepHandleDateChange = (date) => {
    setDepDate(date);
  };
  //return date
  const [returnDate, setReturnDate] = useState("");
  const returnHandleDateChange = (date) => {
    setReturnDate(date);
  };
  //form submission
  const navigate = useNavigate();
  const handleClick = () => {
    let data = {
      trip: tripSelected,
      noGuest: noGuestSelected,
      flyingFrom: flyingFromSelected,
      flyingTo: flyingToSelected,
      departuredt: JSON.stringify(Depdate),
      returndt: JSON.stringify(returnDate),
    };
    if (noGuestSelected === "") {
      alert("Please select number of guests");
    } else if (tripSelected === "") {
        alert("Please select trip type");
    } else if ((data.trip === "Return" && returnDate === "") || Depdate === "") {
      alert("Please select appropriate dates");
    } else if (
      data.flyingFrom === "" ||
      data.flyingTo === "" ||
      data.flyingFrom === null ||
      data.flyingTo === ""
    ) {
      alert("Please select appropriate locations");
    } else {
      console.log(data);
      navigate("flightsearch", { state: data });
    }
  };
  return (
    <Container disableGutters={true}>
      <Paper elevation={0} sx={{ padding: 1, width: "420px" }}>
        <Grid container rowSpacing={1} columnSpacing={2}>
          {/* Row 1: 3 Item Pickers */}
          <Grid item xs={6}>
            <FormControl sx={{ m: 0, minWidth: 120 }} size="small" fullWidth>
              <InputLabel id="Trip">Trip</InputLabel>
              <Select
                labelId="Trip"
                id="Trip"
                value={tripSelected}
                label="Trip"
                onChange={tripHandleChange}
              >
                <MenuItem value="">
                  <em>None</em>
                </MenuItem>
                {tripOptions.map((option) => (
                  <MenuItem key={option} value={option}>
                    {option}
                  </MenuItem>
                ))}
              </Select>
            </FormControl>
          </Grid>
          <Grid item xs={6}>
            <FormControl sx={{ m: 0, minWidth: 120 }} size="small" fullWidth>
              <InputLabel id="noGuest">No. Guest</InputLabel>
              <Select
                labelId="noGuest"
                id="noGuest"
                value={noGuestSelected}
                label="noGuest"
                onChange={noGuestHandleChange}
              >
                <MenuItem value="">
                  <em>None</em>
                </MenuItem>
                {noGuestOptions.map((option) => (
                  <MenuItem key={option} value={option}>
                    {option}
                  </MenuItem>
                ))}
              </Select>
            </FormControl>
          </Grid>

          {/* Row 2: 2 Text Inputs */}
          <Grid item xs={6}>
            <Autocomplete
              id="flyingFrom"
              options={flyingFromOptions}
              value={flyingFromSelected}
              renderInput={(params) => (
                <TextField {...params} placeholder="Flying From" />
              )}
              onChange={(event, newValue) => setFlyingFromSelected(newValue)}
              fullWidth
            />
          </Grid>
          <Grid item xs={6}>
            <Autocomplete
              id="flyingTo"
              options={flyingToOptions}
              value={flyingToSelected}
              renderInput={(params) => (
                <TextField {...params} placeholder="Flying To" />
              )}
              onChange={(event, newValue) => setFlyingToSelected(newValue)}
              fullWidth
            />
          </Grid>

          {/* Row 3: 2 Input Boxes and Submit Button */}
          <Grid item xs={6}>
            <LocalizationProvider dateAdapter={AdapterDayjs}>
              <DatePicker
                name="departuredt"
                label="Depature Date"
                value={Depdate}
                onChange={DepHandleDateChange}
              />
            </LocalizationProvider>
          </Grid>
          <Grid item xs={6}>
            {" "}
            <LocalizationProvider dateAdapter={AdapterDayjs}>
              <DatePicker
                name="returndt"
                label="Return Date"
                value={returnDate}
                onChange={returnHandleDateChange}
                disabled={tripSelected !== "Return"}
                minDate={Depdate}
              />
            </LocalizationProvider>
          </Grid>
          <Grid item xs={12}>
            <Button
              variant="contained"
              color="primary"
              onClick={handleClick}
              fullWidth
            >
              Search Flights
            </Button>
          </Grid>
        </Grid>
      </Paper>
    </Container>
  );
};

export default CompactForm;
