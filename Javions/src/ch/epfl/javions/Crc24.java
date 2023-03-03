package ch.epfl.javions;

public final class Crc24 {

    public static int GENERATOR = 0xFFF409;

    private int[] table;

    private static final int LENGTH_CRC24 = 24;


    public Crc24(int generator){
        this.table = buildTable(generator);
    }

    private static int[] buildTable(int generator){
        int [] table = new int[256];
        for (int i = 0; i < 256; i++){
            table[i] = crc_bitwise(generator, new byte[] {(byte) i});
        }
        return table;
    }


    private static int crc_bitwise(int generator, byte[] bytes){
        int crc = 0;
        int[] table = {0, generator};

        for (byte b : bytes) {
            for (int i = 7; i >=0  ; i--) {
                int bit = Bits.extractUInt(b, i, 1);
                int index = Bits.extractUInt(crc, LENGTH_CRC24 - 1, 1);
                crc = ((crc << 1) | bit) ^ table[index] ;
            }
        }

        for (int i = 0; i < LENGTH_CRC24; i++) {
            int index = Bits.extractUInt(crc, LENGTH_CRC24 - 1, 1);
            crc = ((crc << 1)) ^ table[index];
        }

        return Bits.extractUInt(crc, 0, LENGTH_CRC24);
    }



    public  int crc(byte[] bytes) {
        int crc = 0;
        for (byte o : bytes) {
                int index = Bits.extractUInt(crc, LENGTH_CRC24 - Byte.SIZE, Byte.SIZE);
                crc = ((crc << Byte.SIZE) | Byte.toUnsignedInt(o)) ^ table[index] ;
        }

        for (int i = 0; i < (LENGTH_CRC24 / Byte.SIZE); i++) {
            int index = Bits.extractUInt(crc, LENGTH_CRC24 - Byte.SIZE, Byte.SIZE);
            crc = ((crc << Byte.SIZE)) ^ table[index];
        }

        return Bits.extractUInt(crc, 0, LENGTH_CRC24);
    }
}