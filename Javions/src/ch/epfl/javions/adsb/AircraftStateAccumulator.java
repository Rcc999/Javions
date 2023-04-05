package ch.epfl.javions.adsb;

import ch.epfl.javions.GeoPos;

/**
 * Accumulating ADS-B messages from an aircraft to determine its status overtime
 *
 * @author Tuan Dang Nguyen (361089)
 * @author Rayane Charif Chefchouni (339839)
 */
public class AircraftStateAccumulator<T extends AircraftStateSetter> {

    private final T stateSetter;
    private AirbornePositionMessage previousPositionMessageEven;
    private AirbornePositionMessage previousPositionMessageOdd;

    /**
     * Returns an aircraft state accumulator associated with the given modifiable state
     *
     * @param stateSetter : modifiable state
     * @throws NullPointerException if state setter is null
     */
    public AircraftStateAccumulator(T stateSetter) {
        if (stateSetter == null) throw new NullPointerException("State setter is null");
        this.stateSetter = stateSetter;
    }

    /**
     * Get the modifiable state of the aircraft passed to its constructor
     *
     * @return state setter
     */
    public T stateSetter() {
        return stateSetter;
    }

    /**
     * Updates the mutable state based on the given message
     *
     * @param message of the aircraft
     * @throws IllegalStateException if the message isn't any of the 3 types below
     */
    public void update(Message message) {
        stateSetter.setLastMessageTimeStampNs(message.timeStampNs());
        switch (message) {

            case AircraftIdentificationMessage identification -> {
                stateSetter.setCallSign(identification.callSign());
                stateSetter.setCategory(identification.category());
            }

            case AirbornePositionMessage position -> {
                if (position.parity() == 1) previousPositionMessageOdd = position;
                else previousPositionMessageEven = position;

                stateSetter.setAltitude(position.altitude());

                if (previousPositionMessageEven != null && previousPositionMessageOdd != null) {
                    if (position.parity() == 1) checkAndSetPosition(position, previousPositionMessageEven);
                    else checkAndSetPosition(position, previousPositionMessageOdd);
                }
            }

            case AirborneVelocityMessage velocity -> {
                stateSetter.setVelocity(velocity.speed());
                stateSetter.setTrackOrHeading(velocity.trackOrHeading());
            }

            default -> throw new IllegalStateException("Unexpected value: " + message);
        }

    }

    /**
     * Check all conditions to set position
     *
     * @param current  position message
     * @param previous position message
     */
    private void checkAndSetPosition(AirbornePositionMessage current, AirbornePositionMessage previous) {
        if (timeStampNsDiff(current.timeStampNs(), previous.timeStampNs()))
            if (positionCalculator(current, previous) != null)
                stateSetter.setPosition(positionCalculator(current, previous));
    }

    /**
     * Check difference in time stamp between 2 messages
     *
     * @param current  message
     * @param previous message
     * @return true if the duration between current and previous given message is smaller than 10 seconds
     */
    private boolean timeStampNsDiff(long current, long previous) {
        return (current - previous) <= Math.pow(10, 10);
    }

    /**
     * Position calculator of 2 given messages
     *
     * @param positionMessage1 : current message
     * @param positionMessage2 : previous message
     * @return position from the current message
     */

    private GeoPos positionCalculator(AirbornePositionMessage positionMessage1, AirbornePositionMessage positionMessage2) {
        double x0 = 0.0, y0 = 0.0, x1 = 0.0, y1 = 0.0;
        switch (positionMessage1.parity()) {
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
