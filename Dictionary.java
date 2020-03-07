package sample;

import javafx.scene.layout.GridPane;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import static sample.ArrUtils.indexOf;
import static sample.FileUtils.read;
import static sample.FileUtils.readToMap;
import static sample.FileUtils.readToTrie;

/**
 * Utility class for finding the values associated with tiles by their
 * string values and positions on a matrix containing various multipliers.
 * Only point of contact is function getScores, however other non-private functions could be used
 */
abstract class Dictionary {

    // trie representation of all words in the dictionary
    private static final Trie dictionary = readToTrie("src/sample/dicts/std_dict.txt");

    // maps an element in the alphabet to its associated value
    private static final HashMap<String, Integer> alphabet = readToMap("src/sample/alphabet_values.txt", " ");

    // 'blank' string
    private static final String BLANK = read("src/sample/alphabet_values.txt").get(alphabet.size()-1).split(" ")[0];

    /**
     * Checks if the list of words made from tiles are all valid
     *
     * @param in list of tiles
     * @return if all words are valid, the value of these words, -1 otherwise
     */
    static int getValues(ArrayList<ArrayList<UserTile>> in, String[][] board) {

        int value = 0;
        for (ArrayList<UserTile> tiles : in) {
            if (!isValidWord(TileUtils.toString(tiles))) return -1;
            value += getValue(tiles, board);
        }

        if (value == 0) System.out.println("No valid words found"); //todo move to board/main

        return value;
    }

    //todo make iterative
    /**
     * Returns true if a valid word can be formed from the given string.
     * If it contains blank string(s),
     * by replacing each blank with each letter in the alphabet
     * @param str string
     * @return true if a valid word can be formed from the given string, false otherwise
     */
    static boolean isValidWord(String str) {
        if (str == null) return false;

        String[] arr = str.split("((?<="+BLANK+")|(?="+BLANK+"))");
        System.out.println("found valid word: " + str);//todo move to board/main

        return isValidWord(arr);
    }

    /**
     * Recursive private element of above function
     * Returns true if a valid word can be formed from the given string array,
     * which contains an initial string split by the delimiter 'blank'
     * @param arr string array representation of initial string delimited by 'blank'
     * @return true if a valid word can be formed from the given string array, false otherwise
     */
    private static boolean isValidWord(String[] arr) {

        int i = indexOf(arr, BLANK);

        if (i == -1) return dictionary.contains(ArrUtils.toString(arr));

        for (String letter : alphabet.keySet()) {
            String[] newArr = Arrays.copyOf(arr, arr.length);
            newArr[i] = letter;
            if (isValidWord(newArr)) return true;
        }
        return false;
    }

    /**
     * Returns the value of an array of tiles which have indices which
     * correspond to positions on the matrix 'board', including
     * any multipliers those indices may have
     * @param tiles array of tiles
     * @param board matrix
     * @return the value of an array of tiles
     */
    static int getValue(ArrayList<UserTile> tiles, String[][] board) {
        int value = 0;
        int mul = 1; // multiplier
        for (UserTile tile : tiles) {
            value += getValue(tile, board);
            mul *= getMul(tile, board);
        }
        return value * mul;
    }

    /**
     * Returns the value of a tile which has an index which
     * correspond to a position on the matrix 'board', including
     * any multiplier that index may have
     * @param tile tile
     * @param board matrix
     * @return the value of a tile
     */
    static int getValue(UserTile tile, String[][] board) {
        int row = GridPane.getRowIndex(tile.getImg());
        int col = GridPane.getColumnIndex(tile.getImg());
        String type = board[row][col];
        int value = getValue(tile.getText());
        switch (type) {
            case "double_letter":
                return value * 2;
            case "triple_letter":
                return value * 3;
            default:
                return value;
        }
    }

    /**
     * Returns the value of a string
     * @param str string
     * @return the value of a string
     */
    static int getValue(String str) {
        if (str == null) return -1;

        int value = 0;
        String[] arr = str.split("((?<="+BLANK+")|(?="+BLANK+"))");
        for (String letter : arr) {
            if (letter.equals(BLANK)) value += alphabet.get(BLANK);
            else {
                for (int i = 0; i < letter.length(); i++) {
                    value += alphabet.get(letter.substring(i, i+1));
                }
            }
        }
        return value;
    }

    /**
     * Returns the multiplier for the position of the tile on the board
     * @param tile tile
     * @param board matrix
     * @return the multiplier for the position of the tile on the board
     */
    private static int getMul(UserTile tile, String[][] board) {
        int row = GridPane.getRowIndex(tile.getImg());
        int col = GridPane.getColumnIndex(tile.getImg());
        String type = board[row][col];
        switch (type) {
            case "double_word":
                return 2;
            case "triple_word":
                return 3;
            default: return 1;
        }
    }

    /**
     * Returns the 'blank' string
     * @return the 'blank' string
     */
    static String getBlank() {
        return BLANK;
    }
}
