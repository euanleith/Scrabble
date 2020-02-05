package sample;

import java.util.ArrayList;
import java.util.HashMap;

//todo note that this isn't remotely general
public abstract class Dictionary {
    private static final ArrayList<String> dictionary = FileUtils.read("src/sample/dict.txt");
    private static HashMap<String, Integer> letters = readToMap("src/sample/tiles.txt");

    private static HashMap<String, Integer> readToMap(String fname) {
        ArrayList<String> file = FileUtils.read(fname);
        HashMap<String, Integer> map = new HashMap<>();
        for (String line : file) {
            String[] s = line.split(" "); // str num val
            String str = s[0];
            int val = Integer.parseInt(s[2]);

            map.put(str, val);
        }
        return map;
    }

    /**
     * Checks if the list of words made are all valid
     * @param strings list of strings
     * @return if all words are valid, the score earned from these words, -1 otherwise
     */
    public static int areValidWords(ArrayList<String> strings) {
        int score = 0;
        for (String str : strings) {
            if (!isValidWord(str)) return -1;
            score += getScore(str);
        }
        return score;
    }

    /**
     * Returns true if a valid word can be formed
     * from the given string.
     * if it contains blank character(s),
     * by replacing each blank with each letter
     * @param str string
     * @return true if a valid word can be formed from the given string, false otherwise
     */
    public static boolean isValidWord(String str) {
        String[] arr = str.split("((?<="+Main.BLANK+")|(?="+Main.BLANK+"))");
        for (String letter : letters.keySet()) {
            if (letter.equals("Blank")) break;
            String test = "";
            for (String s : arr) {
                if (s.equals(Main.BLANK)) {
                    test += letter;
                    return isValidWord(test + str.substring((ArrUtils.indexOf(arr, s))+5));
                }
                else test += s;
            }
            if (dictionary.contains(test)) return true;
        }
        return false;
    }

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

    public static int getValue(String letter) {
        return letters.get(letter);
    }
}
