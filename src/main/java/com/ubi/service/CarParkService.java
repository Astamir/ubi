package com.ubi.service;

import com.ubi.model.ChargingPoint;
import java.util.List;
import javax.annotation.Nullable;

public interface CarParkService {
  /**
   * Plugs in a car to particular charging point. Balances current if needed.
   *
   * @param index index of a charging point
   * @return state of the charging point after plugging or null if no such point with index is found
   */
  @Nullable
  ChargingPoint plugIn(int index);

  /**
   * Unplugs a car from particular charging point. Balances current if needed.
   *
   * @param index index of a charging point
   * @return state of the charging point after unplugging or null if no such point with index is
   *     found
   */
  @Nullable
  ChargingPoint unplug(int index);

  /**
   * @param index index of a charging point
   * @return state of the charging point or null if no such point with index is found
   */
  @Nullable
  ChargingPoint getChargingPoint(int index);

  /**
   * Lists all the charging points
   *
   * @return state of the all charging points
   */
  List<ChargingPoint> listChargingPoints();
}
