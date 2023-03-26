package ch.epfl.javions.adsb;

public class MessageParser {

    private MessageParser(){}

    public static Message parse(RawMessage rawMessage){
        int typeCode = rawMessage.typeCode();
        if((9 <= typeCode && typeCode <= 18) || (20 <= typeCode && typeCode <= 22)){
            return AirbornePositionMessage.of(rawMessage);
        }
        if(typeCode == 1 || typeCode == 2 || typeCode == 3 || typeCode == 4){
            return AircraftIdentificationMessage.of(rawMessage);
        }
        if(typeCode == 19){
            return AirborneVelocityMessage.of(rawMessage);
        }
        return null;
    }
    
}
