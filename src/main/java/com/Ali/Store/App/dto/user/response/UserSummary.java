package com.Ali.Store.App.dto.user.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder(toBuilder = true)
public record UserSummary(Long id, String username, String role, @JsonProperty("Profile") ProfileSummary profileSummary){}
