import * as React from "react";
import Button from "@mui/material/Button";
import CssBaseline from "@mui/material/CssBaseline";
import TextField from "@mui/material/TextField";
import FormControlLabel from "@mui/material/FormControlLabel";
import Checkbox from "@mui/material/Checkbox";
import Link from "@mui/material/Link";
import Paper from "@mui/material/Paper";
import Box from "@mui/material/Box";
import Grid from "@mui/material/Grid";
import Typography from "@mui/material/Typography";
import { createTheme, ThemeProvider } from "@mui/material/styles";
import axios from "axios";
import { useState, useEffect } from "react";
import Alert from "@mui/material/Alert"
import AlertTitle from "@mui/material/AlertTitle"
import { setAuthToken, isAuthenticated, removeAuthToken } from "../auth";
import { useNavigate } from 'react-router-dom';


const defaultTheme = createTheme();

export default function SignInPage() {
  const apiUrl = process.env.REACT_APP_API_BASE_URL;
  const [formData, setFormData] = useState({
    username: "",
    password: "",
    rememberBoolean: false,
  });
  const [hasFailedLogin, setHasFailedLogin] = useState(false);

  const navigate = useNavigate();

  const handleChange = (e) => {
    const { name, type } = e.target;
    setFormData({
      ...formData,
      [name]: e.target[type === "checkbox" ? "checked" : "value"],
    });
  };

  const handleLoginSuccess = (jwtResponse) => {
    // Set JWT token in cookies or headers, including user data
    const adminUser = {
      username: formData.username,
      // Add other user-related data here
    };
    setAuthToken(jwtResponse.data, adminUser);

    // navigate to flightsearch if user already was at flight search, else homepage
    let redirectUrl = sessionStorage.getItem('redirectAfterLogin');
    if (redirectUrl) {
      navigate(redirectUrl);
    } else {
      navigate('/');
    }
    sessionStorage.removeItem('redirectAfterLogin')
  }

  const handleSubmit = async (e) => {
    //here just to not trigger errors
    // console.log(formData);
    e.preventDefault();

    try {
      const jwtResponse = await axios.post(
        apiUrl + "api/auth/token",
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
        setHasFailedLogin(true);
        console.log('JWT Response:', jwtResponse);
      }
    } catch (error) {
      setHasFailedLogin(true);
      console.error("Login failed", error);
    }
  };

  // Calls immediately upon page load
  useEffect(() => {
    if (isAuthenticated()) {
      axios.get(apiUrl + "users/authTest").then((response) => {
        if (response.status === 200) {
          navigate('/');
        } else {
          removeAuthToken();
        }
      }).catch((error) => {
        removeAuthToken();
      })
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);


  return (
    <ThemeProvider theme={defaultTheme}>
      <Grid container component="main" sx={{ height: "100vh" }}>
        <CssBaseline />
        <Grid
          item
          xs={false}
          sm={4}
          md={7}
          sx={{
            backgroundImage:
              "url(https://images.inc.com/uploaded_files/image/1920x1080/getty_543224919_124254.jpg)",
            backgroundRepeat: "no-repeat",
            backgroundColor: (t) =>
              t.palette.mode === "light"
                ? t.palette.grey[50]
                : t.palette.grey[900],
            backgroundSize: "cover",
            backgroundPosition: "center",
          }}
        />
        <Grid
          item
          xs={12}
          sm={8}
          md={5}
          component={Paper}
          elevation={6}
          square
          sx={{
            backgroundColor: "#143965",
            display: 'flex',
            flexDirection: 'column',
            justifyContent: 'center'
          }}
        >
          <Typography
            variant="h3"
            color="white"
            sx={{
              fontFamily: "Merriweather",
              fontSize: "100px",
              fontWeight: 500,
              textAlign: 'center',
              marginTop: "60px",
            }}
          >
            WingIt.
          </Typography>
          <Box
            sx={{
              my: 4,
              mx: 4,
              display: "flex",
              flexDirection: "column",
              alignItems: "center",
              backgroundColor: "white",
              borderRadius: "16px",
              height: "400px",
              padding: "2rem",
            }}
          >
            <Typography
              variant="h1"
              sx={{
                fontFamily: "Merriweather",
                fontSize: "1.5rem",
                fontWeight: "bold",
                marginTop: "auto",
                marginLeft: "0rem",
                marginRight: "auto",
              }}
            >
              Sign in
            </Typography>

            <Box
              component="form"
              noValidate
              onSubmit={handleSubmit}
              sx={{ mt: 1 }}
            >
              <TextField
                margin="normal"
                required
                fullWidth
                id="username"
                label="Username"
                name="username"
                autoComplete="username"
                autoFocus
                onChange={handleChange}
                value={formData.username}
              />
              <TextField
                margin="normal"
                required
                fullWidth
                name="password"
                label="Password"
                type="password"
                id="password"
                autoComplete="current-password"
                onChange={handleChange}
                value={formData.password}
              />
              <FormControlLabel
                control={
                  <Checkbox
                    type="checkbox"
                    name="rememberBoolean"
                    value={formData.rememberBoolean}
                    color="primary"
                    onChange={handleChange}
                  />
                }
                label="Remember me"
                sx={{ marginBottom: "-1rem" }}
              />
              <Button
                type="submit"
                fullWidth
                variant="contained"
                sx={{
                  mt: 3,
                  mb: 2,
                  fontSize: "14px",
                  backgroundColor: "#F58A07",
                }}
              >
                Sign In
              </Button>

              <Grid container justifyContent="space-between">
                <Grid item>
                  <Link href="#" variant="body2">
                    Forgot password?
                  </Link>
                </Grid>
                <Grid item>
                  <Link
                    href="/signup"
                    variant="body2"
                  >
                    {"Don't have an account? Sign Up"}
                  </Link>
                </Grid>
              </Grid>
            </Box>
          </Box>
          {hasFailedLogin &&
            <Box sx={{
              display: "flex",
              flexDirection: "column",
              alignItems: "center",
            }}>
              < Alert severity="error" fullWidth>
                <AlertTitle>Error</AlertTitle>
                Sign in failed. Please check your username and password.
              </Alert>
            </Box>
          }
        </Grid>
      </Grid>
    </ThemeProvider>
  );
}
