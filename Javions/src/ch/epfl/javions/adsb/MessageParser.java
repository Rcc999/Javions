package ch.epfl.javions.adsb;

/**
 * Transforming raw messages into different type of airborne message
 *
 * @author Tuan Dang Nguyen (361089)
 * @author Rayane Charif Chefchouni (339839)
 */
public class MessageParser {

    /**
     * Private constructor - non instantiable
     */
    private MessageParser() {}

    /**
     * Parsing from raw message of the aircraft to obtain different type of airborne message
     *
     * @param rawMessage of the aircraft
     * @return the type of airborne message if the type code correspond to a type of message, else return null
     */
    public static Message parse(RawMessage rawMessage) {
        int typeCode = rawMessage.typeCode();
        return switch (typeCode) {
            case 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 20, 21, 22 -> AirbornePositionMessage.of(rawMessage);
            case 1, 2, 3, 4 -> AircraftIdentificationMessage.of(rawMessage);
            case 19 -> AirborneVelocityMessage.of(rawMessage);
            default -> null;
        };
    }
}
