package sample;

import javafx.scene.layout.GridPane;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import static sample.ArrUtils.indexOf;
import static sample.FileUtils.readToMap;
import static sample.FileUtils.readToTrie;
import static sample.Main.boardTiles;

//todo note that this isn't remotely general
abstract class Dictionary {
    private static final Trie dictionary = readToTrie("src/sample/dict.txt");
    //todo split letters into letter : numOfInstances, and letter : value
    private static HashMap<String, Integer> letters = readToMap("src/sample/tiles.txt", " ");

    //------todo move&/clean/redo-------

    /**
     * Checks if the list of words made from tiles are all valid
     *
     * @param in list of tiles
     * @return if all words are valid, the score earned from these words, -1 otherwise
     */
    public static int getScores(ArrayList<ArrayList<UserTile>> in, String[][] board) {
        int score = 0;
        for (ArrayList<UserTile> tiles : in) {
            if (!isValidWord(ButtonUtils.toString(tiles))) return -1;
            score += getScore(tiles, board);
        }
        return score;
    }

    /**
     * Returns true if a valid word can be formed
     * from the given string.
     * if it contains blank character(s),
     * by replacing each blank with each letter
     *
     * @param str string
     * @return true if a valid word can be formed from the given string, false otherwise
     */
    public static boolean isValidWord(String str) {
        String[] arr = str.split("((?<="+Main.BLANK+")|(?="+Main.BLANK+"))");
        System.out.println("this is the word: " + Arrays.toString(arr));

        return isValidWord(arr);
    }

    public static boolean isValidWord(String[] arr) {
        int i = indexOf(arr, Main.BLANK);

        if (i == -1) return dictionary.contains(ArrUtils.toString(arr));

        for (String letter : letters.keySet()) {
            String[] newArr = Arrays.copyOf(arr, arr.length);
            newArr[i] = letter;
            if (isValidWord(newArr)) return true;
        }
        return false;
    }

    static int getScore(ArrayList<UserTile> tiles, String[][] board) {
        int score = 0;
        int mul = 1; // multiplier
        for (UserTile tile : tiles) {
            score += getScore(tile, board);
            mul *= getMul(tile);
        }
        return score * mul;
    }

    static int getScore(UserTile tile, String[][] board) {
        int row = GridPane.getRowIndex(tile.getImg());
        int col = GridPane.getColumnIndex(tile.getImg());
        String type = board[row][col];
        int score = getScore(tile.getText());
        switch (type) {
            case "double_letter":
                return score * 2;
            case "triple_letter":
                return score * 3;
            default:
                return score;
        }
    }

    static int getMul(UserTile tile) {
        int row = GridPane.getRowIndex(tile.getImg());
        int col = GridPane.getColumnIndex(tile.getImg());
        String type = boardTiles[row][col];
        switch (type) {
            case "double_word":
                return 2;
            case "triple_word":
                return 3;
            default: return 1;
        }
    }

    //------------

    public static int getScore(String str) {
        int score = 0;
        String[] arr = str.split("((?<="+Main.BLANK+")|(?="+Main.BLANK+"))");
        for (String letter : arr) {
            if (letter.equals(Main.BLANK)) score += letters.get(letter);
            else {
                for (int i = 0; i < letter.length(); i++) {
                    score += letters.get(letter.substring(i, i+1));
                }
            }
        }
        return score;
    }
}
