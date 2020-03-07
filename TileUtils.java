package sample;

import javafx.geometry.Point2D;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

import java.util.ArrayList;
import java.util.List;

import static sample.ArrUtils.getSurrounding;

abstract class TileUtils {
    static final int IMG_SIZE = 35;

    static ImageView[] getImgs(ArrayList<UserTile> tiles) {
        ImageView[] rackImgs = new ImageView[tiles.size()];
        for (int i = 0; i < tiles.size(); i++) {
            rackImgs[i] = tiles.get(i).getImg();
        }
        return rackImgs;
    }

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
    public static String toString(List<UserTile> tiles) {
        String str = "";
        for (UserTile tile : tiles) {
            str += tile.getText();
        }
        return str;
    }

    public static ArrayList<String> toString(ArrayList<ArrayList<UserTile>> tiles) {
        ArrayList<String> result = new ArrayList<>();
        for (ArrayList<UserTile> list : tiles) {
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
    public static String toString(UserTile tile, ArrayList<UserTile> tiles) {
        String str = "";

        ArrayList<Tile> temp = new ArrayList<>(tiles);
        temp.add(tile);

        double angle = getAngle(temp);

        ArrayList<UserTile> sorted = new ArrayList<>(tiles);
        sorted.add(tile);

        MathsUtils.sortFromAngle(sorted, angle, Tile::getX, Tile::getY);

        for (UserTile t : sorted) {
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

    /**
     * Returns the list of tile lists that are made from the combination of
     * those tiles and any connected tiles
     * @param tiles list of tiles
     * @param board set the tiles are elements of
     * @param type todo
     * @return the list of connected tile lists
     */
    static ArrayList<ArrayList<Tile>> getConnections(List<Tile> tiles, Tile[][] board, Object type) {
        ArrayList<ArrayList<Tile>> connections = new ArrayList<>();

        for (Tile tile : tiles) {

            ArrayList<Tile> surrounding = getSurrounding(tile, board,
                    (Tile t) -> !tiles.contains(t) && t.getClass().equals(type));

            for (Tile connected : surrounding) {
                ArrayList<Tile> newList = new ArrayList<>(tiles);
                newList.add(connected);
                if (!connections.contains(newList)) connections.add(newList);//todo might not work for two separate connections with the same string (i think it will though)
            }
        }
        return connections;
    }

    //todo rename isHVLine?
    //todo is this already ordered? yes
    /**
     * Returns true if the elements of an ordered list of tiles are in a connected line,
     * @param tiles ordered list of tiles
     * @return true if the list is in a line, false otherwise
     */
    public static boolean isLine(ArrayList<Tile> tiles) {
        ArrayList<Point2D> points = toPoints(tiles);//todo replace below with this
        return MathsUtils.areUnitDstX(points) || MathsUtils.areUnitDstY(points);
    }

    //todo Arrays probably has something that does this
    static ArrayList<Point2D> toPoints(ArrayList<Tile> tiles) {
        if (tiles == null) return null;
        ArrayList<Point2D> points = new ArrayList<>(tiles.size());
        for (Tile tile : tiles) {
            points.add(new Point2D(tile.getX(), tile.getY()));
        }
        return points;
    }

    //todo move, name
    public static String toPointString(List<Point2D> list) {
        if (list == null) return "null";
        String str = "";
        for (int i = 0; i < list.size(); i++) {
            Point2D point = list.get(i);
            str += "(" + point.getX() + "," + point.getY() + ")";
            if (i < list.size()-1) str += ", ";
        }
        return str;
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

    /**
     * Converts a 2d list of Tiles to UserTiles
     * @param in 2d list of Tiles
     * @return 2d list of UserTiles
     */
    public static ArrayList<ArrayList<UserTile>> toUserTile(ArrayList<ArrayList<Tile>> in) {
        ArrayList<ArrayList<UserTile>> out = new ArrayList<>(in.size());
        for (ArrayList<Tile> tiles : in) {
            out.add(new ArrayList<>(tiles.size()));
            for (Tile t : tiles) {
                out.get(out.size()-1).add((UserTile) t);
            }
        }
        return out;
    }
}
