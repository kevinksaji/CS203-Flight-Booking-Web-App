import React, { useState, useEffect } from "react";
import axios from "axios";
import "./FareSummary.css";
import { useNavigate } from "react-router-dom";
import {
  isAuthenticated,
  removeAuthToken,
  updateAuthHeadersFromCurrentUser,
} from "../../auth";

const FareSummary = ({ passengers, tripType, bookingId }) => {
  const retrievedData = JSON.parse(sessionStorage.getItem("selectedFlights"));
  const passengerData = JSON.parse(sessionStorage.getItem("passengerData"));
  console.log("This is passsengers");
  console.log(passengers);

  console.log("This is tripType");
  console.log(tripType);

  console.log("This is booking ID");
  console.log(bookingId);

  console.log("This is retrieved data");
  console.log(retrievedData);
  console.log("This is passenger data");
  console.log(passengerData);

  const [totalChargedPrice, setTotalChargedPrice] = useState(0);
  const [seatPrices, setSeatPrices] = useState({});

  const apiUrl = process.env.REACT_APP_API_BASE_URL;
  const navigate = useNavigate();

  //authentication + api call
  useEffect(() => {
    if (isAuthenticated()) {
      axios
        .get(apiUrl + "users/authTest")
        .then((response) => {
          // TODO: This isn't correctly reporting errors. Postman is 403, but here it's still 200.
          if (response.status === 200) {
            updateAuthHeadersFromCurrentUser();
            const fetchChargedPrice = async () => {
              try {
                const url1 =
                  apiUrl + `bookings/calculateChargedPrice/${bookingId}`;
                const response = await axios.put(url1);
                setTotalChargedPrice(response.data.totalChargedPrice);
                console.log("Response from backend:", response.data);
              } catch (error) {
                console.error("Failed to fetch charged price:", error);
              }
            };

            fetchChargedPrice();

            const fetchSeatPrices = async () => {
              try {
                const response = await axios.get(apiUrl + `bookings/getPriceBreakdown/${bookingId}`);
                setSeatPrices(response.data);
                console.log("seat prices lol")
                console.log("Response from backend:", response.data);
              } catch (error) {
                console.error("Failed to fetch seat prices:", error);
              }
            };

            fetchSeatPrices();


            // Set occupant to reserved seat for each passenger
            async function updateSeatsSequentially() {
              passengers.forEach((passenger) => {
                const payload1 = {
                  planeId: retrievedData.departureFlight.planeId,
                  routeId: retrievedData.departureFlight.routeId,
                  departureDatetime:
                    retrievedData.departureFlight.departureDatetime,
                  seatNumber: passenger.outboundSeat,
                  bookingId: bookingId,
                  occupantName: `${passenger.firstName} ${passenger.lastName}`,
                };
                axios
                  .put(
                    apiUrl + `seatListings/bookSeat/setOccupant/${bookingId}`,
                    payload1
                  )
                  .then((res) => {
                    console.log("Set occupant dep success:", res.data);
                  })
                  .catch((error) => {
                    console.error("Failed to set dep occupant:", error);
                  });
                if (tripType === "Return") {
                  const payload2 = {
                    planeId: retrievedData.returnFlight.planeId,
                    routeId: retrievedData.returnFlight.routeId,
                    departureDatetime:
                      retrievedData.returnFlight.departureDatetime,
                    seatNumber: passenger.returnSeat,
                    bookingId: bookingId,
                    occupantName: `${passenger.firstName} ${passenger.lastName}`,
                  };
                  axios
                    .put(
                      apiUrl + `seatListings/bookSeat/setOccupant/${bookingId}`,
                      payload2
                    )
                    .then((res) => {
                      console.log("Set occupant ret success:", res.data);
                    })
                    .catch((error) => {
                      console.error("Failed to set ret occupant:", error);
                    });
                }
              });
            }
            updateSeatsSequentially();
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
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  // previous useeffect code for reference
  // useEffect(() => {
  //     const fetchChargedPrice = async () => {
  //         try {
  //             const url1 = apiUrl + `bookings/calculateChargedPrice/${bookingId}`;
  //             const response = await axios.put(url1);
  //             setTotalChargedPrice(response.data.totalChargedPrice);
  //             console.log('Response from backend:', response.data);

  //         } catch (error) {
  //             console.error("Failed to fetch charged price:", error);
  //         }
  //     };

  //     fetchChargedPrice();
  // }, []);

  console.log("Total Charged Price:", totalChargedPrice);

  return (
    <div className="fare-summary">
      <h3>Fare Summary</h3>

      <table>
        <thead>
          <tr>
            <th>Passengers' Name:</th>
            <th>Seat Number:</th>
            <th>Price:</th>
          </tr>
        </thead>
        <tbody>
          {Array.isArray(passengers) &&
            passengers.map((passenger, index) => (
              <tr key={index}>
                <td>{`${passenger.salutation} ${passenger.firstName} ${passenger.lastName}`}</td>
                <td>
                  {`Outbound: ${passenger.outboundSeat}`}
                  {tripType === "Return" && `, Return: ${passenger.returnSeat}`}
                </td>
                <td>
                  {`Outbound: $${(seatPrices[`OUTBOUND-${passenger.outboundSeat}`]?.SeatPrice || 0).toFixed(2)}`}
                  {tripType === "Return" && `, Return: $${(seatPrices[`INBOUND-${passenger.returnSeat}`]?.SeatPrice || 0).toFixed(2)}`}
                </td>


              </tr>
            ))}
        </tbody>

      </table>

      <div className="subtotal">
        <strong>Total Fare: </strong>${totalChargedPrice.toFixed(2)}
      </div>
    </div>
  );
};

export default FareSummary;
