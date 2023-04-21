package ch.epfl.javions.gui;

import ch.epfl.javions.GeoPos;
import ch.epfl.javions.adsb.AircraftStateSetter;
import ch.epfl.javions.adsb.CallSign;
import ch.epfl.javions.aircraft.AircraftData;
import ch.epfl.javions.aircraft.IcaoAddress;
import javafx.beans.property.*;

public final class ObservableAircraftState implements AircraftStateSetter {

    private final IcaoAddress icaoAddress;
    private final AircraftData data;
    private final LongProperty timeStampNsProperty;
    private final IntegerProperty categoryProperty;
    private final ObjectProperty<CallSign> callSignProperty;
    private final DoubleProperty altitudeProperty, velocityProperty, trackOrHeadingProperty;
    private final ObjectProperty<GeoPos> positionProperty;
    private GeoPos position;

    public ObservableAircraftState(IcaoAddress icaoAddress, AircraftData data){
        this.icaoAddress = icaoAddress;
        this.data = data;
        this.timeStampNsProperty = new SimpleLongProperty(0L);
        this.categoryProperty = new SimpleIntegerProperty(0);
        this.callSignProperty = new SimpleObjectProperty<>(null);
        this.positionProperty = new SimpleObjectProperty<>(null);
        this.altitudeProperty = new SimpleDoubleProperty(0.0);
        this.velocityProperty = new SimpleDoubleProperty(0.0);
        this.trackOrHeadingProperty = new SimpleDoubleProperty(0.0);
        this.position = null;
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
        categoryProperty.set(category);
    }

    public ReadOnlyIntegerProperty categoryProperty(){
        return categoryProperty;
    }

    @Override
    public void setCallSign(CallSign callSign) {
        callSignProperty.set(callSign);
    }

    public ReadOnlyObjectProperty<CallSign> callSignProperty(){
        return callSignProperty;
    }

    @Override
    public void setPosition(GeoPos position) {
        this.position = position;
        positionProperty.set(position);
    }

    public ReadOnlyObjectProperty<GeoPos> positionProperty(){
        return positionProperty;
    }

    public GeoPos getPosition(){
        return position;
    }

    @Override
    public void setAltitude(double altitude) {
        altitudeProperty.set(altitude);
    }

    public ReadOnlyDoubleProperty altitudeProperty(){
        return altitudeProperty;
    }

    @Override
    public void setVelocity(double velocity) {
        velocityProperty.add(velocity);
    }

    public ReadOnlyDoubleProperty velocityProperty(){
        return velocityProperty;
    }

    @Override
    public void setTrackOrHeading(double trackOrHeading) {
        trackOrHeadingProperty.set(trackOrHeading);
    }

    public ReadOnlyDoubleProperty trackOrHeadingProperty(){
        return trackOrHeadingProperty;
    }
}
