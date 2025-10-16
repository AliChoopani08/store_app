package com.Ali.Store.App.entities.userAndProfileUser;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import static jakarta.persistence.GenerationType.IDENTITY;

@NoArgsConstructor
@Getter
@Setter
@ToString(of = {"name", "phoneNumber", "email", "birthData"})
@EqualsAndHashCode(of = {"name", "phoneNumber", "email", "birthData"})
@Entity
public class ProfileUser {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @JsonIgnore
    private Long id;

    private String name;
    private String phoneNumber;
    private String email;
    private LocalDate birthData;

    @OneToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private Users user;

    public ProfileUser(String firstName,String phoneNumber, String email, LocalDate birthData) {
        this.name = firstName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.birthData = birthData;
    }

    public void setUsernameOnCorrectFields(Users user) {
        if (user.getUsername().startsWith("09")) {
            this.phoneNumber = user.getUsername();
        }
        else if (user.getUsername().contains("@")) {
            this.email = user.getUsername();
        }
    }
}
