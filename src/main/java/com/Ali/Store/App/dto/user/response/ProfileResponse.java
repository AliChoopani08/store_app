package com.Ali.Store.App.dto.user.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("Profile user")
public record ProfileResponse (Long id, String name , @JsonProperty("Phone number")String phoneNumber , String email){

}
