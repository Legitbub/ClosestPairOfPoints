/**
 * Justin Dam, (Revised) July 14, 2023
 *
 * This program generates a list of 100 random points and sorts them by their
 * x and y coordinates. It then uses a divide and conquer recursive algorithm
 * to find the closest pair of points in the list.
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

public class ClosestPairOfPoints {
    public static void main(String[] args) {
        // Generate 100 random points and store them in an array
        Random r = new Random();
        Point[] pointsByX = new Point[100];
        Point[] pointsByY = new Point[100];
        for (int i = 0; i < 100; i++) {
            Point p = new Point(100 * r.nextDouble(), 100 * r.nextDouble());
            pointsByX[i] = p;
            pointsByY[i] = p;
        }
        // Sort the arrays by x coordinate and y coordinate
        Arrays.sort(pointsByX);
        Arrays.sort(pointsByY, new CompareY());

        // Display points
        for (Point p : pointsByX) {
            System.out.println(p);
        }

        // Find and display closest pair
        Pair closePair = distance(pointsByX, 0, pointsByX.length - 1, pointsByY);
        System.out.println("\nThe closest pair of points is: (" +
                closePair.getP1() + "), (" + closePair.getP2() + ")");
    }

    // Finds the distance between two given points using distance formula
    public static double distance(Point p1, Point p2) {
        return distance(p1.getX(), p1.getY(), p2.getX(), p2.getY());
    }

    public static double distance(double x1, double y1, double x2,
                                  double y2) {
        double squareX = (x1 - x2) * (x1 - x2);
        double squareY = (y1 - y2) * (y1 - y2);
        return Math.sqrt(squareX + squareY);
    }

    // Unused
    public static Pair getClosestPair(double[][] points) {
        return null;
    }

    // Given only 2 or 3 points, find the closest points in the set
    public static Pair getClosestPair(Point[] points) {
        double minDist = Double.MAX_VALUE;
        Pair closestP = new Pair(points[0], points[1]);
        for (int i = 0; i < points.length - 1; i++) {
            for (int a = i + 1; a < points.length; a++) {
                double compare = distance(points[i], points[a]);
                if (compare < minDist) {
                    minDist = compare;
                    closestP = new Pair(points[i], points[a]);
                }
            }
        }
        return closestP;
    }

    // Recursively find the closest points in the list; use low and high
    // variables to keep track of sublist positions
    public static Pair distance(Point[] pointsOrderedOnX, int low, int high,
                                Point[] pointsOrderedOnY) {
        if (high - low <= 3) {
            return getClosestPair(Arrays.copyOfRange(pointsOrderedOnX, 0, high - low));
        } else {
            // Find midpoint position
            int middle = low + (high - low) / 2;
            Point midpoint = pointsOrderedOnX[middle - low];

            // Subarrays to recursively compare smaller sections
            Point[] pointsYLeft = Arrays.copyOfRange(pointsOrderedOnY, 0,
                    middle - low);
            Point[] pointsYRight = Arrays.copyOfRange(pointsOrderedOnY,
                    middle - low, high - low);
            Point[] pointsXRight = Arrays.copyOfRange(pointsOrderedOnX,
                    middle - low, high - low);

            // Find and compare minimum distances between left and right
            // subarrays; set the one with the smaller distance as the
            // current closest pair
            Pair left = distance(pointsOrderedOnX, low, middle,
                    pointsYLeft);
            Pair right = distance(pointsXRight, middle, high,
                    pointsYRight);

            double minDistance;
            Pair closest;
            if (left.getDistance() < right.getDistance()) {
                minDistance = left.getDistance();
                closest = left;
            } else {
                minDistance = right.getDistance();
                closest = right;
            }

            // Create a strip containing points near the middle that are
            // within the minimum distance from each other
            ArrayList<Point> stripL = new ArrayList<>();
            ArrayList<Point> stripR = new ArrayList<>();

            for (Point p : pointsOrderedOnY) {
                if (p.getX() < midpoint.getX() &&
                        midpoint.getX() - p.getX() <= minDistance) {
                    stripL.add(p);
                } else if (p.getX() > midpoint.getX() &&
                        p.getX() - midpoint.getX() <= minDistance) {
                    stripR.add(p);
                }
            }

            // Skip points outside of the minimum distance (y-direction)
            int stripRIndex = 0;
            for (Point p1 : stripL) {
                while (stripRIndex < stripR.size() && stripR.get(stripRIndex).getY() < p1.getY() - minDistance) {
                    stripRIndex++;
                }

                // Compare distances within to find closest pair
                int tempIndex = stripRIndex;
                while (tempIndex < stripR.size() && stripR.get(tempIndex).getY() <= p1.getY() + minDistance) {
                    double curDist = distance(p1, stripR.get(tempIndex));
                    if (curDist < minDistance) {
                        minDistance = curDist;
                        closest = new Pair(p1, stripR.get(tempIndex));
                    }
                    tempIndex++;
                }
            }

            return closest;
        }
    }
}

// Point class with attributes for x and y positions
class Point implements Comparable<Point> {
    private double x;
    private double y;
    private static CompareY yComparer;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    // Sorted by x; sort by y if x values are the same
    @Override
    public int compareTo(Point o) {
        if (x > o.x) {
            return 1;
        } else if (x < o.x) {
            return -1;
        } else if (y != o.y){
            return yComparer.compare(this, o);
        } else {
            return 0;
        }
    }

    @Override
    public String toString() {
        return x + ", " + y;
    }
}

// Sorting by y; sort by x if y values are the same
class CompareY implements Comparator<Point> {

    @Override
    public int compare(Point o1, Point o2) {
        if (o1.getY() > o2.getY()) {
            return 1;
        } else if (o1.getY() < o2.getY()) {
            return -1;
        } else {
            return o1.compareTo(o2);
        }
    }
}

// Pair class that holds 2 points at a time
class Pair {
    private Point p1;
    private Point p2;

    public Pair(Point p1, Point p2) {
        this.p1 = p1;
        this.p2 = p2;
    }

    public Point getP1() {
        return p1;
    }

    public Point getP2() {
        return p2;
    }
    public double getDistance() {
        return ClosestPairOfPoints.distance(p1, p2);
    }
}