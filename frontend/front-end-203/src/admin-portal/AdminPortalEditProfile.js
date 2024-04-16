import React, { useState } from "react";
import { TextField, Button, Paper, Container } from "@mui/material";

const MyForm = () => {
  // const apiUrl = process.env.REACT_APP_API_BASE_URL;
  // const initialFormData =
  //   //loadData()
  //   {
  //     firstname: "",
  //     lastname: "",
  //   };

  // function loadData() {
  //   try {
  //     const response = axios.get(apiUrl + "users/<username>").then((response) => {
  //       formData.firstname = response.data.firstname
  //       formData.lastname = response.data.lastname
  //     })
  //   } catch (error) {
  //     console.error("failed to get data", error)
  //   }
  // }

  const [formData, setFormData] = useState({
    firstname: "",
    lastname: "",
  });

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData({
      ...formData,
      [name]: value,
    });
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    // You can access the values of firstname and lastname from formData
    console.log("Input 1:", formData.firstname);
    console.log("Input 2:", formData.lastname);
    //UNCOMMENT when doing real logic
    // try {
    //   const response = await axios.post(apiUrl + "/users/update/<username>", formData);

    //   if (response.status === 201) {
    //     console.log("Update Successful! HTTP 201");
    //     alert("Update Successful!");
    //   }else {
    //     console.log("Update failed:", response.data.error);
    //   }
    // } catch (error) {
    //   console.error("Update failed", error);
    // }
  };

  return (
    <Container>
      <Paper elevation={3}>
        <form
          style={{
            display: "flex",
            flexDirection: "column",
            alignItems: "center",
            marginTop: "16px", // You can adjust the spacing
            padding: "16px", // You can adjust the spacing
          }}
          onSubmit={handleSubmit}
        >
          <TextField
            style={{ marginBottom: "16px" }} // You can adjust the spacing
            label="Firstname"
            variant="outlined"
            name="firstname"
            value={formData.firstname}
            onChange={handleInputChange}
          />
          <TextField
            style={{ marginBottom: "16px" }} // You can adjust the spacing
            label="Lastname"
            variant="outlined"
            name="lastname"
            value={formData.lastname}
            onChange={handleInputChange}
          />
          <Button type="submit" variant="contained" color="primary">
            Submit
          </Button>
        </form>
      </Paper>
    </Container>
  );
};

export default MyForm;
