package ch.epfl.javions.adsb;

import ch.epfl.javions.GeoPos;
/**
 * Interface for the setter of the state of an aircraft
 *
 * @author Tuan Dang Nguyen (361089)
 * @author Rayane Charif Chefchouni (339839)
 */
public interface AircraftStateSetter {

    /**
     * Change the timestamp of the last message received by the aircraft at the value given
     *
     * @param timeStampNs : value in nanoseconds
     */
    void setLastMessageTimeStampNs(long timeStampNs);

    /**
     * change the category of the aircraft at the given value
     *
     * @param category :  value to give
     */
    void setCategory(int category);

    /**
     * Change the indicatif of the aircraft to the given value bellow
     *
     * @param callSign
     */
    void setCallSign(CallSign callSign);

    /**
     * Change the position of the aircraft at the value given bellow
     *
     * @param position
     */
    void setPosition(GeoPos position);

    /**
     * Change the altitude of the aircraft at the value given bellow
     *
     * @param altitude
     */
    void setAltitude(double altitude);

    /**
     * Change the velocity of the aircraft at the value given bellow
     *
     * @param velocity
     */
    void setVelocity(double velocity);

    /**
     * Change the direction of the aircraft at the value given bellow
     *
     * @param trackOrHeading
     */
    void setTrackOrHeading(double trackOrHeading);
}
