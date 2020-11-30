package com.ubi.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ubi.errors.ChargingPointAlreadyAvailable;
import com.ubi.errors.ChargingPointAlreadyOccupied;
import com.ubi.model.ChargingPoint;
import com.ubi.model.ChargingPointStatus;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class TestCarParkService {
  @Autowired private CarParkService carParkService;

  @Test
  void testPlug_normal() {
    ChargingPoint chargingPoint = carParkService.plugIn(0);
    verifyChargingPoint(chargingPoint, 0, 20, ChargingPointStatus.OCCUPIED);
    chargingPoint = carParkService.plugIn(1);
    verifyChargingPoint(chargingPoint, 1, 20, ChargingPointStatus.OCCUPIED);
    chargingPoint = carParkService.plugIn(2);
    verifyChargingPoint(chargingPoint, 2, 20, ChargingPointStatus.OCCUPIED);
    chargingPoint = carParkService.plugIn(3);
    verifyChargingPoint(chargingPoint, 3, 20, ChargingPointStatus.OCCUPIED);
    chargingPoint = carParkService.plugIn(4);
    verifyChargingPoint(chargingPoint, 4, 20, ChargingPointStatus.OCCUPIED);
    chargingPoint = carParkService.plugIn(5);
    verifyChargingPoint(chargingPoint, 5, 20, ChargingPointStatus.OCCUPIED);
  }

  @Test
  void testPlug_notFound() {
    assertThat(carParkService.plugIn(-1)).isNull();
    assertThat(carParkService.plugIn(10)).isNull();
  }

  @Test
  void testPlug_alreadyPlugged() {
    ChargingPoint chargingPoint = carParkService.plugIn(0);
    verifyChargingPoint(chargingPoint, 0, 20, ChargingPointStatus.OCCUPIED);

    assertThatThrownBy(() -> carParkService.plugIn(0))
        .isInstanceOf(ChargingPointAlreadyOccupied.class)
        .hasMessageContaining("Unable to plug in charging point 0. It is already OCCUPIED");
  }

  @Test
  void testUnplug_normal() {
    ChargingPoint chargingPoint = carParkService.plugIn(0);
    verifyChargingPoint(chargingPoint, 0, 20, ChargingPointStatus.OCCUPIED);

    chargingPoint = carParkService.unplug(0);
    verifyChargingPoint(chargingPoint, 0, 0, ChargingPointStatus.AVAILABLE);
  }

  @Test
  void testUnplug_notFound() {
    assertThat(carParkService.unplug(-1)).isNull();
    assertThat(carParkService.unplug(10)).isNull();
  }

  @Test
  void testUnplug_alreadyAvailable() {
    assertThatThrownBy(() -> carParkService.unplug(0))
        .isInstanceOf(ChargingPointAlreadyAvailable.class)
        .hasMessageContaining("Unable to unplug charging point 0. It is already AVAILABLE");
  }

  @Test
  void testListPoints() {
    List<ChargingPoint> chargingPoints = carParkService.listChargingPoints();
    assertThat(chargingPoints)
        .hasSize(10)
        .extracting(ChargingPoint::getCurrent)
        .allMatch(c -> c == 0);
    assertThat(chargingPoints)
        .hasSize(10)
        .extracting(ChargingPoint::getStatus)
        .allMatch(s -> s == ChargingPointStatus.AVAILABLE);

    ChargingPoint chargingPoint = carParkService.plugIn(0);
    verifyChargingPoint(chargingPoint, 0, 20, ChargingPointStatus.OCCUPIED);
    chargingPoint = carParkService.plugIn(1);
    verifyChargingPoint(chargingPoint, 1, 20, ChargingPointStatus.OCCUPIED);
    chargingPoint = carParkService.plugIn(2);
    verifyChargingPoint(chargingPoint, 2, 20, ChargingPointStatus.OCCUPIED);
    chargingPoint = carParkService.plugIn(3);
    verifyChargingPoint(chargingPoint, 3, 20, ChargingPointStatus.OCCUPIED);
    chargingPoint = carParkService.plugIn(4);
    verifyChargingPoint(chargingPoint, 4, 20, ChargingPointStatus.OCCUPIED);

    chargingPoints = carParkService.listChargingPoints();
    assertThat(chargingPoints)
        .hasSize(10)
        .extracting(ChargingPoint::getCurrent)
        .containsSequence(20, 20, 20, 20, 20, 0, 0, 0, 0, 0);
    assertThat(chargingPoints)
        .hasSize(10)
        .extracting(ChargingPoint::getStatus)
        .containsSequence(
            ChargingPointStatus.OCCUPIED,
            ChargingPointStatus.OCCUPIED,
            ChargingPointStatus.OCCUPIED,
            ChargingPointStatus.OCCUPIED,
            ChargingPointStatus.OCCUPIED,
            ChargingPointStatus.AVAILABLE,
            ChargingPointStatus.AVAILABLE,
            ChargingPointStatus.AVAILABLE,
            ChargingPointStatus.AVAILABLE,
            ChargingPointStatus.AVAILABLE);
  }

  @Test
  void testGetPoint_notFound() {
    assertThat(carParkService.getChargingPoint(-1)).isNull();
    assertThat(carParkService.getChargingPoint(10)).isNull();
  }

  @Test
  void testGetPoint_normal() {
    verifyChargingPoint(carParkService.getChargingPoint(1), 1, 0, ChargingPointStatus.AVAILABLE);
    verifyChargingPoint(carParkService.getChargingPoint(2), 2, 0, ChargingPointStatus.AVAILABLE);
    verifyChargingPoint(carParkService.getChargingPoint(3), 3, 0, ChargingPointStatus.AVAILABLE);
    verifyChargingPoint(carParkService.getChargingPoint(4), 4, 0, ChargingPointStatus.AVAILABLE);
    verifyChargingPoint(carParkService.getChargingPoint(5), 5, 0, ChargingPointStatus.AVAILABLE);
    verifyChargingPoint(carParkService.getChargingPoint(6), 6, 0, ChargingPointStatus.AVAILABLE);
    verifyChargingPoint(carParkService.getChargingPoint(7), 7, 0, ChargingPointStatus.AVAILABLE);
    verifyChargingPoint(carParkService.getChargingPoint(8), 8, 0, ChargingPointStatus.AVAILABLE);
    verifyChargingPoint(carParkService.getChargingPoint(9), 9, 0, ChargingPointStatus.AVAILABLE);

    carParkService.plugIn(0);
    verifyChargingPoint(carParkService.getChargingPoint(0), 0, 20, ChargingPointStatus.OCCUPIED);
    carParkService.plugIn(1);
    verifyChargingPoint(carParkService.getChargingPoint(1), 1, 20, ChargingPointStatus.OCCUPIED);
    carParkService.plugIn(2);
    verifyChargingPoint(carParkService.getChargingPoint(2), 2, 20, ChargingPointStatus.OCCUPIED);

    carParkService.unplug(2);
    verifyChargingPoint(carParkService.getChargingPoint(2), 2, 0, ChargingPointStatus.AVAILABLE);
  }

  private static void verifyChargingPoint(
      ChargingPoint point, int index, int current, ChargingPointStatus status) {
    assertThat(point).isNotNull();
    assertThat(point.getCurrent()).isEqualTo(current);
    assertThat(point.getStatus()).isEqualTo(status);
    assertThat(point.getIndex()).isEqualTo(index);
  }

  private void foo() {}

  @TestConfiguration
  @Import({CarParkServiceImpl.class})
  static class ContextConfiguration {}
}
