package com.Ali.Store.App.dto.user.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.time.LocalDate;

@NoArgsConstructor
@Setter
@Getter
@EqualsAndHashCode
@AllArgsConstructor
public class ProfileRequest {

    private String name;
    @Pattern(regexp = "^09\\d{9}$" , message = "Phone Number must be Start With (09) and be 11 Numbers !")
    private String phoneNumber;

    @Email(message = "Email Is Invalid !")
    private String email;

    @Past(message = "Birth Time Must Be In The Past !")
    private LocalDate birthData;
}
