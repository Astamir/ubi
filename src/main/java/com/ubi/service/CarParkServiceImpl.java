package com.ubi.service;

import com.google.common.collect.ImmutableList;
import com.ubi.errors.ChargingPointAlreadyAvailable;
import com.ubi.errors.ChargingPointAlreadyOccupied;
import com.ubi.model.ChargingPoint;
import com.ubi.model.ChargingPointStatus;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CarParkServiceImpl implements CarParkService {
  @Value("${carpark.charging-points.total:10}")
  private int totalPoints;

  @Value("${carpark.charging-points.current.min:10}")
  private int minCurrent;

  @Value("${carpark.charging-points.current.max:20}")
  private int maxCurrent;

  @Value("${carpark.charging-points.current.total:100}")
  private int totalCurrent;

  private ChargingPoint[] points;
  LinkedList<Integer> order = new LinkedList<>();

  private int remainingCurrent;

  private final ReadWriteLock lock = new ReentrantReadWriteLock();

  @PostConstruct
  private void init() {
    if (maxCurrent <= minCurrent) {
      throw new IllegalStateException(
          String.format("Invalid max and min current values %s/%s.", maxCurrent, minCurrent));
    }
    if (totalPoints > totalCurrent / minCurrent) {
      throw new IllegalStateException(
          String.format(
              "Invalid number of charging points %s. Total current %s. Max available %s",
              totalPoints, totalCurrent, totalCurrent / minCurrent));
    }
    remainingCurrent = totalCurrent;

    points =
        IntStream.range(0, totalPoints)
            .mapToObj(index -> new ChargingPoint(index, 0, ChargingPointStatus.AVAILABLE))
            .toArray(ChargingPoint[]::new);
  }

  @Nullable
  @Override
  public ChargingPoint plugIn(final int index) {
    if (index < 0 || index > totalPoints - 1) {
      return null;
    }

    lock.writeLock().lock();
    try {
      final ChargingPoint chargingPoint = points[index];
      if (chargingPoint.getStatus() == ChargingPointStatus.OCCUPIED) {
        throw new ChargingPointAlreadyOccupied(index);
      }

      int current = maxCurrent;
      if (remainingCurrent < maxCurrent) {
        current = adjustCurrent();
      }
      remainingCurrent = Math.max(0, remainingCurrent - current);

      order.push(index);

      return chargingPoint.adjustCurrent(current);
    } finally {
      lock.writeLock().unlock();
    }
  }

  private int adjustCurrent() {
    int freedCurrent = 0;
    for (Iterator<Integer> it = order.descendingIterator(); it.hasNext(); ) {
      final Integer index = it.next();
      final ChargingPoint point = points[index];
      final int current = point.getCurrent();
      if (current > minCurrent) {
        point.adjustCurrent(minCurrent);
        freedCurrent += current - minCurrent;
      }
      if (freedCurrent >= maxCurrent) {
        break;
      }
    }

    return freedCurrent;
  }

  private int distributeCurrent(int availableCurrent) {
    int usedCurrent = 0;
    for (final Integer index : order) {
      final ChargingPoint point = points[index];
      final int current = point.getCurrent();
      if (current < maxCurrent) {
        point.adjustCurrent(Math.min(current + availableCurrent, maxCurrent));
        usedCurrent += point.getCurrent() - current;
      }
      if (availableCurrent == usedCurrent) {
        break;
      }
    }

    return availableCurrent - usedCurrent;
  }

  @Nullable
  @Override
  public ChargingPoint unplug(final int index) {
    if (index < 0 || index > totalPoints - 1) {
      return null;
    }
    lock.writeLock().lock();
    try {
      final ChargingPoint chargingPoint = points[index];
      if (chargingPoint.getStatus() == ChargingPointStatus.AVAILABLE) {
        throw new ChargingPointAlreadyAvailable(index);
      }

      order.removeFirstOccurrence(index);
      remainingCurrent += distributeCurrent(chargingPoint.getCurrent());

      return chargingPoint.unplug();
    } finally {
      lock.writeLock().unlock();
    }
  }

  @Nullable
  @Override
  public ChargingPoint getChargingPoint(final int index) {
    if (index < 0 || index > totalPoints - 1) {
      return null;
    }
    lock.readLock().lock();
    try {
      return points[index];
    } finally {
      lock.readLock().unlock();
    }
  }

  @Override
  public List<ChargingPoint> listChargingPoints() {
    lock.readLock().lock();
    try {
      return ImmutableList.copyOf(points);
    } finally {
      lock.readLock().unlock();
    }
  }
}
