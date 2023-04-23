package ch.epfl.javions.gui;

import ch.epfl.javions.adsb.AircraftStateAccumulator;
import ch.epfl.javions.adsb.AircraftStateSetter;
import ch.epfl.javions.adsb.Message;
import ch.epfl.javions.aircraft.AircraftDatabase;
import ch.epfl.javions.aircraft.IcaoAddress;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public final class AircraftStateManager {

    private final Map<IcaoAddress, AircraftStateAccumulator<ObservableAircraftState>> associativeMap;
    private final ObservableSet<ObservableAircraftState> observableAircraftStates;
    private final ObservableSet<ObservableAircraftState> unmodifiableObservableSet;
    private final AircraftDatabase aircraftDatabase;
    private  long timeStamps = 0;


    public AircraftStateManager(AircraftDatabase aircraftDataBase) {
        this.aircraftDatabase = aircraftDataBase;
        this.associativeMap = new HashMap<>();
        this.observableAircraftStates=  FXCollections.observableSet();
        this.unmodifiableObservableSet = FXCollections.unmodifiableObservableSet(observableAircraftStates);
    }

    public void purge(){
       long oneMinuteAgo = timeStamps - (60 * 1000000000L);

        for (IcaoAddress icaoAddress : associativeMap.keySet()) {
            AircraftStateAccumulator<ObservableAircraftState> states = associativeMap.get(icaoAddress);
            ObservableAircraftState stateToRemove = states.stateSetter();

            if (timeStamps - stateToRemove.getLastMessageTimeStampNs() <= oneMinuteAgo) {
                observableAircraftStates.remove(stateToRemove);
                associativeMap.remove(icaoAddress);
            }
        }
    }

    public void updateWithMessage(Message message) throws IOException {
        IcaoAddress key = message.icaoAddress();
        if(associativeMap.get(key) != null) {
            associativeMap.get(key).update(message);
        } else {
            ObservableAircraftState state = new ObservableAircraftState(key, aircraftDatabase.get(key));
            Objects.requireNonNull(associativeMap.put(key, new AircraftStateAccumulator<>(state))).update(message);
        }

        if(associativeMap.get(key).stateSetter().getPosition() != null)
            observableAircraftStates.add(associativeMap.get(key).stateSetter());

        timeStamps = message.timeStampNs();
    }

    public ObservableSet<ObservableAircraftState> states() {
        return unmodifiableObservableSet;
    }

}
