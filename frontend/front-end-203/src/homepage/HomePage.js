import React from "react";
import TopArea from "./Top/TopArea";
import Album from "./MiddleAlbum/Album";
import Footer from "./Bottom/Footer";
import "./HomePage.css";
import NavigationBar from "../nav-bar/NavigationBar";

const HomePage = () => {
  return (
    <>
      <div className="nav">
        <NavigationBar />
      </div>
      <TopArea />
      <Album />
      <Footer />
    </>
  );
};

export default HomePage;
