package com.example.users.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserRequest {

    @NotNull(message = "firstName cannot be null")
    @Size(min = 2, message = "firstName must not be less than 2 characters")
    private String firstName;

    @NotNull(message = "lastName cannot be null")
    @Size(min = 2, message = "lastName must not be less than 2 characters")
    private String lastName;

    @NotNull(message = "password cannot be null")
    @Size(min = 8, max = 16, message = "password must be less than 16 and greater than 8 characters")
    private String password;

    @NotNull(message = "Email cannot be null")
    @Email
    private String email;

}
