package ch.epfl.javions.adsb;

import ch.epfl.javions.GeoPos;

public class AircraftStateAccumulator<T extends AircraftStateSetter> {

    private final T stateSetter;
    private AirbornePositionMessage positionMessage;

    public AircraftStateAccumulator(T stateSetter){
        if(stateSetter == null) throw new NullPointerException("State setter is null");
        this.stateSetter = stateSetter;
    }

    public T stateSetter(){
        return stateSetter;
    }

    public void update(Message message){
        switch (message){
            stateSetter.setLastMessageTimeStampNs(message.timeStampNs());
            case AircraftIdentificationMessage identification -> {
                stateSetter.setCallSign(identification.callSign());
                stateSetter.setCategory(identification.category());
            }

            case AirbornePositionMessage position -> {
                positionMessage = (AirbornePositionMessage) message;
                stateSetter.setAltitude(position.altitude());
                //Check timeStamps - 10 seconds
                //Check Parity
                stateSetter.setPosition();
            }

            case AirborneVelocityMessage velocity -> {
                stateSetter.setVelocity(velocity.speed());
                stateSetter.setTrackOrHeading(velocity.trackOrHeading());
            }
        }

    }

    private boolean

}
