package sample;

import javafx.geometry.Point2D;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;
import static sample.MathsUtils.*;

public class MathsUtilsTest {

    @Test
    public void testFactorial() {
        assertEquals(1, factorial(0));
        assertEquals(1, factorial(1));
        assertEquals(2, factorial(2));
        assertEquals(6, factorial(3));
        assertEquals(24, factorial(4));
        assertEquals(3628800, factorial(10));
    }

    @Test
    public void testIsUnitDst() {
        // test null
        assertFalse(isUnitDst(null,new Point2D(0, 0)));

        // test equal
        assertFalse(isUnitDst(new Point2D(0, 0),new Point2D(0, 0)));
        assertFalse(isUnitDst(new Point2D(5, 5),new Point2D(5, 5)));

        // test std
        assertTrue(isUnitDst(new Point2D(1, 0),new Point2D(0, 1)));
        assertTrue(isUnitDst(new Point2D(2, 1),new Point2D(1, 0)));
        assertFalse(isUnitDst(new Point2D(2, 0),new Point2D(1, 0)));
        assertFalse(isUnitDst(new Point2D(2, 0),new Point2D(1, 6)));
        assertFalse(isUnitDst(new Point2D(3, 2),new Point2D(5, 2)));
        assertFalse(isUnitDst(new Point2D(3, 6),new Point2D(5, 2)));

        // test float
        assertTrue(isUnitDst(new Point2D(2, 1.5),new Point2D(1, 2.5)));
//        assertTrue(isUnitDst(new Point2D(2.3, 1.5),new Point2D(1.3, 2.5)));//todo note rounding error occurs here
        assertFalse(isUnitDst(new Point2D(2.4, 1),new Point2D(5.2, 1)));

        // test negative
        assertTrue(isUnitDst(new Point2D(-2.4, 2),new Point2D(-3.4, 1)));
        assertTrue(isUnitDst(new Point2D(-2.4, -0.5),new Point2D(-3.4, 0.5)));
        assertFalse(isUnitDst(new Point2D(-2.4, 1),new Point2D(5, 2)));
    }

    @Test
    public void testIsUnitDstX() {
        // test null
        assertFalse(isUnitDstX(null,new Point2D(0, 0)));

        // test equal
        assertFalse(isUnitDstX(new Point2D(0, 0),new Point2D(0, 0)));
        assertFalse(isUnitDstX(new Point2D(5, 5),new Point2D(5, 5)));

        // test std
        assertTrue(isUnitDstX(new Point2D(2, 1),new Point2D(1, 1)));
        assertFalse(isUnitDstX(new Point2D(0, 0),new Point2D(0, 1)));
        assertFalse(isUnitDstX(new Point2D(2, 1),new Point2D(5, 2)));
        assertFalse(isUnitDstX(new Point2D(3, 2),new Point2D(5, 2)));
        assertFalse(isUnitDstX(new Point2D(3, 6),new Point2D(5, 2)));

        // test float
        assertTrue(isUnitDstX(new Point2D(2.4, 0),new Point2D(3.4, 0)));
//        assertTrue(isUnitDstX(new Point2D(2.3, 1.5),new Point2D(1.3, 2.5))); //todo note rounding error occurs here
        assertFalse(isUnitDstX(new Point2D(2, 1.5),new Point2D(5, 2.5)));

        // test negative
        assertTrue(isUnitDstX(new Point2D(-2.4, 2),new Point2D(-3.4, 2)));
        assertFalse(isUnitDstX(new Point2D(-2.4, 2),new Point2D(5, 2)));
    }

    @Test
    public void testIsUnitDstY() {
        // test null
        assertFalse(isUnitDstY(null,new Point2D(0, 0)));

        // test equal
        assertFalse(isUnitDstY(new Point2D(0, 0),new Point2D(0, 0)));
        assertFalse(isUnitDstY(new Point2D(5, 5),new Point2D(5, 5)));

        // test std
        assertTrue(isUnitDstY(new Point2D(0, 0),new Point2D(0, 1)));
        assertFalse(isUnitDstY(new Point2D(2, 1),new Point2D(1, 1)));
        assertTrue(isUnitDstY(new Point2D(2, 1),new Point2D(5, 2)));
        assertFalse(isUnitDstY(new Point2D(3, 2),new Point2D(5, 2)));
        assertFalse(isUnitDstY(new Point2D(3, 6),new Point2D(5, 2)));

        // test float
        assertTrue(isUnitDstY(new Point2D(1, 2.4),new Point2D(1, 3.4)));
//        assertTrue(isUnitDstY(new Point2D(1, 2.3),new Point2D(1, 1.3))); //todo note rounding error occurs here
        assertFalse(isUnitDstY(new Point2D(2, 0.5),new Point2D(5, 2.5)));

        // test negative
        assertTrue(isUnitDstY(new Point2D(2, -2.4),new Point2D(2, -3.4)));
        assertFalse(isUnitDstY(new Point2D(2, -2.4),new Point2D(2, 5)));
    }

    @Test
    public void testAreUnitDst() {

    }

    @Test
    public void testAreUnitDstX() {
        ArrayList<Point2D> list = new ArrayList<>();

        // test null
        assertFalse(areUnitDstX(null));

        // test empty
        assertTrue(areUnitDstX(list));

        // test list with one item
        list.add(new Point2D(1, 2));
        assertTrue(areUnitDstX(list));

        // test std
        list.add(new Point2D(2, 3));
        assertTrue(areUnitDstX(list));

        list.add(new Point2D(2, 4));
        assertFalse(areUnitDstX(list));

        list.remove(2);

        list.add(new Point2D(4, 3));
        assertFalse(areUnitDstX(list));

        list.remove(2);

        list.add(new Point2D(3, 4));
        assertTrue(areUnitDstX(list));

        list.add(new Point2D(4, 1));
        assertTrue(areUnitDstX(list));

        // test unitDst but not ordered todo
        list.add(new Point2D(0, 4));
        assertFalse(areUnitDstX(list));
    }

    @Test
    public void testAreUnitDstY() {
        ArrayList<Point2D> list = new ArrayList<>();

        // test null
        assertFalse(areUnitDstY(null));

        // test empty
        assertTrue(areUnitDstY(list));

        // test list with one item
        list.add(new Point2D(2, 1));
        assertTrue(areUnitDstY(list));

        // test std
        list.add(new Point2D(3, 2));
        assertTrue(areUnitDstY(list));

        list.add(new Point2D(4, 2));
        assertFalse(areUnitDstY(list));

        list.remove(2);

        list.add(new Point2D(3, 4));
        assertFalse(areUnitDstY(list));

        list.remove(2);

        list.add(new Point2D(4, 3));
        assertTrue(areUnitDstY(list));

        list.add(new Point2D(1, 4));
        assertTrue(areUnitDstY(list));

        // test unitDst but not ordered todo
        list.add(new Point2D(3, 0));
        assertFalse(areUnitDstY(list));
    }

    @Test
    public void testIsHVConnected() {

    }


}