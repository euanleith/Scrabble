package sample;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Abstract class containing a number of file related functions
 */
public abstract class FileUtils {

    /**
     * Reads the contents of a file
     * @param fname File name
     * @return Contents of file as an ArrayList of Strings
     */
    static ArrayList<String> read(String fname) {
        File file = new File(fname);
        return read(file);
    }

    static ArrayList<String> read(File file) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));

            ArrayList<String> str = new ArrayList<>();
            String line;
            while ((line = br.readLine()) != null) {
                str.add(line);
            }

            return str;

        } catch (Exception e) {e.printStackTrace();}
        return null;
    }

    static CircularArray<String> readToCircularArray(String fname) {
        File file = new File(fname);
        return readToCircularArray(file);
    }

    static CircularArray<String> readToCircularArray(File file) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));

            CircularArray<String> str = new CircularArray<>();
            String line;
            while ((line = br.readLine()) != null) {
                str.add(line);
            }

            return str;

        } catch (Exception e) {e.printStackTrace();}
        return null;
    }

    static HashMap<String, Integer> readToMap(String fname, String delimeter) {
        HashMap<String, Integer> map = new HashMap<>();
        ArrayList<String> file = FileUtils.read(fname);
        if (file == null) return null;
        for (String line : file) {
            String[] s = line.split(delimeter); // str num val
            String key = s[0];
            int val = Integer.parseInt(s[1]);

            map.put(key, val);
        }
        return map;
    }

    static Trie readToTrie(String fname) {
        Trie out = new Trie();
        ArrayList<String> file = read(fname);
        if (file == null) return null;
        for (String line : file) {
            out.add(line);
        }
        return out;
    }

    /**
     * Write to a file
     * @param fname File name
     * @param contents Contents to be written
     */
    public static void write(String fname, String[] contents) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(fname));
            for (String line : contents) {
                writer.write(line+"\n");
            }
            writer.close();
        } catch (Exception e) {e.printStackTrace();}
    }

    /**
     * Prints the contents of a file
     * @param fname File name
     */
    public static void print(String fname) {
        try (BufferedReader br = new BufferedReader(new FileReader(fname))) {
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Deletes a file
     * @param fname File name
     * @return true if the file is successfully deleted
     */
    public static boolean delete(String fname) {
        File file = new File(fname);
        return file.delete();
    }
}
