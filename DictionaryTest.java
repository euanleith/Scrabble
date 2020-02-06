package sample;

import javafx.scene.layout.GridPane;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;
import static sample.Dictionary.*;

public class DictionaryTest {

    @Test
    public void testGetScores() {
        GridPane grid = new GridPane();
        Board board = new Board(15, 15, new CircularArray<>(new Player("Jim")));

        String[][] words = new String[][]{
                new String[]{"W","O","R","D"},
                new String[]{"W","Blank","R","D"},
                new String[]{"W","L","R","D"}
        };
        boolean[] valid = new boolean[]{
                true,
                true,
                false
        };

        for (int i = 0; i < words.length; i++) {
            ArrayList<ArrayList<UserTile>> tiles = new ArrayList<>();
            ArrayList<UserTile> list = new ArrayList<>();

            for (int j = 0; j < words[i].length; j++) {
                UserTile t = new UserTile(words[i][j]);
                grid.add(t.getImg(), i, i);
                list.add(t);
            }
            tiles.add(list);

            if (valid[i]) assertNotEquals(-1, getScores(tiles, Main.boardTiles));
            else assertEquals(-1, getScores(tiles, Main.boardTiles));
        }
    }

    @Test
    public void testIsValidWord() {
        assertTrue(isValidWord("WORD"));
        assertTrue(isValidWord("WBlankRD"));
        String s = null;
        assertFalse(isValidWord(s));
    }
}