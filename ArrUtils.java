package sample;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;

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
     * @param x x index of the object
     * @param y y index of the object
     * @param board matrix the object is an element of
     * @param <T> type of the object
     * @return
     */
    static <T> ArrayList<T> getSurrounding(int x, int y, T[][] board) {
        ArrayList<T> list = new ArrayList<>(8);
        ArrayList<Pair<Integer, Integer>> positions = new ArrayList<>();
        positions.add(new Pair<>(0,-1));
        positions.add(new Pair<>(-1,0));
        positions.add(new Pair<>(1,0));
        positions.add(new Pair<>(0,1));
        for (Pair<Integer, Integer> pos : positions) {
            int x1 = x + pos.getValue();
            int y1 = y + pos.getKey();
            if (x1 >= 0 && x1 < board.length &&
                    y1 >= 0 && y1 < board[x1].length &&
                    board[x1][y1] != null) {
                list.add(board[x1][y1]);
            }
        }
        return list;
    }

    static <T> ArrayList<T> getSurrounding(T obj, T[][] board) {
//        int x = indexOf(board, obj);
//        if (x == -1) return null;
//        int y = indexOf(board[x], obj);
//        if (y == -1) return null;
//        return getSurrounding(y, x, board);

        int[] a = indexOf(board, obj);
        if (a == null) return null;
        return getSurrounding(a[0], a[1], board);
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
}
