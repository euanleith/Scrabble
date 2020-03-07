package sample;

import javafx.geometry.Point2D;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

//todo replace Point2D with custom Point?
public abstract class MathsUtils {

    /**
     * Returns the factorial of a positive integer
     * @param n positive integer
     * @return the factorial of n if it is a positive integer, otherwise -1
     */
    static int factorial(int n) {
        if (n < 0) return -1;
        int fact = 1;
        for (int i = 2; i <= n; i++) {
            fact *= i;
        }
        return fact;
    }

    //--------Graph Theory-----------

    /**
     * Returns the angle between two points
     * @param x1 x co-ordinate of point 1
     * @param y1 y co-ordinate of point 1
     * @param x2 x co-ordinate of point 2
     * @param y2 y co-ordinate of point 2
     * @return the angle between two points
     */
    static double getAngle(double x1, double y1, double x2, double y2) {
        double angle = Math.toDegrees(Math.atan2(y2 - y1, x2 - x1));
        if (angle < 0) angle += 360;
        return angle;
    }

    /**
     * Returns the angle between two points
     * @param p1 point 1
     * @param p2 point 2
     * @return the angle between two points
     */
    static double getAngle(Point2D p1, Point2D p2) {
        if (p1 == null || p2 == null) return -1;
        return getAngle(p1.getX(), p1.getY(), p2.getX(), p2.getY());
    }

    /**
     * Returns the angle of a set of points if it is a line
     * @param points set of points
     * @return the angle of a set of points if it is a line, otherwise -1
     * returns 0 if the line is empty or a single point
     */
    static double getAngle(List<Point2D> points) {
        if (points.size() < 2) return 0;
        double prevAngle = -1;
        for (int i = 0; i < points.size()-1; i++) {
            Point2D p1 = points.get(i);
            Point2D p2 = points.get(i+1);
            double angle = getAngle(p1.getX(), p1.getY(), p2.getX(), p2.getY());
            if (angle % 90 != 0 || (i != 0 && angle != prevAngle)) return -1;
            prevAngle = angle;
        }
        return prevAngle;
    }

    //todo move to ArrUtils?
    //todo could generalise more?
    /**
     * Sorts the given list in a line
     * from top to bottom or left to right
     * depending on the angle
     * @param list list of tiles to be sorted
     * @param angle angle of the tiles
     * @return sorted list
     */
    public static <T,U extends Comparable<? super U>> ArrayList<T> sortFromAngle(ArrayList<T> list, double angle, Function<T, U> getX, Function<T, U> getY) {
        if ((angle >= 0 && angle <= 45) ||
                (angle >= 135 && angle <= 225) ||
                (angle >= 315)) {
            list.sort(Comparator.comparing(getX));
        }
        else {
            list.sort(Comparator.comparing(getY));
        }
        return list;
    }

    public static boolean areUnitDst(ArrayList<Point2D> points) {
        if (points == null) return false;
        return !forIfTwo(points, (Point2D p1, Point2D p2) -> !isUnitDst(p1, p2));
    }

    /**
     * Returns true if all points in an ordered list are one unit apart horizontally
     * @param points ordered list of points
     * @return true if all points in an ordered list are one unit apart horizontally, false otherwise
     * returns true if points.isEmpty() || points.size() == 1,
     * returns false if point == null
     */
    public static boolean areUnitDstX(ArrayList<Point2D> points) {
        if (points == null) return false;
        return !forIfTwo(points, (Point2D p1, Point2D p2) -> !isUnitDstX(p1, p2));
    }

    /**
     * Returns true if all points in an ordered list are one unit apart vertically
     * @param points ordered list of points
     * @return true if all points in an ordered list are one unit apart vertically, false otherwise
     * returns true if points.isEmpty() || points.size() == 1,
     * returns false if point == null
     */
    public static boolean areUnitDstY(ArrayList<Point2D> points) {
        if (points == null) return false;
        return !forIfTwo(points, (Point2D p1, Point2D p2) -> !isUnitDstY(p1, p2));
    }

    //todo add isUnitDst(Point2D p1, Point2D p2, double/float unit)
    /**
     * Returns true if two points are one unit apart
     * @param p1 point 1
     * @param p2 point 2
     * @return true if two points are one unit apart, false otherwise
     * returns false if either point is null
     */
    public static boolean isUnitDst(Point2D p1, Point2D p2) {
        if (p1 == null || p2 == null) return false;
        return isUnitDstX(p1, p2) && isUnitDstY(p1, p2);
    }

    /**
     * Returns true if two points are one unit apart horizontally
     * @param p1 point 1
     * @param p2 point 2
     * @return true if two points are one unit apart horizontally, false otherwise
     * returns false if either point is null
     */
    public static boolean isUnitDstX(Point2D p1, Point2D p2) {
        if (p1 == null || p2 == null) return false;
        return Math.abs(p1.getX() - p2.getX()) == 1;
    }

    /**
     * Returns true if two points are one unit apart vertically
     * @param p1 point 1
     * @param p2 point 2
     * @return true if two points are one unit apart vertically, false otherwise
     * returns false if either point is null
     */
    public static boolean isUnitDstY(Point2D p1, Point2D p2) {
        if (p1 == null || p2 == null) return false;
        return Math.abs(p1.getY() - p2.getY()) == 1;
    }

    //----------Logic----------

    //todo move
    public static <T> boolean forIfTwo(ArrayList<T> list, BiPredicate<T, T> pred) {//forIfMul?
        for (int i = 0; i < list.size()-1; i++) {
            if (list.get(i+1) == null) return false;
            if (pred.test(list.get(i), list.get(i+1))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the first element of a list for which the predicate is true
     * @param list ArrayList
     * @param pred Predicate
     * @param <T> Type of list and pred
     * @return the first element of a list for which the predicate is true, null if this doesn't exist
     */
    static <T>T get(List<T> list, Predicate<T> pred) {
        for (T obj : list) {
            if (pred.test(obj)) {
                return obj;
            }
        }
        return null;
    }

    /**
     * Returns true if there is an element in the list for which the predicate is true
     * @param list List
     * @param pred Predicate to be tested for each element of the list
     * @param <T> Type of list and pred
     * @return true if there is an element in the list for which the predicate is true, false otherwise
     */
    static <T> boolean thereExists(List<T> list, Predicate<T> pred) {
        for (T obj : list) {
            if (pred.test(obj)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Applies the given void function to each element in the list
     * @param list List
     * @param func Function to be applied to each element of the list
     * @param <T> Type of list and function parameter
     */
    static <T> void forEach(List<T> list, Function<T, Void> func) {
        for (T obj : list) {
            func.apply(obj);
        }
    }

    //todo name; forEachThat, forEachIf, forEachWhich, forWhich
    /**
     * Applies the given void function to each element in the list
     * if that element satisfies the given predicate
     * @param list List
     * @param pred Predicate to be tested for each element of the list
     * @param func Function to be applied to each element of the list
     * @param <T> Type of list and function parameter
     * @return true if there is an element for which the predicate is true, false otherwise
     */
    static <T> boolean forWhich(List<T> list, Predicate<T> pred, Function<T, Void> func) {
        boolean out = false;
        for (T obj : list) {
            if (pred.test(obj)) {
                func.apply(obj);
                out = true;
            }
        }
        return out;
    }
}
