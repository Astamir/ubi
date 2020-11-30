package com.ubi.controller;

import com.ubi.api.model.ChargingPointState;
import com.ubi.api.model.ChargingPointStateReport;
import com.ubi.errors.ChargingPointNotFound;
import com.ubi.model.ChargingPoint;
import com.ubi.service.CarParkService;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@CrossOrigin
public class UbiCarParkController implements com.ubi.api.ChargingPointsApi {
  private final CarParkService carParkService;

  @Override
  public ChargingPointState getChargingPointState(@NotNull final Integer index) {
    final ChargingPoint chargingPoint = carParkService.getChargingPoint(index);
    if (chargingPoint == null) {
      throw new ChargingPointNotFound(index);
    }

    return mapToApi(chargingPoint);
  }

  @Override
  public ChargingPointStateReport listChargingPointsState() {
    return mapToApi(carParkService.listChargingPoints());
  }

  @Override
  public ChargingPointState plugInChargingPoint(@NotNull final Integer index) {
    final ChargingPoint chargingPoint = carParkService.plugIn(index);
    if (chargingPoint == null) {
      throw new ChargingPointNotFound(index);
    }

    return mapToApi(chargingPoint);
  }

  @Override
  public ChargingPointState unplugFromChargingPoint(@NotNull final Integer index) {
    final ChargingPoint chargingPoint = carParkService.unplug(index);
    if (chargingPoint == null) {
      throw new ChargingPointNotFound(index);
    }

    return mapToApi(chargingPoint);
  }

  private static ChargingPointState mapToApi(ChargingPoint chargingPoint) {
    return new ChargingPointState()
        .index(chargingPoint.getIndex())
        .consumption(chargingPoint.getCurrent())
        .status(ChargingPointState.StatusEnum.fromValue(chargingPoint.getStatus().name()));
  }

  private static ChargingPointStateReport mapToApi(List<ChargingPoint> points) {
    final List<ChargingPointState> states =
        points.stream().map(UbiCarParkController::mapToApi).collect(Collectors.toList());

    return new ChargingPointStateReport().total(states.size()).chargingPoints(states);
  }
}
