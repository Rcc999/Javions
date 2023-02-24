package ch.epfl.javions;

import java.util.Objects;

public class Bits {

    private Bits (){}

    public static int extractUInt(long value, int start, int size){
        Preconditions.checkArgument(!(size <= 0 || size >= Integer.SIZE));
        Objects.checkFromIndexSize(start, size, Long.SIZE);
        long mask = (1L << size) - 1L;
        return (int) ((value >> start) & mask);
    }

    public static boolean testBit(long value, int index) {
        Objects.checkIndex(index, Long.SIZE);
        return ((value >>> index) & 1L) == 1L;
    }
}
