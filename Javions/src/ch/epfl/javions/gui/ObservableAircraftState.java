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
    private long timeStampNs;
    private final LongProperty timeStampNsProperty;
    private int category;
    private final IntegerProperty categoryProperty;
    private CallSign callSign;
    private final ObjectProperty<CallSign> callSignProperty;
    private double altitude, velocity, trackOrHeading;
    private final DoubleProperty altitudeProperty, velocityProperty, trackOrHeadingProperty;
    private GeoPos position;
    private final ObjectProperty<GeoPos> positionProperty;


    public ObservableAircraftState(IcaoAddress icaoAddress, AircraftData data) {
        this.icaoAddress = icaoAddress;
        this.data = data;
        this.timeStampNsProperty = new SimpleLongProperty(0L);
        this.categoryProperty = new SimpleIntegerProperty(0);
        this.callSignProperty = new SimpleObjectProperty<>(null);
        this.positionProperty = new SimpleObjectProperty<>(null);
        this.altitudeProperty = new SimpleDoubleProperty(0.0);
        this.velocityProperty = new SimpleDoubleProperty(0.0);
        this.trackOrHeadingProperty = new SimpleDoubleProperty(0.0);
    }

    @Override
    public void setLastMessageTimeStampNs(long timeStampNs) {
        this.timeStampNs = timeStampNs;
        timeStampNsProperty.set(timeStampNs);
    }

    public ReadOnlyLongProperty timeStampNsProperty() {
        return timeStampNsProperty;
    }

    public long getTimeStampNs() {
        return timeStampNs;
    }

    @Override
    public void setCategory(int category) {
        this.category = category;
        categoryProperty.set(category);
    }

    public ReadOnlyIntegerProperty categoryProperty() {
        return categoryProperty;
    }

    public int getCategory() {
        return category;
    }

    @Override
    public void setCallSign(CallSign callSign) {
        this.callSign = callSign;
        callSignProperty.set(callSign);
    }

    public ReadOnlyObjectProperty<CallSign> callSignProperty() {
        return callSignProperty;
    }

    public CallSign getCallSign() {
        return callSign;
    }

    @Override
    public void setPosition(GeoPos position) {
        this.position = position;
        positionProperty.set(position);
    }

    public ReadOnlyObjectProperty<GeoPos> positionProperty() {
        return positionProperty;
    }

    public GeoPos getPosition() {
        return position;
    }

    @Override
    public void setAltitude(double altitude) {
        this.altitude = altitude;
        altitudeProperty.set(altitude);
    }

    public ReadOnlyDoubleProperty altitudeProperty() {
        return altitudeProperty;
    }

    public double getAltitude() {
        return altitude;
    }

    @Override
    public void setVelocity(double velocity) {
        this.velocity = velocity;
        velocityProperty.add(velocity);
    }

    public ReadOnlyDoubleProperty velocityProperty() {
        return velocityProperty;
    }

    public double getVelocity() {
        return velocity;
    }

    @Override
    public void setTrackOrHeading(double trackOrHeading) {
        this.trackOrHeading = trackOrHeading;
        trackOrHeadingProperty.set(trackOrHeading);
    }

    public ReadOnlyDoubleProperty trackOrHeadingProperty() {
        return trackOrHeadingProperty;
    }

    public double getTrackOrHeading() {
        return trackOrHeading;
    }
}
