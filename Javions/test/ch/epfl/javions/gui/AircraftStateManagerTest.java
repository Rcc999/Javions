package ch.epfl.javions.gui;

import ch.epfl.javions.Units;
import ch.epfl.javions.adsb.Message;
import ch.epfl.javions.adsb.MessageParser;
import ch.epfl.javions.adsb.RawMessage;
import ch.epfl.javions.aircraft.AircraftDatabase;

import java.io.*;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Scanner;

import static java.nio.charset.StandardCharsets.UTF_8;


/**
 * @author Ozair Faizan (361118)
 */
class AircraftStateManagerTest {
    static AircraftStateManager aircraftStateManager = new AircraftStateManager(getDatabase());

    private static AircraftDatabase getDatabase() {
        // Try to get the database from the resources
        var aircraftResourceUrl = AircraftStateManagerTest.class.getResource("/aircraft.zip");
        if (aircraftResourceUrl != null) {
            return new AircraftDatabase(URLDecoder.decode(aircraftResourceUrl.getFile(), UTF_8));
        }

        // Try to get the database from the JAVIONS_AIRCRAFT_DATABASE environment variable
        // (only meant to simplify testing of several projects with a single database)
        var aircraftFileName = System.getenv("JAVIONS_AIRCRAFT_DATABASE");
        if (aircraftFileName != null) {
            return new AircraftDatabase(aircraftFileName);
        }

        throw new Error("Could not find aircraft database");
    }

    private static void formattedPrint() {
        String CSI = "\u001B[";
        String CLEAR_SCREEN = CSI + "2J";
        System.out.print(CLEAR_SCREEN);
        String format = "%8s %8s %8s %35s %20s %20s %20s %20s\n";
        System.out.printf(format + "\n", "ICAO", "type",
                "reg", "Model", "Longitude", "Latitude", "Altitude", "Velocity");
        var sortedAircraftStates = new ArrayList<>(aircraftStateManager.states());
        sortedAircraftStates.sort(new AddressComparator());
        long currentTime = -1;
        for (ObservableAircraftState aircraftState : sortedAircraftStates) {

            var call = aircraftState.getCallSign();
            var aircraftData = aircraftState.getAircraftData();
            String callString = call == null ? "" : call.string();
            String registration = aircraftData == null ? "" : aircraftData.registration().string();
            String model = aircraftData == null ? "" : aircraftData.model();
            String velocity = Units.convertTo(aircraftState.getVelocity(), Units.Speed.KILOMETER_PER_HOUR) == 0
                    ? "Nan"
                    : "" + (int) Units.convertTo(aircraftState.getVelocity(), Units.Speed.KILOMETER_PER_HOUR);
            System.out.printf(format,
                    aircraftState.getIcaoAddress().string(),
                    callString,
                    registration,
                    model,
                    Units.convertTo(aircraftState.getPosition().longitude(), Units.Angle.DEGREE),
                    Units.convertTo(aircraftState.getPosition().latitude(), Units.Angle.DEGREE),
                    (int) aircraftState.getAltitude(),
                    velocity);
            currentTime = Math.max(currentTime, aircraftState.getLastMessageTimeStampNs());

        }
        System.out.println("Current time: " + currentTime / 1_000_000_000.0 + "s");

    }

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        byte[] bytes = new byte[RawMessage.LENGTH];
        try (DataInputStream s = new DataInputStream(
                new BufferedInputStream(
                        new FileInputStream(AircraftStateManagerTest.class.getResource(
                                "/messages_20230318_0915.bin").getFile())))) {
            long startTimeNs = System.nanoTime();
            int count = 0;
            while (true) {
                long timeStampNs = s.readLong();
                int bytesRead = s.readNBytes(bytes, 0, bytes.length);
                assert bytesRead == RawMessage.LENGTH;

                while (System.nanoTime() - startTimeNs < 0.0125 * timeStampNs) {
                    Thread.sleep(10);
                }
                RawMessage rawMessage = RawMessage.of(timeStampNs, bytes);
                if(rawMessage != null){
                    Message message = MessageParser.parse(rawMessage);
                    aircraftStateManager.updateWithMessage(message);
                    aircraftStateManager.purge();

                }
                formattedPrint();

                Thread.sleep(10);
                System.out.println(++count / 308000.0);
//                scanner.nextLine();
            }
        }
        catch (EOFException e) { /* nothing to do */ }
        catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static class AddressComparator implements Comparator<ObservableAircraftState> {
        @Override
        public int compare(ObservableAircraftState o1, ObservableAircraftState o2) {
            return o1.getIcaoAddress().string().compareTo(o2.getIcaoAddress().string());
        }
    }
}