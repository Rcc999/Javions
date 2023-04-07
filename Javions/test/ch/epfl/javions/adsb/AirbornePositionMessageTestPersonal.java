package ch.epfl.javions.adsb;

import ch.epfl.javions.demodulation.AdsbDemodulator;
import ch.epfl.javions.demodulation.AdsbDemodulatorTestPersonal;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class AirbornePositionMessageTestPersonal {
    public static void main(String[] args) throws IOException {
        String file = AdsbDemodulatorTestPersonal.class.getResource("/samples_20230304_1442.bin").getFile();
        try (InputStream stream = new FileInputStream(file)) {
            AdsbDemodulator demodulator = new AdsbDemodulator(stream);
            RawMessage message;
            int length = 0;
            while ((message = demodulator.nextMessage()) != null) {
                AirbornePositionMessage air = AirbornePositionMessage.of(message);
                int typeCode = message.typeCode();
                if(air != null && (( 9 <= typeCode && typeCode <= 18) || (20 <= typeCode && typeCode <= 22))){
                    System.out.println(air);
                    length++;
                }
            }
            System.out.println(length);
        }
    }
}
