package sample;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//todo clean
public class Trie {

    /**
     * Class Node with a char value, a parent, and children.
     * Trie made up of these nodes, starting with the 'root' node
     * which has no char value of parent, and following its children
     */
    private class Node {
        char c;
        Node parent;
        HashMap<Character, Node> children = new HashMap<>();
        boolean isLeaf;

        /**
         * Empty constructor for root node (with no char or parent)
         */
        Node() {
        }

        /**
         * Constructor for std nodes
         * @param c char value of the node
         * @param parent parent of the node
         */
        Node(char c, Node parent) {
            this.c = c;
            this.parent = parent;
        }
    }

    private Node root;

    /**
     * Constructor for an empty Trie
     */
    Trie() {
        root = new Node();
    }

    /**
     * Constructor for a Trie which adds the contents of the list to the Trie
     * @param list string list whose elements are to be added to the tree
     */
    Trie(List<String> list) {
        root = new Node();
        add(list);
    }

    /**
     * Constructor for a Trie which adds the contents of the array to the Trie
     * @param arr string array whose elements are to be added to the tree
     */
    Trie(String[] arr) {
        root = new Node();
        add(arr);
    }

    /**
     * Adds a word to the Trie
     * @param word word to be added
     */
    void add(String word) {
        HashMap<Character, Node> children = root.children;
        Node parent = root;

        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);

            Node t;
            if (children.containsKey(c)) {
                //go down that branch
                t = children.get(c);
            } else if (Character.isUpperCase(c) &&
                    children.containsKey((char)(c+0x20))) {
                t = children.get((char)(c+0x20));
            } else if (Character.isLowerCase(c) &&
                    children.containsKey((char)(c-0x20))) {
                t = children.get((char)(c-0x20));
            } else { // if no child node exists
                // add a new branch
                t = new Node(c, parent);
                children.put(c, t);
            }

            children = t.children; // go down to next level of branch (if there is one)
            parent = t; // set new parent

            // set leaf node
            if (i == word.length()-1)
                t.isLeaf = true;
        }
    }

    /**
     * Adds the contents of a list to the Trie
     * @param list list of strings
     */
    void add(List<String> list) {
        for (String str : list) {
            add(str);
        }
    }

    /**
     * Adds the contents of an array to the Trie
     * @param arr array of strings
     */
    void add(String[] arr) {
        for (String str : arr) {
            add(str);
        }
    }

    /**
     * Returns true if the Trie contains the given string
     * @param str string to be queried
     * @return true is the Trie contains the given string, false otherwise
     */
    boolean contains(String str) {
        Node t = searchNode(str);
        return t != null && t.isLeaf;
    }

    /**
     * Returns true if there exists a word that starts with the given prefix
     * @param prefix prefix to be queried
     * @return true if there exists a word that starts with the given prefix, false otherwise
     */
    public boolean startsWith(String prefix) {
        return searchNode(prefix) != null;
    }

    /**
     * Tries to find the node associated with a given string
     * @param str string to be queried
     * @return the node if it is in the trie, otherwise null
     */
    Node searchNode(String str) {
        HashMap<Character, Node> children = root.children;
        Node t = null;
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (children.containsKey(c)) {
                t = children.get(c);
                children = t.children;
            } else if (c > 0x41 && c < 0x5A && children.containsKey((char)(c+0x20))) {//if uppercase
                t = children.get((char)(c+0x20));
                children = t.children;
            } else if (c > 0x61 && c < 0x7A && children.containsKey((char)(c-0x20))) {//if lowercase
                t = children.get((char)(c-0x20));
                children = t.children;
            } else {
                return null;
            }
        }
        return t;
    }

    //gets all leaf nodes branching from a given root
    //(incomplete) would be nice if ArrayList wasnt param
    //weird with inputs with spaces?
    ArrayList<String> getLeafNodes(Node root, ArrayList<String> leafNodes, String input) {
        if (root.isLeaf) {
            leafNodes.add(input);
            return leafNodes;
        }

        //for each node's children's char
        HashMap<Character, Node> children = root.children;
        for (HashMap.Entry<Character, Node> pair : children.entrySet()) {
            String str = input + pair.getKey();//replace input with root's string
            getLeafNodes(pair.getValue(), leafNodes, str);
        }

        return leafNodes;
    }

    //gets numResults leaf nodes branching from a given root
    //note leafNodes is an array, not an ArrayList as it requires a finite, known length (numResults)
    String[] getLeafNodes(Node root, String[] leafNodes, int numResults, String input) {
        if (root.isLeaf) {
            //leafNodes.add(root)
            for (int i = 0; i < leafNodes.length; i++) {
                if (leafNodes[i] == null) {
                    leafNodes[i] = input;
                    break;
                }
            }
            numResults--;
            return leafNodes;
        }


        //for each node's children's char
        HashMap<Character, Node> children = root.children;//only returning roots for one case
        for (HashMap.Entry<Character, Node> pair : children.entrySet()) {
            //if numResults have been found, return
            if (leafNodes[leafNodes.length-1] != null) return leafNodes;
            //str += pair.getKey();
            String str = input + pair.getKey();
            getLeafNodes(pair.getValue(), leafNodes, numResults, str);
        }

        return leafNodes;
    }

    //returns all search results for a given input
    //(incomplete) add functionality: if input already = an author, go straight to their page
    ArrayList<String> getSearchResults(String input) {

        //get leafNodes
        Node root = searchNode(input);
        if (root != null) {
            //get initial node's string value
            String str = "";
            Node node = root;
            while (node.parent != null) {
                str = node.c + str;
                node = node.parent;
            }
            return getLeafNodes(root, new ArrayList<String>(), str);
        } else return new ArrayList<String>();
    }

    //returns numResults search results for a given input
    //(incomplete) figure out how arr/arraylist stuff should work
    ArrayList<String> getSearchResults(String input, int numResults) {
        //if input already = an author, go straight to their page
        //going through the trie might actually be faster

        //get leafNodes
        String[] leafNodes = new String[numResults];
        Node root = searchNode(input);
        if (root != null) {
            //get initial node's string value
            String str = "";
            Node node = root;
            while (node.parent != null) {
                str = node.c + str;
                node = node.parent;
            }
            leafNodes = getLeafNodes(root, leafNodes, numResults, str);
        }

        //create ArrayList<String> of words from leafNodes
        //(temp)
        ArrayList<String> s = new ArrayList<>();
        for (String node : leafNodes) {
            if (node != null)
                s.add(node);
        }
        return s;
    }
}