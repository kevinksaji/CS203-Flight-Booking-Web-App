import React from "react";
// import { useNavigate } from "react-router-dom";
import ProgressBar from "../progress-bar/ProgressBar";
import NavigationBar from "../nav-bar/NavigationBar";
import { loadStripe } from '@stripe/stripe-js';
import { Elements } from '@stripe/react-stripe-js';
import PaymentForm from "./PaymentForm";
import BookingSummary from "./BookingSummary";
import "./PaymentPage.css";

const PaymentPage = () => {
    // const navigate = useNavigate();
    // const handleClickReturn = (e) => {
    //     navigate("/");
    // }
    const stripePromise = loadStripe('YOUR_PUBLISHABLE_KEY_HERE');
    
    return (
        <>
            <NavigationBar />
            <ProgressBar currentStep={"Payment"} number={4} deadline={new Date(sessionStorage.getItem('endTime'))} />

            <div className="payment-content">
                <div className="payment-form">
                    <Elements stripe={stripePromise}>
                        <PaymentForm />
                    </Elements>
                </div>
                <div className="booking-summary">
                    <BookingSummary />
                </div>
            </div>
        </>
    );
};

export default PaymentPage;