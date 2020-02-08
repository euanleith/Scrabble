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
        Board board = new Board(15, 15, new CircularArray<>(new Player("Jim")));//todo shouldn't have to do this

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

            if (valid[i]) assertNotEquals(-1, getValues(tiles, Main.boardTiles));
            else assertEquals(-1, getValues(tiles, Main.boardTiles));
        }
    }

    @Test
    public void testGetScore() {
        // test with null string
        assertEquals(-1, getValue(null));

        // test with no blanks
        assertEquals(8, getValue("WORD"));
        assertEquals(12, getValue("DIABOLISE"));

        // test with one blank
        assertEquals(9, getValue("WBlankRD"));
        assertEquals(12, getValue("BlankIABOLISE"));

        // test with multiple blanks
        assertEquals(9, getValue("WBlankRBlank"));
        assertEquals(14, getValue("BlankIABBlankBlankISE"));

        // test with all blanks
        assertEquals(2, getValue("Blank"));
        assertEquals(6, getValue("BlankBlankBlank"));
    }

    @Test
    public void testIsValidWord() {
        // test with null string
        assertFalse(isValidWord(null));

        // test with no blanks
        assertTrue(isValidWord("WORD"));
        assertTrue(isValidWord("DIABOLISE"));

        // test with one blank
        assertTrue(isValidWord("WBlankRD"));
        assertTrue(isValidWord("BlankIABOLISE"));
        assertTrue(isValidWord("DIABlankOLISE"));
        assertTrue(isValidWord("DIABBlankLISE"));

        //todo some require iterative method
        // test with multiple blanks
        assertTrue(isValidWord("WBlankRBlank"));
//        assertTrue(isValidWord("DIABlankOBlankISE"));
//        assertTrue(isValidWord("DIABlankOBlankBlankSE"));

        // test with all blanks
//        assertTrue(isValidWord("Blank"));
//        assertTrue(isValidWord("BlankBlank"));
//        assertTrue(isValidWord("blankblankblankblank"));
    }
}