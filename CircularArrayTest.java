package sample;

import org.junit.Test;

import static org.junit.Assert.*;

public class CircularArrayTest {

    @Test
    public void testConstructor() {
        CircularArray<String> arr = new CircularArray<>();

        arr = new CircularArray<>(5);
        assertEquals(0, arr.size());

        arr = new CircularArray<>("a","d","b");
        assertEquals(3, arr.size());
        assertEquals("[a, d, b, null, null, null, null, null, null, null]", arr.toString());
    }

    @Test
    public void testAdd() {
        CircularArray<Integer> arr = new CircularArray<>(3);
        arr.add(1);
        arr.add(2);
        arr.add(2);
        arr.add(4);

        assertEquals("[1, 2, 2, 4]", arr.toString());
    }

    @Test
    public void testNext() {
        CircularArray<Integer> arr = new CircularArray<>(3);
        arr.add(1);
        arr.add(2);
        arr.add(2);
        arr.add(4);

        assertEquals((Integer)1, arr.next());
        assertEquals((Integer)2, arr.next());
        assertEquals((Integer)2, arr.next());
        assertEquals((Integer)4, arr.next());
        assertEquals((Integer)1, arr.next());

    }

}