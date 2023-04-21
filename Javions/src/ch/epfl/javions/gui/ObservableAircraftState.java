package ch.epfl.javions.gui;

import ch.epfl.javions.GeoPos;
import ch.epfl.javions.adsb.AircraftStateSetter;
import ch.epfl.javions.adsb.CallSign;
import ch.epfl.javions.aircraft.AircraftData;
import ch.epfl.javions.aircraft.IcaoAddress;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ReadOnlyLongProperty;
import javafx.beans.property.SimpleLongProperty;

public final class ObservableAircraftState implements AircraftStateSetter {

    private final IcaoAddress icaoAddress;
    private final AircraftData data;
    private final LongProperty timeStampNsProperty;

    public ObservableAircraftState(IcaoAddress icaoAddress, AircraftData data){
        this.icaoAddress = icaoAddress;
        this.data = data;
        this.timeStampNsProperty = new SimpleLongProperty(0L);
    }

    @Override
    public void setLastMessageTimeStampNs(long timeStampNs) {
        timeStampNsProperty.set(timeStampNs);
    }

    public ReadOnlyLongProperty timeStampNsProperty(){
        return timeStampNsProperty;
    }

    @Override
    public void setCategory(int category) {

    }

    @Override
    public void setCallSign(CallSign callSign) {

    }

    @Override
    public void setPosition(GeoPos position) {

    }

    @Override
    public void setAltitude(double altitude) {

    }

    @Override
    public void setVelocity(double velocity) {

    }

    @Override
    public void setTrackOrHeading(double trackOrHeading) {

    }
}
