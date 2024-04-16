import * as React from "react";
import Card from "@mui/material/Card";
import CardContent from "@mui/material/CardContent";
import CardMedia from "@mui/material/CardMedia";
import Grid from "@mui/material/Grid";
import Typography from "@mui/material/Typography";
import Container from "@mui/material/Container";
import { createTheme, ThemeProvider } from "@mui/material/styles";

const destinations = [
  { name: "Japan", price: "From $770", image: "https://rimage.gnst.jp/livejapan.com/public/article/detail/a/00/02/a0002487/img/basic/a0002487_main.jpg?20230106161700" },
  { name: "Singapore", price: "From $504", image: "https://images.unsplash.com/photo-1565967511849-76a60a516170?auto=format&fit=crop&q=80&w=1000&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8OHx8c2luZ2Fwb3JlfGVufDB8fDB8fHww" },
  { name: "Taiwan", price: "From $606", image: "https://images.unsplash.com/photo-1621584728858-616080e4bd01?auto=format&fit=crop&q=80&w=1000&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8OHx8dGFpd2FufGVufDB8fDB8fHww" },
  { name: "India", price: "From $627", image: "https://i.natgeofe.com/k/42e832f5-fd48-43ff-b338-091bdf4048ca/india-tajmahal_16x9.jpg?w=1200" },
  { name: "China", price: "From $858", image: "https://t3.ftcdn.net/jpg/01/00/40/82/360_F_100408242_ODbvc2HHZOC5YtSIdJrXlWxLKapI2iEG.jpg" },
  { name: "Malaysia", price: "From $420", image: "https://www.chariotworldtours.com/wp-content/uploads/2017/08/malaysia.png" },
];

const defaultTheme = createTheme();

export default function Album() {
  return (
    <ThemeProvider theme={defaultTheme}>
      <Container sx={{ py: 8 }} maxWidth="md" disableGutters={true}>
        <Grid container spacing={4}>
          {destinations.map((destination) => (
            <Grid item key={destination.name} xs={12} sm={6} md={4}>
              <Card
                sx={{
                  height: "100%",
                  display: "flex",
                  flexDirection: "column",
                }}
              >
                <CardMedia
                  component="div"
                  sx={{
                    height: 200,
                    backgroundSize: "cover",
                    backgroundImage: `url(${destination.image})`,
                  }}
                />
                <CardContent sx={{ flexGrow: 1 }}>
                  <Typography variant="h5" component="h2">
                    {destination.name}
                  </Typography>
                  <Typography variant="subtitle1">{destination.price}</Typography>
                </CardContent>
              </Card>
            </Grid>
          ))}
        </Grid>
      </Container>
    </ThemeProvider>
  );
}
