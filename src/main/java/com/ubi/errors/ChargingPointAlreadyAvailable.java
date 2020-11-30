package com.ubi.errors;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class ChargingPointAlreadyAvailable extends ResponseStatusException {
  public ChargingPointAlreadyAvailable(final int index) {
    super(
        HttpStatus.FORBIDDEN,
        String.format("Unable to unplug charging point %s. It is already AVAILABLE", index));
  }
}
