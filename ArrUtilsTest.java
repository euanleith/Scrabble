package sample;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static sample.ArrUtils.*;

public class ArrUtilsTest {

    @Test
    public void testIndexOf() {
        String[] arr = new String[]{"a", "bc", "f"};

        assertEquals(0, indexOf(arr, "a"));
        assertEquals(1, indexOf(arr, "bc"));
        assertEquals(-1, indexOf(arr, "b"));
    }

    @Test
    public void testGetSurrounding() {
        String[][] arr = new String[][]{
                new String[]{"a","bc",null},
                new String[]{"f",null,null},
                new String[]{null,null,null}
        };

        assertArrayEquals(new String[]{"bc","f"}, getSurrounding(0, 0, arr).toArray());
        assertArrayEquals(new String[]{"a"}, getSurrounding(0, 1, arr).toArray());
        assertArrayEquals(new String[]{"bc","f"}, getSurrounding(1, 1, arr).toArray());
        assertArrayEquals(new String[]{}, getSurrounding(2, 1, arr).toArray());
    }

    @Test
    public void testGetSurrounding1() {
        String s1 = "a";
        String s2 = "bc";
        String s3 = "f";
        String[][] arr = new String[][]{
                new String[]{s1,s2,null},
                new String[]{s3,null,null},
                new String[]{null,null,null}
        };

        assertArrayEquals(new String[]{"bc","f"}, getSurrounding(s1, arr).toArray());
        assertArrayEquals(new String[]{"a"}, getSurrounding(s2, arr).toArray());
    }

    @Test
    public void testIsEmpty() {
        List<List<String>> list = new ArrayList<>();

        assertTrue(isEmpty(list));

        list.add(new ArrayList<>());
        list.add(new ArrayList<>());
        assertTrue(isEmpty(list));

        list.get(0).add("a");
        assertFalse(isEmpty(list));

        list.get(0).add("bc");
        list.get(1).add(" d");
        assertFalse(isEmpty(list));
    }

    @Test
    public void testToArr() {
        assertNull(toArr(null));

        ArrayList<String> list = new ArrayList<>();
        assertArrayEquals(new String[]{}, toArr(list));

        list.add("a");
        assertArrayEquals(new String[]{"a"}, toArr(list));

        list.add("bc");
        list.add(" d");

        assertArrayEquals(new String[]{"a","bc"," d"}, toArr(list));
    }

    @Test
    public void testToString() {
        assertNull(ArrUtils.toString(null));

        String[] arr = new String[]{};

        assertEquals("", ArrUtils.toString(arr));

        arr = new String[]{"a","bc"," d"};

        assertEquals("abc d", ArrUtils.toString(arr));

        assertEquals("a, bc,  d", ArrUtils.toString(arr, ", "));

        assertEquals("a|bc| d", ArrUtils.toString(arr, "|"));
    }
}