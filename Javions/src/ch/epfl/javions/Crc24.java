package ch.epfl.javions;

public final class Crc24 {

    public static int GENERATOR = 0xFFF40916;

    private final int generator;
    private int[] table;

    private static final int LENGTH_CRC24 = 24;

    public Crc24(int generator){
        this.generator = generator & 0xFFFFFF;
    }

    private static int crc_bitwise(int generator, byte [] bytes){
        /*int crc = 0;
        int[] table = {0, generator};
        for (byte b : bytes) {
            for (int i = 0; i < Byte.SIZE ; i++) {
                int bit = Bits.extractUInt(b, Byte.SIZE - 1 - i, 1);
                int index = ((crc >> (LENGTH_CRC24 - 1)) ^ bit) & 0x1;
                crc = ((crc << 1) | bit) ^ table[index];
            }
        }

        return crc  & 0xFFFFFF;*/


        int crc = 0;
        int[] table = {0, generator};

        for (byte b : bytes) {
            for (int i = 0; i < Byte.SIZE ; i++) {
                int bit = Bits.extractUInt(b, Byte.SIZE - 1 - i, 1);
                int index = (crc >> (LENGTH_CRC24 - 1)) ^ bit;
                crc = ((crc << 1) | bit) ^ table[index] ;
            }
        }

        for (int i = 0; i < LENGTH_CRC24; i++) {
            int bit = 0;
            int index = (crc >> (LENGTH_CRC24 - 1)) ^ bit;
            crc = ((crc << 1) | bit) ^ table[index];
        }

        return crc & 0xFFFFFF;
    }


    public static int crc(byte[] bytes) {
        return crc_bitwise(GENERATOR, bytes);
    }
}
