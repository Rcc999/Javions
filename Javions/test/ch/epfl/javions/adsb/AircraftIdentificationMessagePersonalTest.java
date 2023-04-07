package ch.epfl.javions.adsb;

import ch.epfl.javions.aircraft.IcaoAddress;
import ch.epfl.javions.demodulation.AdsbDemodulator;
import ch.epfl.javions.demodulation.AdsbDemodulatorTestPersonal;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

    public class AircraftIdentificationMessagePersonalTest {
    public static void main(String[] args) throws IOException {

        String file = AdsbDemodulatorTestPersonal.class.getResource("/samples_20230304_1442.bin").getFile();
        try (InputStream stream = new FileInputStream(file)) {
            AdsbDemodulator demodulator = new AdsbDemodulator(stream);
            RawMessage message;
            int length = 0;
            while ((message = demodulator.nextMessage()) != null) {
                AircraftIdentificationMessage air = AircraftIdentificationMessage.of(message);
                if(air != null && air.category() >= 160 && air.category() < 166) {
                    System.out.println(air);
                    ++length;
                }

            }
            System.out.println(length);
        }


        /*byte[] a = {-115, 77, 34, 40, 35, 73, -108, -73, 40, 72, 32, 50, 59, -127};
        RawMessage rawMessage = RawMessage.of(1499146900, a);
        System.out.println(rawMessage);

        assert rawMessage != null;
        IcaoAddress icaoAddress = rawMessage.icaoAddress();
        //System.out.println(icaoAddress);

        AircraftIdentificationMessage air = AircraftIdentificationMessage.of(rawMessage);
        System.out.println(air);*/

    }
}
