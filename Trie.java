package sample;

import java.util.*;

class Trie {

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
        Node parent = root;
        HashMap<Character, Node> children = parent.children;

        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);

            Node child;
            if (children.containsKey(c)) {
                //go down that branch
                child = children.get(c);
            } else if (Character.isUpperCase(c) &&
                    children.containsKey((char)(c+0x20))) {
                child = children.get((char)(c+0x20));
            } else if (Character.isLowerCase(c) &&
                    children.containsKey((char)(c-0x20))) {
                child = children.get((char)(c-0x20));
            } else { // if no child node exists
                // add a new branch
                child = new Node(c, parent);
                children.put(c, child);
            }

            // set leaf node
            if (i == word.length()-1) child.isLeaf = true;

            parent = child; // set new parent
            children = child.children; // go down to next level of branch (if there is one)
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
    boolean startsWith(String prefix) {
        return searchNode(prefix) != null;
    }

    /**
     * Tries to find the node associated with a given string
     * @param str string to be queried
     * @return the node if it is in the trie, otherwise null
     */
    private Node searchNode(String str) {
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

    /**
     * Returns the string representations of all branch nodes from a given node
     * by recursively adding strings to the list if the current node is a leaf
     * Note: 'currentStr' is included for efficiency:
     * instead of getting the string for each node that is a leaf,
     * you can just append it with each child node's char
     * @param node node
     * @param branches list of strings from branches from the root node
     * @param currentStr string of the current node excluding the current node's char value
     * @return the string representations of all branch nodes from a given root node
     */
    private ArrayList<String> getLeafNodes(Node node, ArrayList<String> branches, String currentStr) {
        if (node.isLeaf) branches.add(currentStr);

        // for each node's children
        for (HashMap.Entry<Character, Node> pair : node.children.entrySet()) {
            String str = currentStr + pair.getKey();
            getLeafNodes(pair.getValue(), branches, str);
        }

        return branches;
    }

    /**
     * Returns the string representations of all branch nodes from a given node
     * by recursively adding strings to the list if the current node is a leaf
     * Note: 'currentStr' is included for efficiency:
     * instead of getting the string for each node that is a leaf,
     * you can just append it with each child node's char
     * @param node node
     * @param branches list of strings from branches from the root node
     * @param nResults the maximum number of nodes to be added to the list before returning
     * @param currentStr string of the current node excluding the current node's char value
     * @return the string representations of all branch nodes from a given root node
     */
   private ArrayList<String> getLeafNodes(Node node, ArrayList<String> branches, int nResults, String currentStr) {
        if (node.isLeaf) branches.add(currentStr);

        // if numResults have been found, return
        if (branches.size() == nResults) return branches;

        // for each node's children
        for (HashMap.Entry<Character, Node> pair : node.children.entrySet()) {
            String str = currentStr + pair.getKey();
            getLeafNodes(pair.getValue(), branches, nResults, str);

            // if numResults have been found, return
            if (branches.size() == nResults) return branches;
        }

        return branches;
    }

    /**
     * Returns an unordered list of strings which start with a given prefix
     * @param prefix prefix to be searched
     * @return a list of strings which start with a given prefix
     */
    ArrayList<String> search(String prefix) {

        Node root = searchNode(prefix);

        if (root == null) return new ArrayList<>();

        return getLeafNodes(root, new ArrayList<>(), toString(root));
    }

    /**
     * Returns an unordered list of strings which start with a given prefix
     * @param prefix prefix to be searched
     * @param nResults the maximum number of results to be returned
     * @return a list of strings which start with a given prefix
     */
    ArrayList<String> search(String prefix, int nResults) {

        Node root = searchNode(prefix);

        if (root == null) return new ArrayList<>();

        ArrayList<String> branches = new ArrayList<>(nResults);
        return getLeafNodes(root, branches, nResults, toString(root));
    }

    /**
     * Returns a string representation of a node
     * by following its path to the root node
     * @param in node
     * @return a string representation of a node
     */
    private String toString(Node in) {
        String out = "";
        Node node = in;
        while (node.parent != null) {
            out = node.c + out;
            node = node.parent;
        }
        return out;
    }

    /**
     * Returns true if the trie is empty
     * @return true if the trie is empty, otherwise false
     */
    boolean isEmpty() {
        return root.children.size() == 0;
    }

    //todo
    /**
     * Returns a string representation of the trie
     * @return a multi-line string with the pretty ascii picture of the tree.
     */
    @Override
    public String toString() {
        if(isEmpty()) return "-null\n";
        return toString(root, "") + "\n";
    }

    private String toString(Node parent, String prefix) {
        String str = prefix + "-" + parent.c;
        ArrayList<String> children = new ArrayList<>(parent.children.size());
        for (Node child : parent.children.values()) {
            children.add(toString(child, prefix));
        }
        return str + "\n" + toString(children);
    }

    private String toString(ArrayList<String> children) {
        String out = "";
        for (String child : children) {
            out += child + "\n";
        }
        return out;
    }
}