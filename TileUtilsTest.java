package sample;

import javafx.geometry.Point2D;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;
import static sample.TileUtils.*;

public class TileUtilsTest {
    private ArrayList<ArrayList<Tile>> matrix;

    @Before
    public void init() {
        matrix = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            matrix.add(new ArrayList<>());
            for (int j = 0; j < 3; j++) {
                Tile t = new Tile();
                matrix.get(i).add(t);
                t.getImg().setLayoutX(i);
                t.getImg().setLayoutY(j);
            }
        }
    }

    @Test
    public void testGetImgs() {
    }

    @Test
    public void testGetAngle() {
    }

    @Test
    public void testToString() {
    }

    @Test
    public void testToString1() {
    }

    @Test
    public void testToString2() {
    }

    @Test
    public void testIsInCentre() {
    }

    @Test
    public void testGetConnections() {
    }

    @Test
    public void testIsLine() {
        ArrayList<Tile> list = new ArrayList<>();

        // test null
        assertFalse(isLine(null));

        // test empty
        assertTrue(isLine(list));

        // test size of 1
        list.add(matrix.get(0).get(1));
        assertTrue(isLine(list));

        // test std
        list.add(matrix.get(0).get(2));
        assertTrue(isLine(list));

        list.add(matrix.get(1).get(2));
        assertFalse(isLine(list));

        list.remove(2);

        list.add(matrix.get(0).get(0));
        assertFalse(isLine(list));
    }

    @Test
    public void testToPoints() {
        ArrayList<Tile> tiles = new ArrayList<>();

        // test null
        assertNull(toPoints(null));

        // test empty
        assertEquals("", toPointString(toPoints(tiles)));

        // test std
        tiles.add(matrix.get(0).get(1));
        assertEquals("(0.0,1.0)", toPointString(toPoints(tiles)));

        tiles.add(matrix.get(2).get(0));
        assertEquals("(0.0,1.0), (2.0,0.0)", toPointString(toPoints(tiles)));
    }

    @Test
    public void testToPointString() {
        ArrayList<Point2D> list = new ArrayList<>();

        // test null
        assertEquals("null", toPointString(null));

        // test empty
        assertEquals("", toPointString(list));

        // test std
        list.add(new Point2D(0, 1));
        assertEquals("(0.0,1.0)", toPointString(list));

        list.add(new Point2D(3, 2));
        assertEquals("(0.0,1.0), (3.0,2.0)", toPointString(list));

        // test float
        list.add(new Point2D(1.5, 3.53));
        assertEquals("(0.0,1.0), (3.0,2.0), (1.5,3.53)", toPointString(list));

        // test negative
        list.add(new Point2D(-1, -3.53));
        assertEquals("(0.0,1.0), (3.0,2.0), (1.5,3.53), (-1.0,-3.53)", toPointString(list));
    }

    @Test
    public void testAreConnected() {
    }

    @Test
    public void testRemove() {
    }

    @Test
    public void testToUserTile() {
    }
}