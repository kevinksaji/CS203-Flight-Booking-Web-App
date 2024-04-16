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
import Select from "@mui/material/Select";
import MenuItem from "@mui/material/MenuItem";
import FormControl from "@mui/material/FormControl";
import InputLabel from "@mui/material/InputLabel";
import Alert from "@mui/material/Alert"
import AlertTitle from "@mui/material/AlertTitle"
import { isAuthenticated, removeAuthToken } from "../auth";
import { useNavigate } from 'react-router-dom';


import React, { useState, useEffect } from "react";
import axios from "axios";

const defaultTheme = createTheme();

const isValidEmail = (email) => {
  // Using regex for basic email validation
  const regex = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,6}$/;
  return regex.test(email);
};

const isValidPhone = (phone) => {
  const regex = /^\d{8,8}$/;
  return regex.test(phone);
};

const isValidName = (name) => {
  // Allow only alphabetic characters
  const regex = /^[a-zA-Z\s]+$/;
  return regex.test(name);
};

const isValidDOB = (dob) => {
  const today = new Date();
  const birthDate = new Date(dob);
  return birthDate <= today;
};



export default function SignUpPage() {
  const apiUrl = process.env.REACT_APP_API_BASE_URL;
  const [formData, setFormData] = useState({
    username: "",
    password: "",
    firstName: "",
    lastName: "",
    dob: "",
    email: "",
    phone: "",
    salutation: "",
    hasAgreedToTerms: false,
  });
  const [signupSuccess, setSignupSuccess] = useState(false);
  const navigate = useNavigate();

  const [errorMessage, setErrorMessage] = useState("");

  const [confirmPassword, setConfirmPassword] = useState("");



  const handleChange = (e) => {
    const { name, value, type, checked } = e.target;
    const newValue = type === "checkbox" ? checked : value;
    setFormData({ ...formData, [name]: newValue });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!isValidEmail(formData.email)) {
      setErrorMessage("Invalid email format.");
      return;
    }

    if (!isValidPhone(formData.phone)) {
      setErrorMessage("Invalid phone number.");
      return;
    }

    if (!isValidName(formData.firstName) || !isValidName(formData.lastName)) {
      setErrorMessage("Names should only contain alphabetic characters.");
      return;
    }

    if (!isValidDOB(formData.dob)) {
      setErrorMessage("Date of birth cannot be in the future.");
      return;
    }



    if (!formData.hasAgreedToTerms) {
      setErrorMessage('Please agree to the terms before signing up.');
      return; // This will exit the function without proceeding to the sign-up process.
    }

    if (formData.password !== confirmPassword) {
      setErrorMessage("Passwords do not match.");
      return;
  }
  

    try {
      // Send a POST request to your backend API endpoint for sign-up
      const response = await axios.post(apiUrl + "users/new", formData);

      if (response.status === 201) {
        // Sign-up successful
        console.log('Sign-up successful: HTTP 201');
        setSignupSuccess(true);
        setErrorMessage(""); //to clear away the error msg after signing up is successful
      } else {
        // Handle other possible responses, e.g., display error messages
        console.log("Sign-up failed:", response.status);
      }
    } catch (error) {
      // Handle errors (e.g., show an error message)
      console.error("Sign-up failed", error);
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
      <Grid container component="main" sx={{ height: '100vh' }}>
        <CssBaseline />
        <Grid item xs={false} sm={4} md={7}
          sx={{
            backgroundImage: 'url(https://images.inc.com/uploaded_files/image/1920x1080/getty_543224919_124254.jpg)',
            backgroundRepeat: 'no-repeat',
            backgroundColor: (t) => t.palette.mode === 'light' ? t.palette.grey[50] : t.palette.grey[900],
            backgroundSize: 'cover',
            backgroundPosition: 'center',
          }} />
        <Grid item xs={12} sm={8} md={5} component={Paper} elevation={6} square sx={{ backgroundColor: '#143965' }}>
          <Typography variant="h3" color="white"
            sx={{
              fontFamily: 'Merriweather',
              fontSize: '100px',
              fontWeight: 500,
              marginTop: '60px',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
            }}>
            WingIt.
          </Typography>
          <Box sx={{
            my: 4,
            mx: 4,
            display: 'flex',
            flexDirection: 'column',
            alignItems: 'center',
            backgroundColor: 'white',
            borderRadius: '16px',
            padding: '2rem',
          }}>
            <Typography variant="h1" sx={{ fontFamily: 'Merriweather', fontSize: '1.5rem', fontWeight: 'bold', marginTop: 'auto', marginLeft: '0rem', marginRight: 'auto', marginBottom: '0.5rem' }}>
              Sign up
            </Typography>
            <Box component="form" noValidate onSubmit={handleSubmit} sx={{ mt: 1 }}>
              <Grid container spacing={2}>
                <Grid item xs={6} sm={4} md={3}>
                  <FormControl fullWidth required>
                    <InputLabel id="salutation-label">Salutation</InputLabel>
                    <Select
                      labelId="salutation-label"
                      id="salutation"
                      name="salutation"
                      value={formData.salutation}
                      onChange={handleChange}
                      label="Salutation"
                    >
                      <MenuItem value={"Mr"}>Mr</MenuItem>
                      <MenuItem value={"Mrs"}>Mrs</MenuItem>
                      <MenuItem value={"Miss"}>Ms</MenuItem>
                      <MenuItem value={"Mdm"}>Mdm</MenuItem>
                      <MenuItem value={"Master"}>Master</MenuItem>
                    </Select>
                  </FormControl>
                </Grid>
                <Grid item xs={12} sm={4.5}>
                  <TextField
                    autoComplete="given-name"
                    name="firstName"
                    required
                    fullWidth
                    id="firstName"
                    label="First Name"
                    autoFocus
                    value={formData.firstName}
                    onChange={handleChange}
                  />
                </Grid>
                <Grid item xs={12} sm={4.5}>
                  <TextField
                    required
                    fullWidth
                    id="lastName"
                    label="Last Name"
                    name="lastName"
                    autoComplete="family-name"
                    value={formData.lastName}
                    onChange={handleChange}
                  />
                </Grid>
                <Grid item xs={12} >
                  <TextField
                    required
                    fullWidth
                    id="email"
                    label="Email Address"
                    name="email"
                    autoComplete="email"
                    value={formData.email}
                    onChange={handleChange}
                  />
                </Grid>
                <Grid item xs={12}>
                  <TextField
                    required
                    fullWidth
                    id="phone"
                    label="Phone Number"
                    name="phone"
                    value={formData.phone}
                    onChange={handleChange}
                  />
                </Grid>
                <Grid item xs={12}>
                  <TextField
                    required
                    fullWidth
                    id="username"
                    label="Username"
                    name="username"
                    value={formData.username}
                    onChange={handleChange}
                  />
                </Grid>
                <Grid item xs={12}>
                  <TextField
                    required
                    fullWidth
                    name="password"
                    label="Password"
                    type="password"
                    id="password"
                    autoComplete="new-password"
                    value={formData.password}
                    onChange={handleChange}
                  />
                </Grid>
                <Grid item xs={12}>
                  <TextField
                    required
                    fullWidth
                    name="confirmPassword"
                    label="Confirm Password"
                    type="password"
                    id="confirmPassword"
                    value={confirmPassword}
                    onChange={(e) => setConfirmPassword(e.target.value)}
                  />
                </Grid>

                <Grid item xs={12}>
                  <TextField
                    required
                    fullWidth
                    id="dob"
                    label="Date of Birth"
                    name="dob"
                    type="date"
                    value={formData.dob}
                    onChange={handleChange}
                    InputLabelProps={{
                      shrink: true,
                    }}
                  />
                </Grid>


                <Grid item xs={12}>
                  <FormControlLabel
                    control={<Checkbox name="hasAgreedToTerms" color="primary" checked={formData.hasAgreedToTerms} onChange={handleChange} />} // Updated name and added checked prop
                    label={
                      <span>
                        I agree to all the{' '}
                        <span style={{ color: 'orange', textDecoration: 'underline' }}>Terms and Privacy Policies</span>
                      </span>
                    }
                  />
                </Grid>
                <Grid item xs={12}>
                  {errorMessage && (
                    <Alert severity="error" sx={{ mt: 2 }}>
                      {errorMessage}
                    </Alert>
                  )}
                </Grid>

                <Grid item xs={12}>
                  {signupSuccess ? (
                    <Alert severity="success" fullWidth>
                      <AlertTitle>Success</AlertTitle>
                      Account created! <br />
                      <strong>
                        Click <Link href="/signin">here</Link> to sign in!
                      </strong>
                    </Alert>
                  ) : (
                    <Button type="submit" fullWidth variant="contained" sx={{ mt: 3, mb: 2, fontSize: '14px', backgroundColor: '#F58A07', }}>
                      Sign Up
                    </Button>
                  )}
                </Grid>
                <Grid container justifyContent="flex-end">
                  <Grid item>
                    <Link href="/signin" variant="body2">
                      Already have an account? Sign in
                    </Link>
                  </Grid>
                </Grid>
              </Grid>
            </Box>
          </Box>
        </Grid>
      </Grid>
    </ThemeProvider>
  );
}
