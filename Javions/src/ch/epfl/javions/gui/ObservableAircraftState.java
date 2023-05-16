package ch.epfl.javions.gui;

import ch.epfl.javions.GeoPos;
import ch.epfl.javions.adsb.AircraftStateSetter;
import ch.epfl.javions.adsb.CallSign;
import ch.epfl.javions.aircraft.AircraftData;
import ch.epfl.javions.aircraft.IcaoAddress;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Observable Aircraft States
 *
 * @author Tuan Dang Nguyen (361089)
 * @author Rayane Charif Chefchouni (339839)
 */
public final class ObservableAircraftState implements AircraftStateSetter {

    private final IcaoAddress icaoAddress;
    private final AircraftData data;
    private final LongProperty timeStampNsProperty;
    private final IntegerProperty categoryProperty;
    private final ObjectProperty<CallSign> callSignProperty;
    private final DoubleProperty altitudeProperty, velocityProperty, trackOrHeadingProperty;
    private final ObjectProperty<GeoPos> positionProperty;
    private final ObservableList<AirbornePos> listFirst = FXCollections.observableArrayList();
    private final ObservableList<AirbornePos> listSecond = FXCollections.unmodifiableObservableList(listFirst);
    private long previousTimeStamps = -1;


    public record AirbornePos(GeoPos pos, double altitude) {
    }

    /**
     * Construct the observable state of the aircraft that contains
     * category, call sign, position, altitude, velocity and track or heading
     *
     * @param icaoAddress of the aircraft
     * @param data        provides fixed characteristics of this aircraft
     */
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

    /**
     * Get the database of the aircraft
     *
     * @return database of the aircraft
     */
    public AircraftData getAircraftData() {
        return data;
    }

    /**
     * Get ICAO address of the aircraft
     *
     * @return ICAO address of the aircraft
     */
    public IcaoAddress getIcaoAddress() {
        return icaoAddress;
    }

    /**
     * Set the latest time stamp of the message
     *
     * @param timeStampNs in nanoseconds
     */
    @Override
    public void setLastMessageTimeStampNs(long timeStampNs) {
        timeStampNsProperty.set(timeStampNs);
    }

    public ReadOnlyLongProperty timeStampNsProperty() {
        return timeStampNsProperty;
    }

    /**
     * Get the time stamp of the latest message
     *
     * @return the time stamp of the latest message
     */
    public long getLastMessageTimeStampNs() {
        return timeStampNsProperty.get();
    }

    /**
     * Set the property category of the aircraft
     *
     * @param category of the aircraft
     */
    @Override
    public void setCategory(int category) {
        categoryProperty.set(category);
    }

    /**
     * Get the property of category
     *
     * @return read-only observable property of category
     */
    public ReadOnlyIntegerProperty categoryProperty() {
        return categoryProperty;
    }

    /**
     * Get the value of the category of the aircraft
     *
     * @return the value of the category of the aircraft
     */
    public int getCategory() {
        return categoryProperty.get();
    }

    /**
     * Set value call sign of the aircraft
     *
     * @param callSign of the aircraft
     */
    @Override
    public void setCallSign(CallSign callSign) {
        callSignProperty.set(callSign);
    }

    /**
     * Get the property of call sign
     *
     * @return read-only property of call sign
     */
    public ReadOnlyObjectProperty<CallSign> callSignProperty() {
        return callSignProperty;
    }

    /**
     * Get the value of call sign of the aircraft
     *
     * @return the value of call sign of the aircraft
     */
    public CallSign getCallSign() {
        return callSignProperty.get();
    }

    /**
     * Set the longitude and latitude of an aircraft
     *
     * @param position: longitude and latitude of the aircraft
     */
    @Override
    public void setPosition(GeoPos position) {
        positionProperty.set(position);
        calculateTrajectory(previousTimeStamps, true);
        previousTimeStamps = getLastMessageTimeStampNs();
    }

    /**
     * Get the property of the position
     *
     * @return read-only property of the position
     */
    public ReadOnlyObjectProperty<GeoPos> positionProperty() {
        return positionProperty;
    }

    /**
     * Get the value of the position of the aircraft
     *
     * @return the value of the position of the aircraft
     */
    public GeoPos getPosition() {
        return positionProperty.get();
    }

    /**
     * Set the altitude of the aircraft
     *
     * @param altitude of the aircraft
     */
    @Override
    public void setAltitude(double altitude) {
        altitudeProperty.set(altitude);
        calculateTrajectory(previousTimeStamps, false);
        previousTimeStamps = getLastMessageTimeStampNs();
    }

    /**
     * Get the property of the altitude
     *
     * @return read-only property of the altitude
     */
    public ReadOnlyDoubleProperty altitudeProperty() {
        return altitudeProperty;
    }

    /**
     * Get the value of the altitude of the aircraft
     *
     * @return the value of the altitude of the aircraft
     */
    public double getAltitude() {
        return altitudeProperty.get();
    }

    /**
     * Set the velocity of the aircraft
     *
     * @param velocity of the aircraft
     */
    @Override
    public void setVelocity(double velocity) {
        velocityProperty.set(velocity);
    }

    /**
     * Get the property of velocity of the aircraft
     *
     * @return read-only property of velocity of the aircraft
     */
    public ReadOnlyDoubleProperty velocityProperty() {
        return velocityProperty;
    }

    /**
     * Get the value of velocity of the aircraft
     *
     * @return value of velocity of the aircraft
     */
    public double getVelocity() {
        return velocityProperty.get();
    }

    /**
     * Set track or heading of the aircraft
     *
     * @param trackOrHeading of the aircraft to be set
     */
    @Override
    public void setTrackOrHeading(double trackOrHeading) {
        trackOrHeadingProperty.set(trackOrHeading);
    }

    /**
     * Get the property of track or heading of the aircraft
     *
     * @return read-only track or heading of the aircraft
     */
    public ReadOnlyDoubleProperty trackOrHeadingProperty() {
        return trackOrHeadingProperty;
    }

    /**
     * Get the value of track or heading of the aircraft
     *
     * @return the value of track or heading of the aircraft
     */
    public double getTrackOrHeading() {
        return trackOrHeadingProperty.get();
    }

    /**
     * Get the list that contains the pair position and altitude
     *
     * @return an observable list of the pair position and altitude
     */
    public ObservableList<AirbornePos> trajectory() {
        return listSecond;
    }

    /**
     * Calculate the trajectory and put it in the trajectory list that contains the position of the aircraft
     *
     * @param latestTimeStamps of the aircraft whose trajectory is added the latest
     * @param positionAdded    is true if a position is set, false otherwise
     */
    private void calculateTrajectory(long latestTimeStamps, boolean positionAdded) {
        if (getPosition() != null) {
            if (positionAdded && getAltitude() != 0) listFirst.add(new AirbornePos(getPosition(), getAltitude()));
            if (listFirst.isEmpty())
                listFirst.add(new AirbornePos(getPosition(), getAltitude()));

            else if (getLastMessageTimeStampNs() == latestTimeStamps)
                listFirst.set(listFirst.size() - 1, new AirbornePos(getPosition(), getAltitude()));
        }
    }

}
