package ch.epfl.javions.adsb;

import ch.epfl.javions.Bits;


    public class AirbornePositionMessagePersonalTest {

        private static int parity = 1;
        private static int altitude = 0b100010110011;


        public static int Q1(){
            /*int alt = Bits.extractUInt( altitude, 0,12);
            int q1 = Bits.extractUInt( altitude , 4, 1);
            if(q1 == parity){
              int altWithoutQ1 = alt & 0b1111101111;
              String binary = Integer.toBinaryString(altWithoutQ1);
                System.out.println(binary);
             return altWithoutQ1;
            }*/
            return removeBit(altitude, 4);
        }

        public static int removeBit(int num, int index) {
            int mask = (1 << index) - 1;
            return (char)((num & ((~mask) << 1)) >>> 1) | (num & mask);
        }


        public static void main(String[] args) {
            System.out.println(AirbornePositionMessagePersonalTest.Q1());
        }
    }
