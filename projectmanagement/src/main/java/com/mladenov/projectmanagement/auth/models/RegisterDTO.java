package com.mladenov.projectmanagement.auth.models;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.mladenov.projectmanagement.util.validation.UniqueUserEmail;
import com.mladenov.projectmanagement.util.validation.UniqueUsername;
import com.mladenov.projectmanagement.util.validation.ValidateUserRole;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class RegisterDTO {
    @NotEmpty(message = "First Name is required.")
    @Size(min = 3, max = 20, message = "First Name must be between 2 nad 20 symbols.")
    private String firstName;
    @NotEmpty(message = "Last Name is required.")
    @Size(min = 3, max = 20, message = "Last Name must be between 2 nad 20 symbols.")
    private String lastName;
    @NotEmpty(message = "User email should be provided.")
    @Email(message = "User email should be valid.")
    @UniqueUserEmail(message = "User email is already taken.")
    private String email;
    @NotEmpty(message = "Password is required.")
    @Size(min = 5, message = "Password must be at least 5 symbols.")
    private String password;
    @NotEmpty(message = "Username can't be empty.")
    @Size(min = 2, max = 20, message = "Must be between 2 and 20 symbols.")
    @UniqueUsername(message = "Username is already taken.")
    private String username;
    @NotEmpty(message = "Role is required.")
    @ValidateUserRole(message = "User role must be 'User' or 'Admin'.")
    private String role;

    @JsonSetter("role")
    public void setRole(String role) {
        this.role = role != null ? role.toUpperCase() : null;
    }

    public String getFirstName() {
        return firstName;
    }

    public RegisterDTO setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public String getLastName() {
        return lastName;
    }

    public RegisterDTO setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public RegisterDTO setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public RegisterDTO setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public RegisterDTO setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getRole() {
        return role;
    }
}