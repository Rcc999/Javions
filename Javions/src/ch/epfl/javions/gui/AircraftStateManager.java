package ch.epfl.javions.gui;

import ch.epfl.javions.adsb.AircraftStateAccumulator;
import ch.epfl.javions.adsb.Message;
import ch.epfl.javions.aircraft.AircraftDatabase;
import ch.epfl.javions.aircraft.IcaoAddress;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * A class that manages the aircraft states.
 *
 * @author Tuan Dang Nguyen (361089)
 * @author Rayane Charif Chefchouni (339839)
 */
public final class AircraftStateManager {

    //The maximum time in nanoseconds that an aircraft state can be kept in the map
    public static final long MINUTE_AGO_PURGE = 60 * 1_000_000_000L;

    private final Map<IcaoAddress, AircraftStateAccumulator<ObservableAircraftState>> associativeMap;
    private final ObservableSet<ObservableAircraftState> observableAircraftStates;
    private final ObservableSet<ObservableAircraftState> unmodifiableObservableAircraftStates;
    private final AircraftDatabase aircraftDatabase;
    private long currentTimeStampNs = 0;

    /**
     * Constructs a new AircraftStateManager
     *
     * @param aircraftDataBase : the aircraft database to use
     */
    public AircraftStateManager(AircraftDatabase aircraftDataBase) {
        this.aircraftDatabase = aircraftDataBase;
        this.associativeMap = new HashMap<>();
        this.observableAircraftStates = FXCollections.observableSet();
        this.unmodifiableObservableAircraftStates = FXCollections.unmodifiableObservableSet(observableAircraftStates);
    }


    /**
     * Purges the state of the aircrafts that have not been updated for more than a minute
     */
    public void purge() {
        Iterator<AircraftStateAccumulator<ObservableAircraftState>> iterator = associativeMap.values().iterator();
        while (iterator.hasNext()) {
            AircraftStateAccumulator<ObservableAircraftState> stateAccumulator = iterator.next();
            if (currentTimeStampNs - stateAccumulator.stateSetter().getLastMessageTimeStampNs() >= MINUTE_AGO_PURGE) {
                observableAircraftStates.remove(stateAccumulator.stateSetter());
                iterator.remove();
            }
        }
    }

    /**
     * Updates the state of the aircraft with the message
     *
     * @param message : the message to update the state with
     * @throws IOException if the message is not valid
     */
    public void updateWithMessage(Message message) throws IOException {
        IcaoAddress key = message.icaoAddress();
        if (associativeMap.get(key) == null) {
            ObservableAircraftState newState = new ObservableAircraftState(key, aircraftDatabase.get(key));
            associativeMap.put(key, new AircraftStateAccumulator<>(newState));
        }
        associativeMap.get(key).update(message);

        if (associativeMap.get(key).stateSetter().getPosition() != null)
            observableAircraftStates.add(associativeMap.get(key).stateSetter());

        currentTimeStampNs = message.timeStampNs();
    }

    /**
     * Returns the observable set of the aircraft states
     *
     * @return an unmodifiable observable set of the aircraft states
     */
    public ObservableSet<ObservableAircraftState> states() {
        return unmodifiableObservableAircraftStates;
    }

}
