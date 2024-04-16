import React from "react";
import { useNavigate } from "react-router-dom";
import { useState } from "react";
import {
  CardNumberElement,
  CardExpiryElement,
  CardCvcElement,
  // useStripe,
  // useElements,
} from "@stripe/react-stripe-js";

import { Button, Box, TextField, Typography } from "@mui/material";
import { ThemeProvider, createTheme } from "@mui/material";
import axios from "axios";
import {
  getCurrentUser,
  isAuthenticated,
  updateAuthHeadersFromCurrentUser,
  removeAuthToken,
} from "../auth";

function PaymentForm() {
  const navigate = useNavigate();
  const bookingId = sessionStorage.getItem("bookingId");

  const handleProceedToPayment = () => {
    // Navigate to the confirmation component
    const apiUrl = process.env.REACT_APP_API_BASE_URL;
    console.log("This is booking id: " + bookingId);
    const url1 = apiUrl + `bookings/markAsPaid/${bookingId}`; // The endpoint you're sending the request to

    if (isAuthenticated()) {
      axios
        .get(apiUrl + "users/authTest")
        .then((response) => {
          // TODO: This isn't correctly reporting errors. Postman is 403, but here it's still 200.
          if (response.status === 200) {
            updateAuthHeadersFromCurrentUser();
            
            // put request to backend for updating markAsPaid to true
            axios
              .put(url1)
              .then((response) => {
                console.log("Data updated successfully hehe:", response.data);
              })
              .catch((error) => {
                console.error("Error updating data:", error);
              });
            navigate("/confirmation");

            const username = getCurrentUser().username;

            // just to display bookings of the user to verify that the markAsPaid is set to true on click of the confirm and pay button
            const url2 = apiUrl + `bookings/${username}`;
            console.log("This is username hehe: " + username);
            axios
              .get(url2)
              .then((response) => {
                console.log("This is booking data", response.data);
              })
              .catch((error) => {
                console.error("Error getting booking data:", error);
              });
          } else {
            removeAuthToken();
            navigate("/signin");
            console.log("made an error at flightSearch 51");
          }
        })
        .catch((error) => {
          removeAuthToken();
          navigate("/signin");
          console.log(error);
        });
    } else {
      navigate("/signin");
      console.log("made an error at flightSearch 61");
    }

    // previous code for reference
    // // put request to backend for updating markAsPaid to true
    // axios.put(url1)
    //   .then(response => {
    //     console.log('Data updated successfully hehe:', response.data);
    //   })
    //   .catch(error => {
    //     console.error('Error updating data:', error);
    //   });
    //   navigate("/confirmation");

    //   const username = getCurrentUser().username;

    //   // just to display bookings of the user to verify that the markAsPaid is set to true on click of the confirm and pay button
    //   const url2 = apiUrl + `bookings/${username}`
    //   console.log("This is username hehe: " + username);
    //   axios.get(url2)
    //   .then(response => {
    //     console.log('This is booking data', response.data);
    //   })
    //   .catch(error => {
    //     console.error('Error getting booking data:', error);
    //   });
  };

  const [hasText, setHasText] = useState(false);

  const handleTextChange = (e) => {
    setHasText(e.target.value !== "");
  };

  const CARD_ELEMENT_OPTIONS = {
    style: {
      base: {
        color: "black",
        fontFamily: '"Helvetica Neue", Helvetica, sans-serif',
        fontSmoothing: "antialiased",
        fontSize: "16px",
        backgroundColor: "white",
        "::placeholder": {
          color: "grey",
        },
      },
      invalid: {
        color: "red",
        iconColor: "red",
      },
    },
  };

  const customTheme = createTheme({
    components: {
      MuiOutlinedInput: {
        styleOverrides: {
          root: {
            "&:hover .MuiOutlinedInput-notchedOutline": {
              borderColor: "white", // Border color when hovered
            },
            "&.Mui-focused .MuiOutlinedInput-notchedOutline": {
              borderColor: "white", // Border color when focused
              borderWidth: "1px",
            },
          },
          notchedOutline: {
            borderColor: "white", // Default border color
          },
          input: {
            color: "black", // This will change the input text color to white
          },
        },
      },
      MuiInputLabel: {
        styleOverrides: {
          outlined: {
            color: "grey", // Label color
            "&.Mui-focused": {
              color: "grey", // Label color when input is focused
            },
          },
        },
      },
      MuiInputBase: {
        styleOverrides: {
          input: {
            color: "black",
          },
        },
      },
    },
  });
  // const stripe = useStripe();
  // const elements = useElements();
  //

  // const handleSubmit = async (event) => {
  //   event.preventDefault();
  //
  //   if (!stripe || !elements) {
  //     return;
  //   }
  //
  //   const cardNumberElement = elements.getElement(CardNumberElement);
  //   const { error, paymentMethod } = await stripe.createPaymentMethod({
  //     type: "card",
  //     card: cardNumberElement,
  //   });
  //
  //   if (error) {
  //     console.log(error)
  //   } else {
  //     console.log(paymentMethod);
  //   }
  // };

  return (
    <Box
      padding={5}
      bgcolor="#223662"
      width="400px"
      borderRadius={"1rem"}
      marginLeft={"12rem"}
      marginTop={"4rem"}
      marginBottom={"4rem"}
    >
      <Typography
        variant="h5"
        color={"white"}
        sx={{ fontFamily: "Merriweather Sans" }}
      >
        Payment Method
      </Typography>
      <Typography
        variant="body2"
        color={"white"}
        sx={{ fontFamily: "Merriweather Sans", marginY: "0.75rem" }}
      >
        Select a payment method below. WingIt processes your payment securely
        with end-to-end encryption.
      </Typography>
      <Button
        sx={{
          borderColor: "#FF9B00",
          borderWidth: "2px",
          padding: "10px 30px",
          backgroundColor: "darkOrange",
          fontFamily: "Merriweather Sans",
          color: "white",
          textTransform: "none",
          "&:hover": {
            backgroundColor: "#FF9B00", // replace with your desired color
          },
        }}
      >
        Credit card
      </Button>

      <Typography
        variant="h6"
        color={"white"}
        marginY={2}
        sx={{ fontFamily: "Merriweather Sans", marginTop: "1.5rem" }}
      >
        Credit card details
      </Typography>

      <Box marginBottom={2}>
        <ThemeProvider theme={customTheme}>
          <TextField
            fullWidth
            margin="normal"
            variant="outlined"
            label={!hasText ? "Name on card" : null}
            sx={{
              fontFamily: "Merriweather Sans",
              backgroundColor: "white",
              borderRadius: "4px",
            }}
            onChange={handleTextChange}
            InputLabelProps={{ shrink: false }}
          />
        </ThemeProvider>
      </Box>

      <Box marginBottom={2}>
        <div
          style={{
            border: "1px solid white",
            borderRadius: "4px",
            padding: "1rem",
            backgroundColor: "white",
          }}
        >
          <CardNumberElement options={CARD_ELEMENT_OPTIONS} />
        </div>
      </Box>

      <Box display="flex" justifyContent="space-between" marginBottom={2}>
        <Box width="60%" marginRight={1}>
          <div
            style={{
              border: "1px solid white",
              borderRadius: "4px",
              padding: "1rem",
              backgroundColor: "white",
            }}
          >
            <CardExpiryElement options={CARD_ELEMENT_OPTIONS} />
          </div>
        </Box>
        <Box width="35%">
          <div
            style={{
              border: "1px solid white",
              borderRadius: "4px",
              padding: "1rem",
              backgroundColor: "white",
            }}
          >
            <CardCvcElement options={CARD_ELEMENT_OPTIONS} />
          </div>
        </Box>
      </Box>
      <Button
        sx={{
          borderColor: "#FF9B00",
          borderWidth: "2px",
          padding: "10px 30px",
          backgroundColor: "darkOrange",
          fontFamily: "Merriweather Sans",
          color: "white",
          textTransform: "none",
          "&:hover": {
            backgroundColor: "#FF9B00", // replace with your desired color
          },
        }}
        onClick={handleProceedToPayment}
      >
        Confirm and pay
      </Button>
    </Box>
  );
}

export default PaymentForm;
