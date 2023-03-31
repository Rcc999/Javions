package ch.epfl.javions.adsb;

import ch.epfl.javions.GeoPos;

public class AircraftStateAccumulator<T extends AircraftStateSetter> {

    private final T stateSetter;
    private AirbornePositionMessage previousPositionMessageEven;
    private AirbornePositionMessage previousPositionMessageOdd;

    public AircraftStateAccumulator(T stateSetter){
        if(stateSetter == null) throw new NullPointerException("State setter is null");
        this.stateSetter = stateSetter;
    }

    public T stateSetter(){
        return stateSetter;
    }

    public void update(Message message){
        stateSetter.setLastMessageTimeStampNs(message.timeStampNs());
        switch (message){
            
            case AircraftIdentificationMessage identification -> {
                stateSetter.setCallSign(identification.callSign());
                stateSetter.setCategory(identification.category());
            }
            case AirbornePositionMessage position -> {
                if(position.parity() == 1){previousPositionMessageOdd = position;}
                else{previousPositionMessageEven = position;}

                stateSetter.setAltitude(position.altitude());

                if(previousPositionMessageEven != null && previousPositionMessageOdd != null){
                    if(position.parity() == 1) {
                        if (timeStampNsDiff(position.timeStampNs(), previousPositionMessageEven.timeStampNs())) {
                            if(positionCalculator(position, previousPositionMessageEven) != null){
                                stateSetter.setPosition(positionCalculator(position, previousPositionMessageEven));
                            }
                        }
                    }
                    else{
                        if(timeStampNsDiff(position.timeStampNs(), previousPositionMessageOdd.timeStampNs())){
                            if(positionCalculator(position, previousPositionMessageOdd) != null){
                                stateSetter.setPosition(positionCalculator(position, previousPositionMessageOdd));
                            }
                        }
                    }
                }
            }
            case AirborneVelocityMessage velocity -> {
                stateSetter.setVelocity(velocity.speed());
                stateSetter.setTrackOrHeading(velocity.trackOrHeading());
            }
            default -> throw new IllegalStateException("Unexpected value: " + message);
        }

    }

    private boolean timeStampNsDiff(long current, long previous){
        return (current - previous) <= Math.pow(10, 10);
    }

    private GeoPos positionCalculator(AirbornePositionMessage positionMessage1, AirbornePositionMessage positionMessage2){
        double x0 = 0.0, y0 = 0.0, x1 = 0.0, y1 = 0.0;
        switch (positionMessage1.parity()){
            case 0 -> {
                x0 = positionMessage1.x();
                y0 = positionMessage1.y();
                x1 = positionMessage2.x();
                y1 = positionMessage2.y();
            }
            case 1 -> {
                x0 = positionMessage2.x();
                y0 = positionMessage2.y();
                x1 = positionMessage1.x();
                y1 = positionMessage1.y();
            }
        }
        return CprDecoder.decodePosition(x0, y0, x1, y1, positionMessage1.parity());
    }

}
