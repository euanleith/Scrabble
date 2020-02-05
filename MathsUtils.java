package sample;

import javafx.geometry.Point2D;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

public abstract class MathsUtils {

    //--------Graph Theory-----------

    public static double getAngle(double x1, double y1, double x2, double y2) {
        double angle = Math.toDegrees(Math.atan2(y2 - y1, x2 - x1));
        if(angle < 0) angle += 360;
        return angle;
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

    /**
     * Checks if the list of points are in a horizontal or vertical line todo connected
     * @param points list
     * @return true if the list is in a horizontal or vertical line, false otherwise
     */
    public static boolean isHVLine(ArrayList<Point2D> points) {
        if (points.size() < 2) return true;
        double prevAngle = -1;
        for (int i = 0; i < points.size()-1; i++) {
            Point2D p1 = points.get(i);
            Point2D p2 = points.get(i+1);
            double angle = getAngle(p1.getX(), p1.getY(), p2.getX(), p2.getY());
            if (angle % 90 != 0 || (i != 0 && angle != prevAngle)) return false;
            prevAngle = angle;
        }
        return true;
    }

    public static boolean areHVConnected(ArrayList<Point2D> points) {
        return !forIfTwo(points, (Point2D p1, Point2D p2) -> !isHVConnected(p1, p2));
    }

    public static boolean isHVConnected(Point2D p1, Point2D p2) {
        return Math.max(p1.getX(), p2.getX()) - Math.min(p1.getX(), p2.getX()) == 1 ||
                Math.max(p1.getY(), p2.getY()) - Math.min(p1.getY(), p2.getY()) == 1;
    }

    //----------Logic----------

    //todo move
    public static <T> boolean forIfTwo(ArrayList<T> list, BiPredicate<T, T> pred) {//forIfMul?
        for (int i = 0; i < list.size()-1; i++) {
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
     * @param list ArrayList
     * @param pred Predicate
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
}
