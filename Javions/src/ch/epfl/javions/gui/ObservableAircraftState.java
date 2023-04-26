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


    public record AirbornePos(GeoPos pos, double altitude){}

    /**
     * Construct the observable state of the aircraft
     *
     * @param icaoAddress of the aircraft
     * @param data provides fixed characteristics of this aircraft
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
     * @return data of the aircraft
     */
    public AircraftData getAircraftData() {
        return data;
    }

    /**
     * @return ICAO address of the aircraft
     */
    public IcaoAddress getIcaoAddress(){
        return icaoAddress;
    }

    @Override
    public void setLastMessageTimeStampNs(long timeStampNs) {
        timeStampNsProperty.set(timeStampNs);
    }

    public ReadOnlyLongProperty timeStampNsProperty() {
        return timeStampNsProperty;
    }

    public long getLastMessageTimeStampNs() {
        return timeStampNsProperty.get();
    }

    @Override
    public void setCategory(int category) {
        categoryProperty.set(category);
    }

    public ReadOnlyIntegerProperty categoryProperty() {
        return categoryProperty;
    }

    public int getCategory() {
        return categoryProperty.get();
    }

    @Override
    public void setCallSign(CallSign callSign) {
        callSignProperty.set(callSign);
    }

    public ReadOnlyObjectProperty<CallSign> callSignProperty() {
        return callSignProperty;
    }

    public CallSign getCallSign() {
        return callSignProperty.get();
    }

    @Override
    public void setPosition(GeoPos position) {
        positionProperty.set(position);
        calculateTrajectory(previousTimeStamps);
        previousTimeStamps = getLastMessageTimeStampNs();
    }

    public ReadOnlyObjectProperty<GeoPos> positionProperty() {
        return positionProperty;
    }

    public GeoPos getPosition() {
        return positionProperty.get();
    }

    @Override
    public void setAltitude(double altitude) {
        altitudeProperty.set(altitude);
        calculateTrajectory(previousTimeStamps);
        previousTimeStamps = getLastMessageTimeStampNs();
    }

    public ReadOnlyDoubleProperty altitudeProperty() {
        return altitudeProperty;
    }

    public double getAltitude() {
        return altitudeProperty.get();
    }

    @Override
    public void setVelocity(double velocity) {
        velocityProperty.set(velocity);
    }

    public ReadOnlyDoubleProperty velocityProperty() {
        return velocityProperty;
    }

    public double getVelocity() {
        return velocityProperty.get();
    }

    @Override
    public void setTrackOrHeading(double trackOrHeading) {
        trackOrHeadingProperty.set(trackOrHeading);
    }

    public ReadOnlyDoubleProperty trackOrHeadingProperty() {
        return trackOrHeadingProperty;
    }

    public double getTrackOrHeading() {
        return trackOrHeadingProperty.get();
    }

    public ObservableList<AirbornePos> trajectory(){
        return listSecond;
    }

    /**
     * Calculate the trajectory and put it in the trajectory list that contains the position of the aircraft
     *
     * @param latestTimeStamps of the aircraft whose trajectory is added the latest
     */
    private void calculateTrajectory(long latestTimeStamps){
        if(getPosition() != null){
            if(listFirst.isEmpty() || !getPosition().equals(listFirst.get(listFirst.size()-1).pos()))
                listFirst.add(new AirbornePos(getPosition(), getAltitude()));

            else if(getLastMessageTimeStampNs() == latestTimeStamps)
                    listFirst.set(listFirst.size() - 1, new AirbornePos(getPosition(), getAltitude()));
        }
    }

}
