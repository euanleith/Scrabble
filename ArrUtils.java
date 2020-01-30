package sample;

import javafx.util.Pair;

import java.util.ArrayList;

public class ArrUtils {

    public static <T>int indexOf(T[] arr, T s) {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == s) return i;
        }
        return -1;
    }

    public static <T>int[] indexOf(T[][] arr, T s) {
        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[i].length; j++) {
                if (arr[i][j] == s) return new int[]{i,j};
            }
        }
        return null;
    }

    //todo could probably generalise to any dimensions
    /**
     * Returns the list of elements surrounding a given obj todo
     * @param x x index of the object
     * @param y y index of the object
     * @param board
     * @param <T>
     * @return
     */
    public static <T> ArrayList<T> getSurrounding(int x, int y, T[][] board) {
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

    public static <T> ArrayList<T> getSurrounding(T obj, T[][] board) {
//        int x = indexOf(board, obj);
//        if (x == -1) return null;
//        int y = indexOf(board[x], obj);
//        if (y == -1) return null;
//        return getSurrounding(y, x, board);

        int[] a = indexOf(board, obj);
        if (a == null) return null;
        return getSurrounding(a[0], a[1], board);
    }
}
