package ch.epfl.javions.adsb;

import ch.epfl.javions.aircraft.IcaoAddress;

public interface Message {

    /**
     *
     * @return the timestamp of the message in nanoseconds
     */
    long timeStampNs();

    /**
     *
     * @return the OAIC adress of the sender of the message
     */
    IcaoAddress icaoAddress();

}
