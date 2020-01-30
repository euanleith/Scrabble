package sample;

import javafx.geometry.Point2D;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ButtonUtils {
    //todo most if not all of these could be abstracted, or at least just call AbstractUtils within them

    /**
     * Returns the angle of a linear list of buttons
     * @param buttons linear list of buttons
     * @return the angle of a linear list of buttons
     */
    public static double getAngle(List<Button> buttons) {
        if (buttons.size() < 2) return 0;
        return MathsUtils.getAngle(
                buttons.get(0).getLayoutX(), buttons.get(0).getLayoutY(),
                buttons.get(1).getLayoutX(), buttons.get(1).getLayoutY());
    }

    /**
     * Returns a string concatenation of a list of buttons' texts'
     * @param buttons list of buttons
     * @return a string concatenation of a list of buttons' texts'
     */
    public static String toString(List<Button> buttons) {
        String str = "";
        for (Button button : buttons) {
            str += button.getText();
        }
        return str;
    }

    /**
     * Create string from the combination of 'button'
     * and the line it draws through 'buttons'
     * @param button a button
     * @param buttons list of buttons
     * @return string of button and buttons
     */
    public static String toString(Button button, ArrayList<Button> buttons) {
        String str = "";

        ArrayList<Button> sorted = new ArrayList<>(buttons);
        sorted.add(button);

        double angle = ButtonUtils.getAngle(sorted);

        MathsUtils.sortFromAngle(sorted, angle, Button::getLayoutX, Button::getLayoutY);

        for (Button b : sorted) {
            str += b.getText();
        }
        return str;
    }

    /**
     * Checks if the button is in the centre index of its GridPane
     * @param button button
     * @return true if the button is in the centre index of its GridPane, false otherwise
     */
    static boolean isInCentre(Button button) {
        GridPane pane = (GridPane)button.getParent();
        return GridPane.getColumnIndex(button) == (pane.getRowCount()/2) &&//todo wrong way around?
                GridPane.getRowIndex(button) == (pane.getColumnCount()/2);
    }

    //todo not working
    //todo returns a list of strings which are the strings created by the new connections on the board from this turn
    //check each button, and for each connection, create a new string from the string of buttons it creates
    //note: assumes list is a line
    public static ArrayList<String> getConnections(ArrayList<Button> buttons, Button[][] board) {
        ArrayList<String> connections = new ArrayList<>();
        for (Button button : buttons) {
            ArrayList<Button> surrounding = getSurroundingButtons(button, board, buttons);
            for (Button s : surrounding) {
                connections.add(ButtonUtils.toString(s, buttons));
            }
        }
        return connections;
    }

    //todo
    /**
     * Returns the list of non-empty buttons surrounding a given button
     * that are within the confines of the board
     * @param button button
     * @param board board of buttons
     * @return list of non-empty buttons surrounding a given button
     * that are within the confines of the board
     */
    public static ArrayList<Button> getSurroundingButtons(Button button, Button[][] board, ArrayList<Button> buttons) {
        int x = GridPane.getRowIndex(button);
        int y = GridPane.getColumnIndex(button);//todo wrong way around?
        ArrayList<Button> surrounding = ArrUtils.getSurrounding(x, y, board);
        surrounding.removeIf(b -> b.getText().equals("") || buttons.contains(b));
        return surrounding;
    }

    /**
     * Checks if the list of buttons are in a connected line
     * @param buttons button list
     * @return true if the list is in a line, false otherwise
     */
    public static boolean isLine(ArrayList<Button> buttons) {
//        ArrayList<Point2D> points = toPoints(buttons);//todo replace below with this
//        return MathsUtils.areHVConnected(points) && MathsUtils.isHVLine(points);
        if (buttons.size() < 2) return true;
        double prevAngle = -1;
        for (int i = 0; i < buttons.size()-1; i++) {
            Button button = buttons.get(i);
            Point2D p1 = new Point2D(GridPane.getColumnIndex(button), GridPane.getRowIndex(button));
            Button button2 = buttons.get(i+1);
            Point2D p2 = new Point2D(GridPane.getColumnIndex(button2), GridPane.getRowIndex(button2));
            double angle = MathsUtils.getAngle(p1.getX(), p1.getY(), p2.getX(), p2.getY());
            if (angle % 90 != 0 || (i != 0 && angle != prevAngle) || !areConnected(p1, p2)) return false;
            prevAngle = angle;
        }
        return true;
    }

    //todo Arrays probably has something that does this
    static ArrayList<Point2D> toPoints(ArrayList<Button> buttons) {
        ArrayList<Point2D> points = new ArrayList<>(buttons.size());
        for (Button button : buttons) {
            points.add(new Point2D(button.getLayoutX(), button.getLayoutY()));
        }
        return points;
    }

    //todo move?
    /**
     * Checks if two points are 1 unit away horizontally or vertically
     * @param p1 first point
     * @param p2 second point
     * @return true if two points are 1 unit away horizontally or vertically, false otherwise
     */
    public static boolean areConnected(Point2D p1, Point2D p2) {
        return Math.max(p1.getX(), p2.getX()) - Math.min(p1.getX(), p2.getX()) == 1 ||
                Math.max(p1.getY(), p2.getY()) - Math.min(p1.getY(), p2.getY()) == 1;
    }

    //todo might still be wrong...
    /**
     * Removes button from a GridPane and shift other items left
     * gridpane.getChildren is an ObservableList, which shifts all values when removing elements
     * gridpane however has separate indices which don't change, so I have to change them manually
     * @param button button to be removed
     */
    public static void remove(GridPane grid, Button button) {
        for (int i = 0; i < grid.getChildren().size(); i++) {
            if (grid.getChildren().get(i) == button) {
                for (int j = i; j < grid.getChildren().size()-1; j++) {
                    GridPane.setColumnIndex(grid.getChildren().get(j+1), j);
                }
                break;
            }
        }
        grid.getChildren().remove(button);
    }
}
