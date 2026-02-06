package it.greentrails.backend.utils;

import org.springframework.data.geo.Point;

public class DistanceCalculator {

  private DistanceCalculator() {}

  public static double distance(Point punto1, Point punto2) {

    double lat1 = punto1.getX();
    double lat2 = punto2.getX();

    double lon1 = punto1.getY();
    double lon2 = punto2.getY();

    final int R = 6371;

    double latDistance = Math.toRadians(lat2 - lat1);
    double lonDistance = Math.toRadians(lon2 - lon1);

    double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
        + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
        * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    double distance = R * c * 1000;

    return Math.sqrt(Math.pow(distance, 2));
  }

}
