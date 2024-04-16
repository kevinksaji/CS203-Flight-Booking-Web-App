import React, { useState, useEffect } from "react";
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import { isAuthenticated, removeAuthToken, updateAuthHeadersFromCurrentUser } from "../auth";

import { Typography, Box, TextField, Button, Paper, Container, Grid } from "@mui/material";

const RouteUpdatingForm = () => {
  const apiUrl = process.env.REACT_APP_API_BASE_URL;
  const [formData, setFormData] = useState({
    routeId: "-1",
    departureDest: "",
    arrivalDest: "",
    flightDuration: "",
  });
  const [allRoutes, setAllRoutes] = useState([]);
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

    axios.post(apiUrl + "routes/new", formData)
      .then((response) => {
        if (response.status === 201) {
          getAllRoutes();
          const msgStr = "Successfully added new Route with ID: " + response.data.routeId + "\n" +
            "Departure Destination: " + formData.departureDest + "\n" +
            "Arrival Destination: " + formData.arrivalDest + "\n" +
            "Flight Duration: " + formData.flightDuration;
          alert(msgStr);
        } else {
          alert("Failed to create route.\nPlease check that Flight Duration is in the correct format of 'PT<hour>H<minute>M'");
          console.log("Did not create route: " + response.status);
        }
      })
      .catch((error) => {
        alert("Failed to create route.\nPlease check that Flight Duration is in the correct format of 'PT<hour>H<minute>M'");
        console.log("Did not create route: " + error);
      })

    console.log("Departure Destination:", formData.departureDest);
    console.log("Arrival Destination:", formData.arrivalDest);
    console.log("Flight Duration:", formData.flightDuration);
  };

  function getAllRoutes() {
    axios.get(apiUrl + "routes")
      .then((response) => {
        // If the call fails, a html to the login page is sent back.
        const isResponseJsonType = response.headers.get('content-type')?.includes('application/json');
        if (response.data != null && isResponseJsonType) {
          setAllRoutes(response.data)
        } else {
          setAllRoutes([])
        }
      })
      .catch((error) => {
        console.log(error);
        setAllRoutes([]);
      })
  }

  function onDelete(routeId) {
    axios.delete(apiUrl + "routes/delete/" + routeId)
      .then(() => {
        alert("Successfully deleted " + routeId + "and refreshed routes list.");
        getAllRoutes();
      })
      .catch((error) => {
        alert("Failed to delete " + routeId + ".\nPlease check that there are no dependent routeListings for this route. Additonal error msg:\n" + error);
        console.log(error)
      })
  }

  function onEdit(routeId) {
    let data = {
      currentRouteId: routeId
    };
    navigate("/adminPortal/routes/edit", { state: data });
  }

  // Calls immediately upon page load
  useEffect(() => {
    if (isAuthenticated()) {
      axios.get(apiUrl + "users/adminAuthTest").then((response) => {
        // TODO: This isn't correctly reporting errors. Postman is 403, but here it's still 200.
        if (response.status === 200) {
          updateAuthHeadersFromCurrentUser();
          getAllRoutes();
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
      <Paper sx={{
        p: 1,
        m: 1
      }}>
        <Box sx={{
          display: 'flex',
          justifyContent: 'space-between',
          p: 1,
          m: 1,
          bgcolor: 'background.paper',
          borderRadius: 1,
          alignItems: 'center',
        }}>
          <Box>
            <Typography variant="h3">Routes Management</Typography>
          </Box>
          <Box>
            <Button variant="contained" color="secondary"
              onClick={() => { navigate("/adminPortal/home") }}>Back to Home</Button>
          </Box>
        </Box>
      </Paper>



      <Grid container>
        <Grid item xs={12} sm={8} style={{
          padding: 10,
        }}>
          <Paper elevation={3} style={{
            padding: 20,
          }}>
            <div>
              <Box sx={{
                display: 'flex',
                justifyContent: 'space-between',
              }}>
                <Typography variant="h4">All Routes (Count: {allRoutes != null ? allRoutes.length : 0})</Typography>
                <Button onClick={getAllRoutes} variant="contained">Refresh Routes List</Button>
              </Box>
              <br />
                <Box sx={{
                  display: 'flex',
                  flexDirection: 'column',
                }}>
                  {allRoutes.length > 0 ? (
                    allRoutes.map((item, index) => (
                      <div key={item.routeId} style={{
                        backgroundColor: index % 2 === 0 ? ("#F0F0F0") : ("#FFFFFF")
                      }}>
                        <Box sx={{
                          margin: 1,
                          display: 'flex',
                          justifyContent: 'space-between',
                        }}>
                          <Box sx={{
                            display: 'flex',
                            flexDirection: 'row',
                            justifyContent: 'flex-start',
                          }}>
                            <Button onClick={() => { onEdit(item.routeId); }} variant="contained" color="secondary"
                            sx={{paddingLeft: 3, paddingRight: 3,}}>EDIT</Button>
                            <Typography variant="h5" sx={{paddingLeft: 1}}>
                              {item.routeId}-{item.departureDest} to {item.arrivalDest} ({item.flightDuration})
                            </Typography>
                          </Box>
                          <Button onClick={() => { onDelete(item.routeId); }} variant="contained" color="error">DELETE</Button>
                        </Box>
                      </div>
                    ))) : (
                    <p></p>
                  )}
                </Box>
            </div>
          </Paper>
        </Grid>

        <Grid item xs={12} sm={4} style={{
          padding: 10
        }}>
          <Paper elevation={3}>
            <Box columnGap={2} style={{
              display: 'flex',
              flexDirection: 'column',
              padding: 20,
              alignItems: 'center',
            }}>
              <form 
                style={{
                  display: "flex",
                  flexDirection: "column",
                  alignItems: "center",
                  width: '100%',
                }}
                onSubmit={handleSubmit}
              >
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
                  Add Route
                </Button>
              </form>

            </Box>
          </Paper>
        </Grid>
      </Grid>
    </Container>
  );
};

export default RouteUpdatingForm;
