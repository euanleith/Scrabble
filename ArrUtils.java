package sample;

import javafx.util.Pair;

import java.util.*;
import java.util.function.Predicate;

abstract class ArrUtils {

    /**
     * Returns the index of a given object in a given array
     * @param arr array
     * @param obj object
     * @param <T> type of the object
     * @return the index of the object if it's in the array, otherwise -1
     */
    static <T> int indexOf(T[] arr, T obj) {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i].equals(obj)) return i;
        }
        return -1;
    }

    /**
     * Returns the index of a given object in a given 2d array
     * @param arr array
     * @param obj object
     * @param <T> type of the object
     * @return the index of the object as an int array
     * with two elements if it's in the array, otherwise -1
     */
    static <T>int[] indexOf(T[][] arr, T obj) {
        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[i].length; j++) {
                if (arr[i][j].equals(obj)) return new int[]{i,j};
            }
        }
        return null;
    }

    //todo could probably generalise to any dimensions
    //todo
    /**
     * Returns the list of elements surrounding a given obj
     * for which a given predicate is true
     * @param x x index of the object
     * @param y y index of the object
     * @param matrix matrix the object is an element of
     * @param pred predicate
     * @param <T> type of the object
     * @return the list of elements surrounding a given obj
     * for which a given predicate is true
     */
    static <T> ArrayList<T> getSurrounding(int x, int y, T[][] matrix, Predicate<T> pred) {
        ArrayList<T> list = new ArrayList<>(8);
        ArrayList<Pair<Integer, Integer>> positions = new ArrayList<>();
        positions.add(new Pair<>(0,-1));
        positions.add(new Pair<>(-1,0));
        positions.add(new Pair<>(1,0));
        positions.add(new Pair<>(0,1));
        for (Pair<Integer, Integer> pos : positions) {
            int x1 = x + pos.getValue();
            int y1 = y + pos.getKey();
            if (x1 >= 0 && x1 < matrix.length &&
                    y1 >= 0 && y1 < matrix[x1].length &&
                    matrix[x1][y1] != null &&
                    pred.test(matrix[x1][y1])) {
                list.add(matrix[x1][y1]);
            }
        }
        return list;
    }

    static <T> ArrayList<T> getSurrounding(int x, int y, T[][] matrix) {
        return getSurrounding(x, y, matrix, p -> true);
    }

    static <T> ArrayList<T> getSurrounding(T obj, T[][] board, Predicate<T> pred) {
        int[] a = indexOf(board, obj);
        if (a == null) return null;
        return getSurrounding(a[0], a[1], board, pred);
    }

    static <T> ArrayList<T> getSurrounding(T obj, T[][] board) {
        return getSurrounding(obj, board, p -> true);
    }

    /**
     * Returns true if the 2d list is empty
     * @param lists 2d list
     * @param <T> type of the list
     * @return true if the 2d list is empty, false otherwise
     */
    static <T> boolean isEmpty(List<List<T>> lists) {
        for (List<T> list : lists) {
            if (!list.isEmpty()) return false;
        }
        return true;
    }

    /**
     * Converts a given String list to a String array
     * note: can't do this for general types...
     * @param list String lsit
     * @return return a String array representation of the given String list
     */
    static String[] toArr(List<String> list) {
        if (list == null) return null;

        String[] arr = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            arr[i] = list.get(i);
        }
        return arr;
    }

    /**
     * Returns a string representation of an array with no delimiter between each element
     * @param arr array
     * @return a string representation of an array
     */
    static String toString(String[] arr) {
        if (arr == null) return null;

        String out = "";
        for (String str : arr) {
            out += str;
        }
        return out;
    }

    /**
     * Returns a string representation of an array with a given delimiter between each element
     * @param arr array
     * @param delimiter delimiter
     * @return a string representation of an array
     */
    static String toString(String[] arr, String delimiter) {
        if (arr == null) return null;

        String out = "";
        for (String str : arr) {
            out += str + delimiter;
        }
        out = out.substring(0, out.length()-delimiter.length());
        return out;
    }

    //todo note doesn't inc empty set...
    static <T> ArrayList<ArrayList<T>> powerSet(List<T> list) {
        int size = list.size();
        ArrayList<ArrayList<T>> out = new ArrayList<>((int) Math.pow(2, size));
        for (int i = 0; i < size; i++) {
            ArrayList<T> subset = new ArrayList<>(size-i);
            for (int j = i; j < size; j++) {
                subset.add(list.get(j));
                out.add(subset);
                subset = new ArrayList<>(subset);

            }
        }
        return out;
    }

    static <T> ArrayList<ArrayList<T>> combinations(ArrayList<T> list) {//todo make int[][]?
        int size = list.size();

        ArrayList<ArrayList<T>> out = new ArrayList<>((int) Math.pow(size ,2));

        for (int i = 0; i < (1 << size); i++) {

            ArrayList<T> subset = new ArrayList<>();
            for (int j = 0; j < size; j++) {
                if ((i & (1 << j)) > 0) {
                    subset.add(list.get(j));
                }
            }

            out.add(subset);
        }

        return out;
    }

    static ArrayList<ArrayList<Integer>> combinations(int n) {//todo make int[][]?
        ArrayList<ArrayList<Integer>> out = new ArrayList<>((int) Math.pow(n ,2));

        for (int i = 0; i < (1 << n); i++) {

            ArrayList<Integer> subset = new ArrayList<>();
            for (int j = 0; j < n; j++) {
                if ((i & (1 << j)) > 0) {
                    subset.add(j);
                }
            }

            out.add(subset);
        }

        return out;
    }

    static <T> ArrayList<T> reverse(ArrayList<T> list) {
        int size = list.size();
        ArrayList<T> newList = new ArrayList<>(size);
        for (int i = size-1; i >= 0; i--) {
            newList.add(list.get(i));
        }
        return newList;
    }
}
