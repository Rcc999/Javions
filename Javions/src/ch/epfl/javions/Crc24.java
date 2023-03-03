package ch.epfl.javions;

public final class Crc24 {

    public static int GENERATOR = 0xFFF40916;

    private final int generator;
    private int[] table;

    private static final int LENGTH_CRC24 = 24;

    public Crc24(int generator){
        this.generator = generator & 0xFFFFFF;
    }

    private static int crc_bitwise(int generator, byte[] bytes){

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
        // extract 8 bits plus fort de crc from 17 til 23 (not 32)
        int crc = 0;

        return crc_bitwise(GENERATOR, bytes);
    }

    private static int[] buildTable(int generator){
        int [] table = new int[256];
        for (int i = 0; i < 256; i++){
            table[i] = crc_bitwise(generator, new byte[] {(byte) i});
        }
        return table;
    }

    public static void main(String[] args){

        byte a = (byte) 0b100010101010100101001100001110;

        int b = (a & 0xFFFFFF);

        int d = Bits.extractUInt(a, 0, 24);

        String c = Integer.toBinaryString(b); //111111111111111110001110

        String e = Integer.toBinaryString(d); //111111111111111110001110

        System.out.println(c);

        System.out.println(e);


    }

}