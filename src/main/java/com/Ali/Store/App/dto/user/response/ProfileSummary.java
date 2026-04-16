package com.Ali.Store.App.dto.user.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Builder;

import java.time.LocalDate;

@JsonTypeName("Profile user")
@Builder
public record ProfileSummary(Long id, String name , @JsonProperty("Phone number")String phoneNumber , String email,  LocalDate birthData){

}
