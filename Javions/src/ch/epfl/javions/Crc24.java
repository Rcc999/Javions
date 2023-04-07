package ch.epfl.javions;

/**
 * Calculate the Crc24 using an algorithm of a sequence
 *
 * @author Tuan Dang Nguyen (361089)
 * @author Rayane Charif Chefchouni (339839)
 */
public final class Crc24 {

    public static int GENERATOR = 0xFFF409;
    private final int[] table;
    private static final int LENGTH_CRC24 = 24;
    private static final int TABLE_SIZE = 256;

    /**
     * Public Constructor of the class Crc24, It will build the table of 256 entries that will be used for the calculation of the CRC24
     *
     * @param generator: will be used to do the calculation of crc
     */
    public Crc24(int generator) {
        this.table = buildTable(generator);
    }

    /**
     * Build the table of 256 entries that will be used for the calculation of the CRC24
     *
     * @param generator: will be used for the calculation of crc, using the bit by bit algorithm
     * @return an int table of 256 entries that contain information upon using
     */
    private static int[] buildTable(int generator) {
        int[] table = new int[TABLE_SIZE];
        for (int i = 0; i < TABLE_SIZE; i++) {
            table[i] = crc_bitwise(generator, new byte[]{(byte) i});
        }
        return table;
    }

    /**
     * Uses the bit by bit algorithm to calculate the CRC24 of the given table of octet
     *
     * @param generator : uses for the calculation of the CRC24, it is the denominator of the division
     * @param bytes     : table of octet
     * @return : the CRC24 of the table "bytes" given  using a bit by bit algorithm
     */
    private static int crc_bitwise(int generator, byte[] bytes) {
        int crc = 0;
        int[] table = {0, generator};

        for (byte b : bytes) {
            for (int i = 7; i >= 0; i--) {
                int bit = Bits.extractUInt(b, i, 1);
                int index = Bits.extractUInt(crc, LENGTH_CRC24 - 1, 1);
                crc = ((crc << 1) | bit) ^ table[index];
            }
        }

        for (int i = 0; i < LENGTH_CRC24; i++) {
            int index = Bits.extractUInt(crc, LENGTH_CRC24 - 1, 1);
            crc = ((crc << 1)) ^ table[index];
        }

        return Bits.extractUInt(crc, 0, LENGTH_CRC24);
    }


    /**
     * Calculate the CRC24 of the given table of octet using the table of 256 entries and the octet by octet algorithm
     *
     * @param bytes :  table of octet
     * @return :  the given CRC24 of the given table using octet by octet algorithm
     */
    public int crc(byte[] bytes) {
        int crc = 0;
        for (byte o : bytes) {
            int index = Bits.extractUInt(crc, LENGTH_CRC24 - Byte.SIZE, Byte.SIZE);
            crc = ((crc << Byte.SIZE) | Byte.toUnsignedInt(o)) ^ table[index];
        }

        for (int i = 0; i < (LENGTH_CRC24 / Byte.SIZE); i++) {
            int index = Bits.extractUInt(crc, LENGTH_CRC24 - Byte.SIZE, Byte.SIZE);
            crc = ((crc << Byte.SIZE)) ^ table[index];
        }

        return Bits.extractUInt(crc, 0, LENGTH_CRC24);
    }
}