package com.ubi.errors;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class ChargingPointNotFound extends ResponseStatusException {
  public ChargingPointNotFound(final int index) {
    super(HttpStatus.NOT_FOUND, String.format("Charging point with index %s is not found!", index));
  }
}
