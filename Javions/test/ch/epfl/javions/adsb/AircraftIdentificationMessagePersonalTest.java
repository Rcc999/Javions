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
                System.out.println(air);
                ++length;
            }
            System.out.println(length);
        }

        /**
        byte[] a = {-115, 75, 23, -27, -8, 33, 0, 2, 0, 75, -72, -79, -15, -84};
        RawMessage rawMessage = RawMessage.of(8096200, a);
        System.out.println(rawMessage);

        IcaoAddress icaoAddress = rawMessage.icaoAddress();
        System.out.println(icaoAddress); */

    }
}
