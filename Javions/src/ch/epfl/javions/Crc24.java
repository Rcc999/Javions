package ch.epfl.javions;

public final class Crc24 {

    public static int GENERATOR = 0xFFF40916;

    private final int generator;

    private static final int LENGTH_CRC24 = 24;

    public Crc24(int generator){
        this.generator = generator & 0xFFFFFF;
    }

    /*private static int crc_bitwise(int generator, byte [] bytes){
        int crc = 0;
        int[] table = {0, generator};
        for (int i = 0; i < bytes.length; i++) {
            for (int j = 0; j < Byte.SIZE ; j++) {
                int bitsExtracted = Bits.extractUInt(bytes[i], i, 1);
                crc = ((crc << 1) | bitsExtracted) ^ table[crc[LENGTH_CRC24 - 1]];

            }

        }
    }*/

    private static int crc_bitwise(int generator, byte[] bytes) {
        int crc = 0;
        int[] table = {0, generator & 0xFFFFFF};
        for (byte b : bytes) {
            for (int j = 0; j < Byte.SIZE; j++) {
                int bit = Bits.extractUInt(b, j, 1);
                crc = ((crc << 1) | bit) ^ table[crc >> (LENGTH_CRC24 - 1)];
            }
        }
        return crc & 0xFFFFFF;
    }

    public static int crc(byte[] bytes) {
        return crc_bitwise(GENERATOR, bytes);
    }

}
