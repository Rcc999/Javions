package ch.epfl.javions;

import java.util.Objects;

public class Bits {
    /**
     * Constructor: private - non instantiable
     */
    private Bits (){}

    /**
     *
     * @param value : the 64 bits vector
     * @param start :  the index of the bit we start at
     * @param size : the length we want to check
     * @return extracts the value in a unsigned way with the parameters given or throws an
     *      * IllegalArgumentException if the length is not compatible with the start and if the range described by start and size is not well framed
     */
    public static int extractUInt(long value, int start, int size){
        Preconditions.checkArgument(!(size <= 0 || size >= Integer.SIZE));
        Objects.checkFromIndexSize(start, size, Long.SIZE);
        long mask = (1L << size) - 1L;
        return (int) ((value >> start) & mask);
    }

    /**
     *
     * @param value : the 64 bits vector
     * @param index : the index of the bit given
     * @return  returns true if and only if the given index value bit is 1,
     * or throws IndexOutOfBoundsException if it is not well framed
     */
    public static boolean testBit(long value, int index) {
        Objects.checkIndex(index, Long.SIZE);
        return ((value >>> index) & 1L) == 1L;
    }
}
