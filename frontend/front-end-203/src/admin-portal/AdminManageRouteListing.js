import React, { useState, useEffect } from "react";
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import { isAuthenticated, removeAuthToken, updateAuthHeadersFromCurrentUser } from "../auth";

import { Typography, Box, TextField, Button, Paper, Container, Grid } from "@mui/material";

const RouteListingUpdatingForm = () => {
  const apiUrl = process.env.REACT_APP_API_BASE_URL;
  const [formData, setFormData] = useState({
    routeId: "5",
    planeId: "SQ123",
    departureDatetime: "2023-12-19T12:12:00",
    basePrice: "",
  });
  const [searchFormData, setSearchFormData] = useState({
    departureDest: "Taiwan",
    arrivalDest: "Singapore",
    year: "2023",
    month: "12",
    day: ""
  });
  const [searchedRouteListings, setSearchedRouteListings] = useState([]);
  const navigate = useNavigate();

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData({
      ...formData,
      [name]: value,
    });
  };
  const handleSearchInputChange = (e) => {
    const { name, value } = e.target;
    setSearchFormData({
      ...searchFormData,
      [name]: value,
    });
  };

  const handleSubmit = (e) => {
    e.preventDefault();

    axios.post(apiUrl + "routeListings/new", formData)
      .then((response) => {
        if (response.status === 201) {
          getSearchedRouteListings();
          const msgStr = "Successfully added new RouteListing\n" +
            "Route ID: " + formData.routeId + "\n" +
            "Plane ID: " + formData.planeId + "\n" +
            "Departure Date Time: " + formData.departureDatetime + "\n" +
            "Base Price" + formData.basePrice;
          alert(msgStr);
        } else {
          alert("Failed to create routeListing.\nPlease check that Departure Date Time is in the correct format of 'YYYY-MM-DDTHH:mm:ss'" +
          "\nPlease also check that routeId and planeId are valid." +
          "\nAdditionally, please ensure that routeListing combination of route, plane, and depareture datetime does not yet exist already.");
          console.log("Did not create routeListing: " + response.status);
        }
      })
      .catch((error) => {
        alert("Failed to create routeListing.\nPlease check that Departure Date Time is in the correct format of 'YYYY-MM-DDTHH:mm:ss'" +
          "\nPlease also check that routeId and planeId are valid." +
          "\nAdditionally, please ensure that routeListing combination of route, plane, and depareture datetime does not yet exist already.");
        console.log("Did not create routeListing: " + error);
      })

    console.log("RouteId:", formData.routeId);
    console.log("PlaneId:", formData.planeId);
    console.log("DeparetureDateTime:", formData.departureDatetime);
    console.log("BasePrice:", formData.basePrice);
  };

  function getSearchedRouteListings() {
    const searchFields =
      searchFormData.departureDest + "/" +
      searchFormData.arrivalDest + "/" +
      searchFormData.year + "/" +
      searchFormData.month + "/" +
      searchFormData.day
    axios.get(apiUrl + "routeListings/fullSearch/" + searchFields)
      .then((response) => {
        // If the call fails, a html to the login page is sent back.
        const isResponseJsonType = response.headers.get('content-type')?.includes('application/json');
        if (response.data != null && isResponseJsonType) {
          setSearchedRouteListings(response.data)
        } else {
          setSearchedRouteListings([])
        }
      })
      .catch((error) => {
        console.log(error);
        setSearchedRouteListings([]);
      })
  }

  // TODO:
  // function onDelete(routeId) {
  //   axios.delete(apiUrl + "routes/delete/" + routeId)
  //     .then(() => {
  //       alert("Successfully deleted " + routeId + "and refreshed routes list.");
  //       getAllRoutes();
  //     })
  //     .catch((error) => {
  //       alert("Failed to delete " + routeId + ".\nPlease check that there are no dependent seatListings for this routeListing. Additonal error msg:\n" + error);
  //       console.log(error)
  //     })
  // }

  // TODO:
  // function onEdit(routeId) {
  //   let data = {
  //     currentRouteId: routeId
  //   };
  //   navigate("/adminPortal/routeListings/edit", { state: data });
  // }

  // Calls immediately upon page load
  useEffect(() => {
    if (isAuthenticated()) {
      axios.get(apiUrl + "users/adminAuthTest").then((response) => {
        // TODO: This isn't correctly reporting errors. Postman is 403, but here it's still 200.
        if (response.status === 200) {
          updateAuthHeadersFromCurrentUser();
          // We can't do anything. No defaults for search.
          // getSearchedRouteListings();
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
                <Typography variant="h4">Searched RouteListings (Count: {searchedRouteListings != null ? searchedRouteListings.length : 0})</Typography>
              </Box>
              <br />
                <Box sx={{
                  display: 'flex',
                  flexDirection: 'column',
                }}>
                  {searchedRouteListings.length > 0 ? (
                    searchedRouteListings.map((item, index) => (
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
                            {/* <Button onClick={() => { onEdit(item.routeId); }} variant="contained" color="secondary" */}
                            {/* sx={{paddingLeft: 3, paddingRight: 3,}}>EDIT</Button> */}
                            <Typography variant="h5" sx={{paddingLeft: 1}}>
                              RouteID: {item.routeId}-{item.planeId}
                              <br></br>
                              {item.departureDatetime}, duration: {item.flightDuration}
                              <br></br>
                              BasePrice: ${item.basePrice}, seatsLeft: {item.availableSeats}
                            </Typography>
                          </Box>
                          {/* <Button onClick={() => { onDelete(item.routeId); }} variant="contained" color="error">DELETE</Button> */}
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
              marginBottom: 20,
              alignItems: 'center',
            }}>

              <Typography variant="h5" sx={{marginBottom:2}}>Search Fields</Typography>

              <form 
                style={{
                  display: "flex",
                  flexDirection: "column",
                  alignItems: "center",
                  width: '100%',
                }}
              >
                <TextField fullWidth
                  style={{ marginBottom: "16px" }} // You can adjust the spacing
                  label="Departure Destination"
                  variant="outlined"
                  name="departureDest"
                  value={searchFormData.departureDest}
                  onChange={handleSearchInputChange}
                />
                <TextField fullWidth
                  style={{ marginBottom: "16px" }} // You can adjust the spacing
                  label="Arrival Destination"
                  variant="outlined"
                  name="arrivalDest"
                  value={searchFormData.arrivalDest}
                  onChange={handleSearchInputChange}
                />
                <TextField fullWidth
                  style={{ marginBottom: "16px" }} // You can adjust the spacing
                  label="Year"
                  variant="outlined"
                  name="year"
                  value={searchFormData.year}
                  onChange={handleSearchInputChange}
                />
                <TextField fullWidth
                  style={{ marginBottom: "16px" }} // You can adjust the spacing
                  label="Month"
                  variant="outlined"
                  name="month"
                  value={searchFormData.month}
                  onChange={handleSearchInputChange}
                />
                <TextField fullWidth
                  style={{ marginBottom: "16px" }} // You can adjust the spacing
                  label="Day"
                  variant="outlined"
                  name="day"
                  value={searchFormData.day}
                  onChange={handleSearchInputChange}
                />
                <Button onClick={getSearchedRouteListings} variant="contained">Refresh Searched RouteListings List</Button>
              </form>
            </Box>
          </Paper>

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
                  label="Route ID"
                  variant="outlined"
                  name="routeId"
                  value={formData.routeId}
                  onChange={handleInputChange}
                />
                <TextField fullWidth
                  style={{ marginBottom: "16px" }} // You can adjust the spacing
                  label="Plane ID"
                  variant="outlined"
                  name="planeId"
                  value={formData.planeId}
                  onChange={handleInputChange}
                />
                <TextField fullWidth
                  style={{ marginBottom: "16px" }} // You can adjust the spacing
                  label="Departure Date Time"
                  variant="outlined"
                  name="departureDatetime"
                  value={formData.departureDatetime}
                  onChange={handleInputChange}
                />
                <TextField fullWidth
                  style={{ marginBottom: "16px" }} // You can adjust the spacing
                  label="Base Price"
                  variant="outlined"
                  name="basePrice"
                  value={formData.basePrice}
                  onChange={handleInputChange}
                />
                <Button type="submit" variant="contained" color="primary" p={3} fullWidth>
                  Add RouteListing
                </Button>
              </form>

            </Box>
          </Paper>
        </Grid>
      </Grid>
    </Container>
  );
};

export default RouteListingUpdatingForm;
