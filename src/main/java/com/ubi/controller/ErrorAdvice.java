package com.ubi.controller;

import com.ubi.errors.ChargingPointAlreadyAvailable;
import com.ubi.errors.ChargingPointAlreadyOccupied;
import com.ubi.errors.ChargingPointNotFound;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class ErrorAdvice {

  @ExceptionHandler(ResponseStatusException.class)
  @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
  public ErrorMessage handleCustomException(ResponseStatusException ce) {
    return new ErrorMessage(ce.getStatus().value(), ce.getReason());
  }

  @ExceptionHandler(ChargingPointAlreadyOccupied.class)
  @ResponseStatus(value = HttpStatus.FORBIDDEN)
  public ErrorMessage handleCustomException(ChargingPointAlreadyOccupied ce) {
    return new ErrorMessage(ce.getStatus().value(), ce.getReason());
  }

  @ExceptionHandler(ChargingPointAlreadyAvailable.class)
  @ResponseStatus(value = HttpStatus.FORBIDDEN)
  public ErrorMessage handleCustomException(ChargingPointAlreadyAvailable ce) {
    return new ErrorMessage(ce.getStatus().value(), ce.getReason());
  }

  @ExceptionHandler(ChargingPointNotFound.class)
  @ResponseStatus(value = HttpStatus.NOT_FOUND)
  public ErrorMessage handleCustomException(ChargingPointNotFound ce) {
    return new ErrorMessage(ce.getStatus().value(), ce.getReason());
  }

  @Data
  private static class ErrorMessage {
    private final int status;
    private final String message;
  }
}
