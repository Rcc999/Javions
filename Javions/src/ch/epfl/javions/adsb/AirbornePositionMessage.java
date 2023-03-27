package ch.epfl.javions.adsb;

import ch.epfl.javions.Bits;
import ch.epfl.javions.Preconditions;
import ch.epfl.javions.Units;
import ch.epfl.javions.aircraft.IcaoAddress;

public record AirbornePositionMessage(long timeStampNs, IcaoAddress icaoAddress, double altitude, int parity,
                                      double x, double y) implements Message{


    public AirbornePositionMessage {
        if (icaoAddress == null) { throw new NullPointerException("IcaoAddress is null");}
        Preconditions.checkArgument(timeStampNs >= 0);
        Preconditions.checkArgument(parity == 0 || parity == 1);
        Preconditions.checkArgument(x >= 0 && x < 1);
        Preconditions.checkArgument(y >= 0 && y < 1);
    }
    @Override
    public long timeStampNs() {
        return timeStampNs;
    }

    @Override
    public IcaoAddress icaoAddress() {return icaoAddress;}

    public static AirbornePositionMessage of(RawMessage rawMessage){
        if(Double.isNaN(altitudeCalculator(rawMessage))){
            return null;
        }else{
            return new AirbornePositionMessage(rawMessage.timeStampNs(), rawMessage.icaoAddress(),
                    altitudeCalculator(rawMessage), determineParity(rawMessage), LAT_CPR(rawMessage), LON_CPR(rawMessage));
        }
    }

    private static double LAT_CPR(RawMessage rawMessage){
        return Math.scalb(Bits.extractUInt(rawMessage.payload(), 0, 17), -17);
    }

    private static double LON_CPR(RawMessage rawMessage){
        return Math.scalb(Bits.extractUInt(rawMessage.payload(), 17, 17), -17);
    }

    private static int determineParity(RawMessage rawMessage){
        return Bits.extractUInt(rawMessage.payload(), 34, 1);
    }

    private static double altitudeCalculator(RawMessage rawMessage){
        int alt = Bits.extractUInt(rawMessage.payload(), 36, 12);

        if(determineQ(rawMessage) == 1){
            return Units.convert((double) -1000 + removeBitForQ1(alt) * 25, Units.Length.FOOT, Units.Length.METER);
       } else {
            //Un-scramble
            alt = shouldBeReplaceWithBetterAlgo(alt);

            //Divide in 2 groups
            int group1 = Bits.extractUInt(alt, 0, 3);
            int group2 = Bits.extractUInt(alt, 3, 9);

            //From Gray --> Real Value
            group1 = GrayToValue(group1, 3);
            group2 = GrayToValue(group2, 9);

            if(group1 == 0 || group1 == 5 || group1 == 6){
                return Double.NaN;
            }
            if(group1 == 7){
                group1 = 5;
            }
            if(group2 % 2 == 1){
                group1 = 6 - group1;
            }

            return Units.convert(-1300 + group1 * 100 + group2 * 500, Units.Length.FOOT, Units.Length.METER);
        }
    }

    private static int determineQ(RawMessage rawMessage){
        return Bits.extractUInt(Bits.extractUInt(rawMessage.payload(), 36, 12) , 4, 1);
    }

    private static int removeBitForQ1(int num) {
        int mask = (1 << 4) - 1;
        return (char) ((num & ((~mask) << 1)) >>> 1) | (num & mask);
    }

    private static int shouldBeReplaceWithBetterAlgo(int b){
        int[] a = new int[12];
        for(int i = 0; i < 12; ++i){
            a[i] = Bits.extractUInt(b, i, 1);
        }

        int[] c = {a[4], a[2], a[0], a[10], a[8], a[6], a[5], a[3], a[1], a[11], a[9], a[7]};

        int d = 0;
        for (int j : c) {
            d = (d << 1) | j;
        }
        return d;
    }

    private static int GrayToValue(int gray, int nb_bits){
        int a = gray;
        for(int i = 1; i < nb_bits; ++i){
            a = a ^ (gray >> i);
        }
        return a;
    }

    /**
    public static void main(String[] args) {
        System.out.println(removeBitForQ1( 0b100010110011));
        System.out.println(GrayToValue( 0b000101011, 9));
        System.out.println(GrayToValue( 0b010, 3));
        System.out.println(shouldBeReplaceWithBetterAlgo(0b011001001010));

        byte[] a = {-115, 73, 82, -103, 88, -77, 2, -26, -31, 95, -93, 82, 48, 107};
        RawMessage rawMessage = RawMessage.of(75898000, a);
        AirbornePositionMessage air = AirbornePositionMessage.of(rawMessage);
        System.out.println(air);
    }*/

}


