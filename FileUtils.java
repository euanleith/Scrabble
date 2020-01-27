package sample;

import java.io.*;
import java.util.ArrayList;

/**
 * Abstract class containing a number of file related functions
 */
public abstract class FileUtils {

    /**
     * Reads the contents of a file
     * @param fname File name
     * @return Contents of file as an ArrayList of Strings
     */
    public static ArrayList<String> read(String fname) {
        try {
            File file = new File(fname);

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
