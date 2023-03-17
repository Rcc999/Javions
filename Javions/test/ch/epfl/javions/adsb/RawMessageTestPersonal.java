package ch.epfl.javions.adsb;

import ch.epfl.javions.ByteString;
import ch.epfl.javions.aircraft.IcaoAddress;

import java.io.IOException;
import java.util.HexFormat;

public class RawMessageTestPersonal {

    public static void main(String[] args) throws IOException {

        /**
        String f = "samples_20230304_1442.bin";

        try (InputStream s = new FileInputStream(f)) {
            AdsbDemodulator d = new AdsbDemodulator(s);
            RawMessage m;
            while ((m = d.nextMessage()) != null)
                System.out.println(m);
        }*/

        byte[] a = HexFormat.of().parseHex("8D4B17E5F8210002004BB8B1F1AC");;
        ByteString byteString = new ByteString(a);
        RawMessage m = new RawMessage(1, byteString);
        int b = 141; //10001101

        int c = RawMessage.size((byte) b);
        System.out.println(c); //good


        IcaoAddress icaoAddress = m.icaoAddress();
        System.out.println(icaoAddress); //good

        int e = m.typeCode();
        System.out.println(e);

        long f = 8096200;
        String f_Binary = Long.toBinaryString(f);
        System.out.println(f_Binary);

        int g = RawMessage.typeCode(0b10011011_10010110_10001110_10101010_01010101_10001110_10101010L);
        System.out.println(g == 0b10011);
    }
}
