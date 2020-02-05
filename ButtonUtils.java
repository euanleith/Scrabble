package sample;

import javafx.geometry.Point2D;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;

import static sample.ArrUtils.getSurrounding;

public abstract class ButtonUtils {//todo name
    //todo most if not all of these could be abstracted, or at least just call AbstractUtils within them

    /**
     * Returns the angle of a linear list of buttons
     * @param tiles linear list of tiles
     * @return the angle of a linear list of tiles
     */
    public static double getAngle(List<Tile> tiles) {
        if (tiles.size() < 2) return 0;
        return MathsUtils.getAngle(
                tiles.get(0).getImg().getLayoutX(), tiles.get(0).getImg().getLayoutY(),
                tiles.get(1).getImg().getLayoutX(), tiles.get(1).getImg().getLayoutY());
    }

    /**
     * Returns a string concatenation of a list of tiles' texts'
     * @param tiles list of tiles
     * @return a string concatenation of a list of tiles' texts'
     */
    public static String toString(List<Tile> tiles) {
        String str = "";
        for (Tile tile : tiles) {
            str += tile.getText();
        }
        return str;
    }

    public static ArrayList<String> toString(ArrayList<ArrayList<Tile>> tiles) {
        ArrayList<String> result = new ArrayList<>();
        for (ArrayList<Tile> list : tiles) {
            result.add(toString(list));
        }
        return result;
    }

    /**
     * Create string from the combination of 'tile'
     * and the line it draws through 'tiles'
     * @param tile a tile
     * @param tiles list of tiles
     * @return string of tile and tiles
     */
    public static String toString(Tile tile, ArrayList<Tile> tiles) {
        String str = "";

        ArrayList<Tile> sorted = new ArrayList<>(tiles);
        sorted.add(tile);

        double angle = getAngle(sorted);

        //todo might want getX() instead of getLayoutX()?
        MathsUtils.sortFromAngle(sorted, angle, Tile::getX, Tile::getY);

        for (Tile t : sorted) {
            str += t.getText();
        }
        return str;
    }

    /**
     * Checks if the tile is in the centre index of its GridPane
     * @param tile tile
     * @return true if the tile is in the centre index of its GridPane, false otherwise
     */
    static boolean isInCentre(Tile tile) {
        ImageView img = tile.getImg();
        GridPane pane = (GridPane)img.getParent();
        return GridPane.getColumnIndex(img) == (pane.getRowCount()/2) &&//todo wrong way around?
                GridPane.getRowIndex(img) == (pane.getColumnCount()/2);
    }

    //todo remove duplicates? (not always... just -)
    /**
     * todo
     * @param tiles
     * @param board
     * @return
     */
    static ArrayList<ArrayList<Tile>> getConnections(ArrayList<Tile> tiles, Tile[][] board) {
        double angle = getAngle(tiles);
        ArrayList<ArrayList<Tile>> connections = new ArrayList<>();
        for (Tile tile : tiles) {
            ArrayList<Tile> surrounding = getSurroundingTiles(tile, tiles, board);
            for (Tile connected : surrounding) {
                ArrayList<Tile> newList = new ArrayList<>(tiles);
                newList.add(connected);
                MathsUtils.sortFromAngle(newList, angle, Tile::getX, Tile::getY);
                connections.add(newList);
            }
        }
        return connections;
    }

    /**
     * Returns the list of non-empty tiles surrounding a given tile
     * that are within the confines of the board
     * @param tile tile
     * @param board board of tiles
     * @return list of non-empty tiles surrounding a given tile
     * that are within the confines of the board
     */
    static ArrayList<Tile> getSurroundingTiles(Tile tile, ArrayList<Tile> tiles, Tile[][] board) {
        ArrayList<Tile> surrounding = new ArrayList<>();//todo~
        ArrayList<Pair<Integer, Integer>> positions = new ArrayList<>();
        positions.add(new Pair<>(0,-1));
        positions.add(new Pair<>(-1,0));
        positions.add(new Pair<>(1,0));
        positions.add(new Pair<>(0,1));
        for (Pair<Integer, Integer> pos : positions) {
            int x = GridPane.getColumnIndex(tile.getImg()) + pos.getKey();
            int y = GridPane.getRowIndex(tile.getImg()) + pos.getValue();
            if (x >= 0 && x < board.length &&
                    y >= 0 && y < board[x].length &&
                    !board[x][y].getText().equals(Main.BLANK) &&//todo this will need to be changed when what denotes a used board tile changes
                    !tiles.contains(board[x][y])) {
                surrounding.add(board[x][y]);
            }
        }
        return surrounding;
    }

    //todo
    /**
     * Returns the list of non-empty tiles surrounding a given tile
     * that are within the confines of the board
     * @param tile tile
     * @param board board of tiles
     * @return list of non-empty tiles surrounding a given tile
     * that are within the confines of the board
     */
    public static ArrayList<Tile> getSurroundingTiles(Tile tile, Tile[][] board, ArrayList<Tile> tiles) {
        int x = GridPane.getRowIndex(tile.getImg());
        int y = GridPane.getColumnIndex(tile.getImg());//todo wrong way around?
        ArrayList<Tile> surrounding = getSurrounding(x, y, board);
        surrounding.removeIf(b -> b.getText().equals("") || tiles.contains(b));
        return surrounding;
    }

    /**
     * Checks if the list of tiles are in a connected line
     * @param tiles tile list
     * @return true if the list is in a line, false otherwise
     */
    public static boolean isLine(ArrayList<Tile> tiles) {
//        ArrayList<Point2D> points = toPoints(tiles);//todo replace below with this
//        return MathsUtils.areHVConnected(points) && MathsUtils.isHVLine(points);
        if (tiles.size() < 2) return true;
        double prevAngle = -1;
        for (int i = 0; i < tiles.size()-1; i++) {
            ImageView img1 = tiles.get(i).getImg();
            Point2D p1 = new Point2D(GridPane.getColumnIndex(img1), GridPane.getRowIndex(img1));
            ImageView img2 = tiles.get(i+1).getImg();
            Point2D p2 = new Point2D(GridPane.getColumnIndex(img2), GridPane.getRowIndex(img2));
            double angle = MathsUtils.getAngle(p1.getX(), p1.getY(), p2.getX(), p2.getY());
            if (angle % 90 != 0 || (i != 0 && angle != prevAngle) || !areConnected(p1, p2)) return false;
            prevAngle = angle;
        }
        return true;
    }

    public static boolean areLines(ArrayList<ArrayList<Tile>> tiles) {
        for (ArrayList<Tile> list : tiles) {
            if (!isLine(list)) return false;
        }
        return true;
    }

    //todo Arrays probably has something that does this
    static ArrayList<Point2D> toPoints(ArrayList<Tile> tiles) {
        ArrayList<Point2D> points = new ArrayList<>(tiles.size());
        for (Tile tile : tiles) {
            ImageView img = tile.getImg();
            points.add(new Point2D(img.getLayoutX(), img.getLayoutY()));
        }
        return points;
    }

    //todo move
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
     * Removes tile from a GridPane and shift other items left
     * gridpane.getChildren is an ObservableList, which shifts all values when removing elements
     * gridpane however has separate indices which don't change, so I have to change them manually
     * @param tile tile to be removed
     */
    public static void remove(GridPane grid, Tile tile) {
        for (int i = 0; i < grid.getChildren().size(); i++) {
            if (grid.getChildren().get(i) == tile.getImg()) {
                for (int j = i; j < grid.getChildren().size()-1; j++) {
                    GridPane.setColumnIndex(grid.getChildren().get(j+1), j);
                }
                break;
            }
        }
        grid.getChildren().remove(tile.getImg());
    }
}