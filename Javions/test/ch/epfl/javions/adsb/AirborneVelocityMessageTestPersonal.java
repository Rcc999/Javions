package ch.epfl.javions.adsb;

import ch.epfl.javions.aircraft.IcaoAddress;
import ch.epfl.javions.demodulation.AdsbDemodulator;
import ch.epfl.javions.demodulation.AdsbDemodulatorTestPersonal;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class AirborneVelocityMessageTestPersonal {

    /*public static void main(String[] args) throws IOException {
        String file = AdsbDemodulatorTestPersonal.class.getResource("/samples_20230304_1442.bin").getFile();
        //IcaoAddress expectedAddress = new IcaoAddress("4D2228");
        try (InputStream stream = new FileInputStream(file)) {
            AdsbDemodulator demodulator = new AdsbDemodulator(stream);
            RawMessage message;
            while ((message = demodulator.nextMessage()) != null) {
                AirborneVelocityMessage airborneVelocityMessage = AirborneVelocityMessage.of(message);
                if(message.typeCode() == 19) {
                    if( AirborneVelocityMessage.of(message) == null) continue;
                    System.out.println(airborneVelocityMessage);
                }

            }
        }
    }*/

    public static void main(String[] args) throws IOException {
        String file = AdsbDemodulatorTestPersonal.class.getResource("/samples_20230304_1442.bin").getFile();
        IcaoAddress expectedAddress = new IcaoAddress("4D2228");
        try (InputStream s = new FileInputStream(file)) {
            AdsbDemodulator d = new AdsbDemodulator(s);
            RawMessage m;
            AircraftStateAccumulator<AircraftState> a =
                    new AircraftStateAccumulator<>(new AircraftState());
            while ((m = d.nextMessage()) != null) {
                if (!m.icaoAddress().equals(expectedAddress)) continue;

                Message pm = MessageParser.parse(m);
                if (pm != null) a.update(pm);
            }
        }
    }
}
