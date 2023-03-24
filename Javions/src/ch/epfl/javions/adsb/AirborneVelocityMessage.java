package ch.epfl.javions.adsb;

import ch.epfl.javions.Preconditions;
import ch.epfl.javions.aircraft.IcaoAddress;

public record AirborneVelocityMessage(long timeStampNs, IcaoAddress icaoAddress, double speed, double trackOrHeading) implements Message {

    public AirborneVelocityMessage {
        Preconditions.checkArgument(timeStampNs >= 0 && speed >= 0 && trackOrHeading >= 0);
        if(icaoAddress ==  null){ throw new NullPointerException();}
    }

    public static AirborneVelocityMessage of(RawMessage rawMessage){
        return null;
    }



    @Override
    public long timeStampNs() {
        return timeStampNs;
    }

    @Override
    public IcaoAddress icaoAddress() {
        return icaoAddress;
    }
}
