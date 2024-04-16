// Banner.js
import React from 'react';

const Banner = () => {
  return (
    <div
      style={{
        height: '50vh', // 1/4th of the viewport height
        width: '100vw',// 100% of the viewport width
        background: `url('https://media.npr.org/assets/img/2021/10/06/gettyimages-1302813215_wide-6c48e5a6aff547d2703693450c4805978de47435.jpg')`, // Replace with your image URL
        backgroundSize: 'cover', // Scale the image to cover the entire container
        backgroundPosition: 'center' // Center the image
      }}
    >
      {/* You can add content or additional components inside this div if needed */}
    </div>
  );
};

export default Banner;
