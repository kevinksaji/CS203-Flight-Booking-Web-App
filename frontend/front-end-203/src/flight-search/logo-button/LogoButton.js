// My Flights and My Bookings buttons on the navigation bar

import React, { useState } from 'react';
import './LogoButton.css'; // Import your CSS file for styling

const LogoButton = ({ text, onClick }) => {
  const [isHovered, setIsHovered] = useState(false);

  const handleClick = () => {
    console.log("Clicked logo!")
  };

  return (
    <button
      className={`logo-button ${isHovered ? 'hovered' : ''}`}
      onClick={handleClick}
      onMouseEnter={() => setIsHovered(true)}
      onMouseLeave={() => setIsHovered(false)}
    >
      {text}
    </button>
  );
};

export default LogoButton;

