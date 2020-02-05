package sample;

import java.util.List;

//todo
public class Trie {
    private class Node {
        private Node parent;
        private Node[] children;
        private char val;

        Node() {

        }

        char getVal() {
            return val;
        }
    }

    private Node root;

    Trie() {
        root = null;
    }

    /**
     * Constructs a trie from a file of line-separated words
     * @param fname
     */
    Trie(String fname) {
        List<String> words = FileUtils.read(fname);
        for (String word : words) {
//            add(words);
        }
    }

    /**
     * Constructs a trie from a list of words
     * @param words
     */
    Trie(List<String> words) {
        for (String word : words) {
            add(word);
        }
    }

    private void add(String word) {

    }

//    private Node search(Node root, char c) {
//        Node node = get(root.children, c);
//    }
}
