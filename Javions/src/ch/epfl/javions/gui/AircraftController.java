package ch.epfl.javions.gui;

import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableSet;
import javafx.scene.layout.Pane;

public final class AircraftController {

    private final TileManager tileManager;
    private final ObservableSet<ObservableAircraftState> unmodifiableObservableAircraftStates;
    private final ObjectProperty<ObservableAircraftState> selectedAircraftState;

    public AircraftController(TileManager tileManager,
                              ObservableSet<ObservableAircraftState> unmodifiableObservableAircraftStates,
                              ObjectProperty<ObservableAircraftState> selectedAircraftState) {
        this.tileManager = tileManager;
        this.unmodifiableObservableAircraftStates = unmodifiableObservableAircraftStates;
        this.selectedAircraftState = selectedAircraftState;
    }

    public Pane pane() {
    }
}

