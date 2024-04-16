import React, { useState, useEffect } from "react";
import { Grid, Card, Typography, Box, TextField, Button, Container } from "@mui/material";
import axios from 'axios';
import { useNavigate, useLocation } from 'react-router-dom';

import { isAuthenticated, removeAuthToken, updateAuthHeadersFromCurrentUser } from "../auth";

const RouteUpdatingForm = () => {
  const apiUrl = process.env.REACT_APP_API_BASE_URL;
  const [formData, setFormData] = useState({
    routeId: "-1",
    departureDest: "",
    arrivalDest: "",
    flightDuration: "",
  });
  let location = useLocation();
  const navigate = useNavigate();

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData({
      ...formData,
      [name]: value,
    });
  };

  const handleSubmit = (e) => {
    e.preventDefault();

    axios.put(apiUrl + "routes/update/" + location.state.currentRouteId, formData)
      .then((response) => {
        if (response.status === 200) {
          getRoute();
          const msgStr = "Route " + formData.routeId + " has been updated!\n" +
            "Departure Destination: " + formData.departureDest + "\n" +
            "Arrival Destination: " + formData.arrivalDest + "\n" +
            "Flight Duration: " + formData.flightDuration;
          alert(msgStr);
        } else {
          alert("Failed to update route: " + response.status + "\nPlease check that Flight Duration is in the correct format of 'PT<hour>H<minute>M'");
          console.log("Did not update route: " + response.status);
        }
      })
      .catch((error) => {
        alert("Failed to update route: " + error + "\nPlease check that Flight Duration is in the correct format of 'PT<hour>H<minute>M'");
        console.log("Did not create route: " + error);
      })
  };

  function getRoute() {
    console.log(location.state.currentRouteId);
    axios.get(apiUrl + "routes/" + location.state.currentRouteId)
      .then((response) => {
        // If the call fails, a html to the login page is sent back.
        const isResponseJsonType = response.headers.get('content-type')?.includes('application/json');
        if (response.data != null && isResponseJsonType) {
          setFormData(response.data);
        }
      })
      .catch((error) => {
        console.log(error);
      })
  }


  // Calls immediately upon page load
  useEffect(() => {
    if (isAuthenticated()) {
      axios.get(apiUrl + "users/adminAuthTest").then((response) => {
        // TODO: This isn't correctly reporting errors. Postman is 403, but here it's still 200.
        if (response.status === 200) {
          updateAuthHeadersFromCurrentUser();
          getRoute();
        } else {
          removeAuthToken();
          navigate('/adminPortal/login');
        }
      }).catch((error) => {
        removeAuthToken();
        navigate('/adminPortal/login');
      })
    } else {
      navigate('/adminPortal/login');
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);


  return (
    <Container>


      <Grid container sx={{
        marginTop: "20%",
      }}>
        <Grid item md={3} sm={1} xs={0}></Grid>
        <Grid item md={6} sm={10} xs={12}>
          <Card elevation={3}>
            <form
              style={{
                display: "flex",
                flexDirection: "column",
                alignItems: "center",
                verticalAlign: "center",
                padding: 50,
              }}
              onSubmit={handleSubmit}
            >
              <Box sx={{
                display: 'flex',
                justifyContent: 'space-between',
                width: '100%',
                marginBottom: 2,
              }}>
                <Typography variant="h3">
                  Route {formData.routeId}
                </Typography>

                <Button variant="contained" color="secondary"
                  onClick={() => { navigate("/adminPortal/routes") }}>
                  Go Back
                </Button>
              </Box>

              <TextField fullWidth
                style={{ marginBottom: "16px" }} // You can adjust the spacing
                label="Departure Destination"
                variant="outlined"
                name="departureDest"
                value={formData.departureDest}
                onChange={handleInputChange}
              />
              <TextField fullWidth
                style={{ marginBottom: "16px" }} // You can adjust the spacing
                label="Arrival Destination"
                variant="outlined"
                name="arrivalDest"
                value={formData.arrivalDest}
                onChange={handleInputChange}
              />
              <TextField fullWidth
                style={{ marginBottom: "16px" }} // You can adjust the spacing
                label="Flight Duration"
                variant="outlined"
                name="flightDuration"
                value={formData.flightDuration}
                onChange={handleInputChange}
              />
              <Button type="submit" variant="contained" color="primary" p={3} fullWidth>
                UPDATE ROUTE
              </Button>

            </form>
          </Card>
        </Grid>
      </Grid>
    </Container>
  );
};

export default RouteUpdatingForm;
