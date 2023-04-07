package ch.epfl.javions.adsb;

import ch.epfl.javions.aircraft.IcaoAddress;
import ch.epfl.javions.demodulation.AdsbDemodulator;
import ch.epfl.javions.demodulation.AdsbDemodulatorTestPersonal;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class AirborneVelocityMessageTestPersonal {

    public static void main(String[] args) throws IOException {
        String f = AircraftStateAccumulatorTestPersonal.class.getResource("/samples_20230304_1442.bin").getFile();
        try (InputStream s = new FileInputStream(f)) {
            AdsbDemodulator d = new AdsbDemodulator(s);
            RawMessage m;
            int length = 0;
            while ((m = d.nextMessage()) != null) {
                AirborneVelocityMessage pm = AirborneVelocityMessage.of(m);
                if (pm != null && m.typeCode() == 19){
                    System.out.println(pm);
                    length++;
                }
            }
            System.out.println(length);
        }
    }
}
