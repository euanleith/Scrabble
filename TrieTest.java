package sample;

import org.junit.Test;

import java.util.ArrayList;
import java.util.function.Function;

public class TrieTest {

    /**
     * Tests the speed of trie.contains vs. arraylist.contains
     */
    @Test
    public void testSpeed() {
        ArrayList<String> list = FileUtils.read("src/sample/dict.txt");
        Trie trie = new Trie(list);
        String[] words = new String[]{"AA","MALPOSED","ZZZS"};
        for (String word : words) {
            System.err.println(word);
            System.err.println("list: " + getTimeTaken(list::contains, word));
            System.err.println("trie: " + getTimeTaken(trie::contains, word));
            System.err.println();
        }

    }

    /**
     * Get the time taken in nanoseconds for a function to run
     * @param func function
     * @param obj object parameter to the function
     * @param <T> function input type
     * @param <U> function output type
     * @return time taken for function to run
     */
    private static <T, U>long getTimeTaken(Function<T, U> func, T obj) {
        final long start = System.nanoTime();
        func.apply(obj);
        final long end = System.nanoTime();
        return end - start;
    }
}