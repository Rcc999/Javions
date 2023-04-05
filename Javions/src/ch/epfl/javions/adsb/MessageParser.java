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
        if ((9 <= typeCode && typeCode <= 18) || (20 <= typeCode && typeCode <= 22)) {
            return AirbornePositionMessage.of(rawMessage);
        }
        if (typeCode >= 1 && typeCode <= 4) {
            return AircraftIdentificationMessage.of(rawMessage);
        }
        if (typeCode == 19) {
            return AirborneVelocityMessage.of(rawMessage);
        }
        return null;
    }

}
