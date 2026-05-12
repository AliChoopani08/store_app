package com.Ali.Store.App.dto.user.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public record DeviceSummary(@JsonProperty("device UUID")UUID deviceUUid, @JsonProperty("device info")String deviceInfo) {
}
