package ch.epfl.javions.gui;

import ch.epfl.javions.adsb.AircraftStateAccumulator;
import ch.epfl.javions.aircraft.AircraftDatabase;
import ch.epfl.javions.aircraft.IcaoAddress;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;

import java.util.HashMap;
import java.util.Map;


public final class AircraftStateManager {

    private final Map<IcaoAddress, AircraftStateAccumulator<ObservableAircraftState>> associativeMap;
    private final ObservableSet<ObservableAircraftState> observableAircraftStates;
    private final ObservableSet<ObservableAircraftState> unmodifiableObservableSet;


    public AircraftStateManager(AircraftDatabase aircraftDataBase) {
        this.associativeMap = new HashMap<>();
        this.observableAircraftStates=  FXCollections.observableSet();
        this.unmodifiableObservableSet = FXCollections.unmodifiableObservableSet(observableAircraftStates);
    }

    public ObservableSet<ObservableAircraftState> states() {
        return unmodifiableObservableSet;
    }

}
