package com.G2T5203.wingit.user;

import com.G2T5203.wingit.booking.Booking;
import com.G2T5203.wingit.user.CustomAuthorityDeserializer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Entity
public class WingitUser implements UserDetails {
    // TODO: Validation annotations to include messages.
    @Id
    private String username;
    @NotEmpty
    private String password;
    @NotEmpty @Pattern(regexp = "ROLE_USER|ROLE_ADMIN")
    private String authorityRole = "ROLE_USER"; // defaults to user.
    @NotEmpty
    private String firstName;
    @NotEmpty
    private String lastName;
    @NotNull @Past
    private LocalDate dob;
    @Column(unique = true) @Email @NotEmpty
    private String email;
    // TODO: Enforce some sort of pattern check for phone numbers.
    @NotEmpty
    private String phone;
    @NotEmpty @Pattern(regexp = "Mr|Mrs|Miss|Mdm|Master", message = "Salutation can only be Mr, Mrs, Miss, Mdm, or Master")
    private String salutation;
    @OneToMany(mappedBy = "wingitUser", cascade = CascadeType.ALL)
    //@JsonManagedReference
    @JsonIgnore
    private List<Booking> bookings;


    public WingitUser(String username, String password, String authorityRole, String firstName, String lastName, LocalDate dob, String email, String phone, String salutation) {
        this.username = username;
        this.password = password;
        this.authorityRole = authorityRole;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dob = dob;
        this.email = email;
        this.phone = phone;
        this.salutation = salutation;
    }
    public WingitUser() {}

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAuthorityRole() { return authorityRole; }

    public void setAuthorityRole(String authorityRole) { this.authorityRole = authorityRole; }

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

    @Override
    public String toString() {
        return "WingitUser{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", authorityRole='" + authorityRole + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", dob=" + dob +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", salutation='" + salutation + '\'' +
                '}';
    }


    // Functions below is to comply with implementation of UserDetails.

    @JsonDeserialize(using = CustomAuthorityDeserializer.class)
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Arrays.asList(new SimpleGrantedAuthority(authorityRole));
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }
}
