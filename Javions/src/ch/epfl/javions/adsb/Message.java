package ch.epfl.javions.adsb;

import ch.epfl.javions.aircraft.IcaoAddress;
/**
 * Interface for the message of an aircraft
 *
 * @author Tuan Dang Nguyen (361089)
 * @author Rayane Charif Chefchouni (339839)
 */
public interface Message {

    /**
     *
     * @return the time stamp of the message in nanoseconds
     */
    long timeStampNs();

    /**
     *
     * @return the OAIC adress of the sender of the message
     */
    IcaoAddress icaoAddress();

}
