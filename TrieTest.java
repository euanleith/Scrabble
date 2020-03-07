package sample;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.function.Function;

import static org.junit.Assert.*;
import static sample.Main.getTimeTaken;

public class TrieTest {

    @Test
    public void testStartsWith() {
        ArrayList<String> list = FileUtils.read("src/sample/std_dict.txt");
        Trie trie = new Trie(list);

        assertTrue(trie.startsWith("A"));

        assertTrue(trie.startsWith("DIABOLISE"));

        assertTrue(trie.startsWith("DIABOLISED"));

        assertFalse(trie.startsWith("ZZZZ"));
    }

    @Test
    public void testSearch() {
        ArrayList<String> list = FileUtils.read("src/sample/std_dict.txt");
        Trie trie = new Trie(list);

        ArrayList<String> trieStrs = trie.search("DIABOLISE");
        Collections.sort(trieStrs);
        assertEquals("[DIABOLISE, DIABOLISED, DIABOLISES]",
                trieStrs.toString());

        trieStrs = trie.search("DIABOLISE", 2);
        Collections.sort(trieStrs);
        assertEquals("[DIABOLISE, DIABOLISES]",
                trieStrs.toString());

        trieStrs = trie.search("DIABOLISES", 3);
        Collections.sort(trieStrs);
        assertEquals("[DIABOLISES]",
                trieStrs.toString());

        trieStrs = trie.search("ZZZZ");
        Collections.sort(trieStrs);
        assertEquals("[]",
                trieStrs.toString());
    }

    @Test
    public void isEmpty() {
        ArrayList<String> list = FileUtils.read("src/sample/std_dict.txt");

        Trie trie = new Trie(list);
        assertFalse(trie.isEmpty());

        trie = new Trie();
        assertTrue(trie.isEmpty());
    }

    //todo redo
    @Test
    public void testToString() {
        String[] arr = new String[]{
                "abc"
        };
        Trie trie = new Trie(arr);

        assertEquals("[abc]", trie.search("abc").toString());

        trie.add("abd");

        assertEquals("", trie.toString());
    }

    /**
     * Tests the speed of trie.contains vs. arraylist.contains
     */
    @Test
    public void testSpeed() {
        System.err.println("Time to find words in dictionary;\n");
        ArrayList<String> list = FileUtils.read("src/sample/std_dict.txt");
        Trie trie = new Trie(list);
        String[] words = new String[]{"AA","MALPOSED","ZZZS"};
        for (String word : words) {
            System.err.println(word);
            System.err.println("list: " + getTimeTaken(list::contains, word));
            System.err.println("trie: " + getTimeTaken(trie::contains, word));
            System.err.println();
        }
    }
}