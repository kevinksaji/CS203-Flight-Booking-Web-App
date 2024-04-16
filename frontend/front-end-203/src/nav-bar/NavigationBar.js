import { React } from "react";
import "./NavigationBar.css"; // Import your CSS file for styling
import NavButton from "../nav-button/NavButton"; // Import the Button component
import ProfileIcon from "../profile-icon/ProfileIcon"; // Import the ProfileIcon component
import LogoButton from "../flight-search/logo-button/LogoButton";
import { isAuthenticated } from "../auth";
import { useNavigate } from "react-router-dom";

const NavigationBar = () => {
  const navigate = useNavigate();

  const handleSignIn = (e) => {
    console.log("routing to sign in");
    navigate("/signin");
  };

  const handleBookings = (e) => {
      navigate("/bookinghistory")
  }

  const handleFlights = (e) => {
    navigate("/")
}

  return isAuthenticated() ? (
    <div className="navbar">
      {/* Left Logo (without hover effect) */}
      <LogoButton text={"WingIt"}></LogoButton>

      {/* Navigation Buttons with hover effect */}
      <NavButton text="Book Flights" handleClick={handleFlights} />
      <NavButton text="My Bookings" handleClick={handleBookings}/>
      {/* Profile Icon */}
      <ProfileIcon />
    </div>
  ) : (
    <div className="navbar">
      <LogoButton text={"WingIt"}></LogoButton>
      <NavButton text="Sign in" handleClick={handleSignIn} />
    </div>
  );
};

export default NavigationBar;
