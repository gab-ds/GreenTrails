package it.greentrails.backend.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.data.geo.Point;

class DistanceCalculatorTest {

  @Test
  void zeroDistance() {
    Point p = new Point(45.0, 9.0);
    double d = DistanceCalculator.distance(p, p);
    assertEquals(0.0, d, 1e-9);
  }

  @Test
  void symmetry() {
    Point a = new Point(10.0, 20.0);
    Point b = new Point(-5.0, 100.0);
    double d1 = DistanceCalculator.distance(a, b);
    double d2 = DistanceCalculator.distance(b, a);
    assertEquals(d1, d2, 1e-9);
    assertTrue(d1 >= 0.0);
  }

  @Test
  void knownDistanceOneDegreeLongitudeAtEquator() {
    Point p1 = new Point(0.0, 0.0);
    Point p2 = new Point(0.0, 1.0);
    // Expected distance using R = 6371 km as in DistanceCalculator implementation
    double expected = 111194.92664455873;
    double actual = DistanceCalculator.distance(p1, p2);
    assertEquals(expected, actual, 0.5); // tolerance 0.5 meters
  }

  @Test
  void nullPointThrows() {
    Point p = new Point(0.0, 0.0);
    assertThrows(NullPointerException.class, () -> DistanceCalculator.distance(null, p));
    assertThrows(NullPointerException.class, () -> DistanceCalculator.distance(p, null));
  }

}