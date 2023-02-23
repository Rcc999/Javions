package ch.epfl.javions;

public class Bits {

    private Bits (){};

    public static int extractUInt(long value, int start, int size){

        Preconditions.checkArgument(size <= 0 || size > Integer.SIZE);

        // Ensure that the range described by start and size is within the bounds of the 64-bit value
        if (start <= 0 || start + size > Long.SIZE) {
            throw new IndexOutOfBoundsException("Invalid range: start=" + start + ", size=" + size);
        }

        // Mask the relevant bits and shift them to the right position
        long mask = (1L << size) - 1;
        int shiftedValue = (int) ((value >> start) & mask);

        return shiftedValue;
    }

    public static boolean testBit(long value, int index) {
        if (index <= 0 || index >= Long.SIZE) {
            throw new IndexOutOfBoundsException("Index must be between 0 (inclusive) and 64 (exclusive)");
        }
        return ((value >>> index) & 1) == 1;
    }

}
