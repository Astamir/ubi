package com.ubi.errors;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class ChargingPointAlreadyOccupied extends ResponseStatusException {
  public ChargingPointAlreadyOccupied(final int index) {
    super(
        HttpStatus.FORBIDDEN,
        String.format("Unable to plug in charging point %s. It is already OCCUPIED", index));
  }
}
