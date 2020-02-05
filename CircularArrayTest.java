package sample;

import org.junit.Test;

import static org.junit.Assert.*;

public class CircularArrayTest {

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