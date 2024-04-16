package com.G2T5203.wingit.user;

import java.time.LocalDate;

public class WingitUserSimpleJson {
    private String username;
    private String firstName;
    private String lastName;
    private LocalDate dob;
    private String email;
    private String phone;
    private String salutation;

    public WingitUserSimpleJson(String username, String firstName, String lastName, LocalDate dob, String email, String phone, String salutation) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dob = dob;
        this.email = email;
        this.phone = phone;
        this.salutation = salutation;
    }

    public WingitUserSimpleJson(WingitUser user) {
        this(
                user.getUsername(),
                user.getFirstName(),
                user.getLastName(),
                user.getDob(),
                user.getEmail(),
                user.getPhone(),
                user.getSalutation()
        );
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public LocalDate getDob() {
        return dob;
    }

    public void setDob(LocalDate dob) {
        this.dob = dob;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSalutation() {
        return salutation;
    }

    public void setSalutation(String salutation) {
        this.salutation = salutation;
    }
}
