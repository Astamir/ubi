package com.ubi.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@RequiredArgsConstructor
@EqualsAndHashCode(of = "index")
@Getter
@Setter
@Accessors(chain = true)
@AllArgsConstructor
public class ChargingPoint {
  private final int index;
  private int current;
  private ChargingPointStatus status;

  public ChargingPoint adjustCurrent(int current) {
    if (status == ChargingPointStatus.AVAILABLE) {
      status = ChargingPointStatus.OCCUPIED;
    }

    this.current = current;

    return this;
  }

  public ChargingPoint unplug() {
    status = ChargingPointStatus.AVAILABLE;
    current = 0;

    return this;
  }
}
