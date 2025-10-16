package com.Ali.Store.App.dto.user.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UserResponse(Long id, String username, String role, @JsonProperty("Profile") ProfileResponse profileResponse){}
