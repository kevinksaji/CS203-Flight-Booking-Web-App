import React, { useState, useEffect } from "react";
import axios from 'axios';
import { useNavigate } from 'react-router-dom';

import { Typography, TextField, Button, Card, Grid, Container } from "@mui/material";
import { setAuthToken, isAuthenticated, removeAuthToken } from "../auth";

const AdminPortalLogin = () => {
  const apiUrl = process.env.REACT_APP_API_BASE_URL;
  const [formData, setFormData] = useState({
    username: "",
    password: "",
  });
  const [errorMsg, setErrorMsg] = useState(String);



  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData({
      ...formData,
      [name]: value,
    });
  };

  const navigate = useNavigate();
  const handleLoginSuccess = (jwtResponse) => {
    // Set JWT token in cookies or headers, including user data
    const adminUser = {
      username: formData.username,
      // Add other user-related data here
    };
    setAuthToken(jwtResponse.data, adminUser);


    navigate('/adminPortal/home');
  }

  const handleSubmit = async (e) => {
    e.preventDefault();

    try {
      const jwtResponse = await axios.post(
        apiUrl + "api/auth/adminToken",
        {},
        {
          auth: {
            username: formData.username,
            password: formData.password,
          },
        }
      );
      if (jwtResponse.status === 200) {
        handleLoginSuccess(jwtResponse);
      } else {
        setErrorMsg("Login failed: " + jwtResponse.status);
        console.log('JWT Response:', jwtResponse);
      }
    } catch (error) {
      setErrorMsg("Login failed: " + error);
      console.error("Login failed", error);
    }
  };

  // Calls immediately upon page load
  useEffect(() => {
    if (isAuthenticated()) {
      axios.get(apiUrl + "users/adminAuthTest").then((response) => {
        // TODO: This isn't correctly reporting errors. Postman is 403, but here it's still 200.
        if (response.status === 200) {
          navigate('/adminPortal/home');
        } else {
          setErrorMsg("ERROR, Please login again");
          removeAuthToken();
        }
      }).catch((error) => {
        setErrorMsg("LOGGED IN AS NON ADMIN USER");
        removeAuthToken();
      })
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
              <Typography variant="h3" fontWeight="bold">
                ADMIN PORTAL
              </Typography>

              <Typography variant="body2" style={{ color: 'red', paddingBottom: 20, }}>{errorMsg}</Typography>

              <TextField fullWidth
                style={{ marginBottom: "16px" }} // You can adjust the spacing
                label="Username"
                variant="outlined"
                name="username"
                value={formData.username}
                onChange={handleInputChange}
              />
              <TextField fullWidth
                style={{ marginBottom: "16px" }} // You can adjust the spacing
                label="Password"
                variant="outlined"
                type="password"
                name="password"
                value={formData.password}
                onChange={handleInputChange}
              />
              <Button type="submit" variant="contained" color="primary" p={3} fullWidth>
                LOGIN
              </Button>
            </form>
          </Card>
        </Grid>
      </Grid>
    </Container>
  );
};

export default AdminPortalLogin;
