// profile-icon component
import React, { useState } from "react";
import "./ProfileIcon.css"; // Import your CSS file for styling
import { removeAuthToken } from "../auth";
import { useNavigate } from "react-router-dom";

import { Box, Typography } from "@mui/material"
import { getCurrentUser } from "../auth";

const ProfileIcon = () => {
  const currentUser = getCurrentUser();
  const navigate = useNavigate();
  // State to track the dropdown menu's open/closed state
  const [isDropdownOpen, setIsDropdownOpen] = useState(false);

  // Function to toggle the dropdown menu's visibility
  const toggleDropdown = () => {
    setIsDropdownOpen(!isDropdownOpen);
  };
  const handleClickSignOut = () => {
    console.log("Clicked sign out!");
    removeAuthToken();
    navigate("/signin");
  };
  const handleClickAccount = () => {
    console.log("Clicked my account!");
  };
  const handleProfileClick = () => {
    console.log("Clicked profile icon!");
  };

  return (
    <div className="profile-icon-container">
      <Box sx={{
        display: 'flex',
        flexDirection: 'row',
        alignItems: 'center',
      }}>
        <Typography variant="body2" sx={{
          p: 1,
          m: 1,
          color: "white",
        }}>Welcome<br/>{currentUser != null ? currentUser.username : ""}</Typography>
        <div
          className={`profile-icon ${isDropdownOpen ? "open" : ""}`}
          onClick={toggleDropdown}
        >
          {/* Your profile icon image or avatar */}
          <img
            src="https://www.shareicon.net/data/512x512/2016/05/24/770117_people_512x512.png"
            alt="Profile"
            onClick={handleProfileClick}
          />
        </div>
      </Box>


      {/* Render the dropdown menu when it's open */}
      {isDropdownOpen && (
        <div className="dropdown-menu">
          <button className="dropdown-button" onClick={handleClickAccount}>
            My Account
          </button>
          <button className="dropdown-button" onClick={handleClickSignOut}>
            Sign Out
          </button>
        </div>
      )}
    </div>
  );
};

export default ProfileIcon;
