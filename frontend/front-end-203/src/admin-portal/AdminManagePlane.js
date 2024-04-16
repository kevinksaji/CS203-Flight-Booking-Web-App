import React, { useState, useEffect } from "react";
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import { isAuthenticated, removeAuthToken, updateAuthHeadersFromCurrentUser } from "../auth";

import { Typography, Box, TextField, Button, Paper, Container, Grid } from "@mui/material";

const PlaneUpdatingForm = () => {
  const apiUrl = process.env.REACT_APP_API_BASE_URL;
  const [formData, setFormData] = useState({
    planeId: "",
    capacity: "",
    model: "",
  });
  const [allPlanes, setAllPlanes] = useState([]);
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

    const ERR_MSG = "Please check that there are no duplicate Plane IDs.\n\nAlso, plane capacity must be >= 30 and multiple of 6.";
    axios.post(apiUrl + "planes/newWithSeats", formData)
      .then((response) => {
        if (response.status === 201) {
          getAllPlanes();
          alert("Successfully added " + formData.planeId);
        } else {
          alert("Failed to create " + formData.planeId + ".\n" + ERR_MSG);
          console.log("Did not create plane: " + response.status);
        }
      })
      .catch((error) => {
        alert("Failed to create " + formData.planeId + ".\n" + ERR_MSG);
        console.log("Did not create plane: " + error);
      })

    // You can access the values of planeId, capacity, and model from formData
    console.log("Plane ID:", formData.planeId);
    console.log("Capacity:", formData.capacity);
    console.log("Model:", formData.model);
  };

  function getAllPlanes() {
    axios.get(apiUrl + "planes")
      .then((response) => {
        // If the call fails, a html to the login page is sent back.
        const isResponseJsonType = response.headers.get('content-type')?.includes('application/json');
        if (response.data != null && isResponseJsonType) {
          setAllPlanes(response.data)
        } else {
          setAllPlanes([])
        }
      })
      .catch((error) => {
        console.log(error);
        setAllPlanes([]);
      })
  }

  function onDelete(planeId) {
    // TODO: delete by planeId and then also call get all planes again.
    axios.delete(apiUrl + "planes/delete/" + planeId)
      .then(() => {
        alert("Successfully deleted " + planeId + "and refreshed planes list.");
        getAllPlanes();
      })
      .catch((error) => {
        alert("Failed to delete " + planeId + ".\nPlease check that there are no dependent routeListings for this plane. Additonal error msg:\n" + error);
        console.log(error)
      })
  }

  function onEdit(planeId) {
    let data = {
      currentPlaneId: planeId
    };
    navigate("/adminPortal/planes/edit", { state: data });
  }

  // Calls immediately upon page load
  useEffect(() => {
    if (isAuthenticated()) {
      axios.get(apiUrl + "users/adminAuthTest").then((response) => {
        // TODO: This isn't correctly reporting errors. Postman is 403, but here it's still 200.
        if (response.status === 200) {
          updateAuthHeadersFromCurrentUser();
          getAllPlanes();
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
            <Typography variant="h3">Planes Management</Typography>
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
                <Typography variant="h4">All Planes (Count: {allPlanes != null ? allPlanes.length : 0})</Typography>
                <Button onClick={getAllPlanes} variant="contained">Refresh Planes List</Button>
              </Box>
              <br />
              <ol>
                <Box sx={{
                  display: 'flex',
                  flexDirection: 'column',
                }}>
                  {allPlanes.length > 0 ? (
                    allPlanes.map((item, index) => (
                      <li key={item.planeId} style={{
                        fontSize: '24px',
                        fontFamily: 'Sans-Serif',
                        fontWeight: 'bold',
                        backgroundColor: index % 2 === 0 ? ("#F0F0F0") : ("#FFFFFF"),
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
                            <Button onClick={() => { onEdit(item.planeId); }} variant="contained" color="secondary"
                            sx={{paddingLeft: 3, paddingRight: 3,}}>EDIT</Button>
                            <Typography variant="h5" sx={{paddingLeft: 1}}>
                              {item.planeId}-{item.model} ({item.capacity})
                            </Typography>
                          </Box>
                          <Button onClick={() => { onDelete(item.planeId); }} variant="contained" color="error">DELETE</Button>
                        </Box>
                      </li>
                    ))) : (
                    <p></p>
                  )}
                </Box>
              </ol>
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
                  label="Plane ID"
                  variant="outlined"
                  name="planeId"
                  value={formData.planeId}
                  onChange={handleInputChange}
                />
                <TextField fullWidth
                  style={{ marginBottom: "16px" }} // You can adjust the spacing
                  label="Capacity"
                  variant="outlined"
                  name="capacity"
                  value={formData.capacity}
                  onChange={handleInputChange}
                />
                <TextField fullWidth
                  style={{ marginBottom: "16px" }} // You can adjust the spacing
                  label="Model"
                  variant="outlined"
                  name="model"
                  value={formData.model}
                  onChange={handleInputChange}
                />
                <Button type="submit" variant="contained" color="primary" p={3} fullWidth>
                  Add Plane
                </Button>
              </form>

            </Box>
          </Paper>
        </Grid>
      </Grid>
    </Container>
  );
};

export default PlaneUpdatingForm;
