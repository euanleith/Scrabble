package sample;

import java.util.AbstractList;
import java.util.Arrays;
import java.util.List;
import java.util.RandomAccess;

//todo more like CircularIndexedArray or something
public class CircularArray<T> extends AbstractList<T>
        implements List<T>, RandomAccess, Cloneable, java.io.Serializable {
    private static final int DEFAULT_CAPACITY = 10;

    private Object[] arr;
    private int outIndex;
    private int inIndex;

    public CircularArray() {
        arr = new Object[DEFAULT_CAPACITY];
        outIndex = 0;
        inIndex = 0;
    }

    public CircularArray(int size) {
        arr = new Object[size];
        outIndex = 0;
        inIndex = 0;
    }

    public boolean add(T obj) {
        if (inIndex >= arr.length) {
            Object[] newArr = new Object[arr.length+1];
            System.arraycopy(arr, 0, newArr, 0, arr.length);
            arr = newArr;
        }

        arr[inIndex++] = obj;
        return true;//todo temp
    }

    public T next() {
        if (outIndex >= arr.length) outIndex = 0;
        T obj = (T) arr[outIndex++];
        return obj;
    }

    public T peek() {
        if (outIndex >= arr.length) outIndex = 0;
        return (T) arr[outIndex];
    }

    public T get(int index) {
        return (T) arr[index];
    }

    public int size() {
        return arr.length;
    }

    @Override
    public String toString() {
        return Arrays.toString(arr);
    }
}