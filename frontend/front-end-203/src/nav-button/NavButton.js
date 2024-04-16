// My Flights and My Bookings buttons on the navigation bar

import React, { useState } from 'react';
import './NavButton.css'; // Import your CSS file for styling

const NavButton = ({ text, handleClick }) => {
  const [isHovered, setIsHovered] = useState(false);



  return (
    <button
      className={`nav-button ${isHovered ? 'hovered' : ''}`}
      onMouseEnter={() => setIsHovered(true)}
      onMouseLeave={() => setIsHovered(false)}
      onClick={handleClick}
    >
      {text}
    </button>
  );
};

export default NavButton;

