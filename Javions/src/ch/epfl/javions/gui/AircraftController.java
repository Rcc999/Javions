package ch.epfl.javions.gui;

import ch.epfl.javions.Units;
import ch.epfl.javions.aircraft.AircraftData;
import ch.epfl.javions.aircraft.IcaoAddress;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.shape.SVGPath;

public final class AircraftController {

    private final MapParameters mapParameters;
    private final ObservableSet<ObservableAircraftState> observableAircraftStates;
    private final ObjectProperty<ObservableAircraftState> selectedAircraftState;
    private final AircraftData data;
    private final Pane pane;
    //private final Group annotedAircraftGroup;

    public AircraftController(MapParameters mapParameters,
                              ObservableSet<ObservableAircraftState> observableAircraftStates,
                              ObjectProperty<ObservableAircraftState> selectedAircraftState) {
        this.mapParameters = mapParameters;
        this.observableAircraftStates = observableAircraftStates;
        this.selectedAircraftState = selectedAircraftState;
        data = selectedAircraftState.get().getAircraftData();

        pane = new Pane();
        pane.getStyleClass().add("aircraft.css");

        //annotedAircraftGroup = new Group();
        //annotedAircraftGroup.setId(selectedAircraftState.get().getIcaoAddress().toString());

        //svgPath.rotateProperty().bind(selectedAircraftState.get().trackOrHeadingProperty());

        //svgPath.rotateProperty().bind(Bindings.createDoubleBinding(observableAircraftStates.getClass().));

        observableAircraftStates.addListener((SetChangeListener<ObservableAircraftState>)
                change -> { /* … corps de la lambda */});
    }

    public Pane pane() {
        return pane;
    }


    private Group annotatedAircraftGroup(ObservableAircraftState aircraftState) {
        Group aircraftGroup = new Group();
        aircraftGroup.setId(aircraftState.getIcaoAddress().toString());
        aircraftGroup.viewOrderProperty().bind(aircraftState.altitudeProperty().negate());
        return aircraftGroup;
    }

    /*private Group iconAndLabelGroup(ObservableAircraftState aircraftState) {
        Group iconAndLabelGroup = new Group();
        return iconAndLabelGroup;
    }*/

    private SVGPath icon(ObservableAircraftState aircraftState) {
        SVGPath svgPath = new SVGPath();
        svgPath.getStyleClass().add("aircraft");
        AircraftIcon aircraftIcon = AircraftIcon.iconFor(data.typeDesignator(), data.description(),
                aircraftState.getCategory(), data.wakeTurbulenceCategory());
        ObjectProperty<AircraftIcon> iconProperty = new SimpleObjectProperty<>(aircraftIcon);

        var iconCategory = aircraftState.categoryProperty().map(e -> aircraftIcon);
        svgPath.contentProperty().bind(iconCategory.map(AircraftIcon::svgPath));

        svgPath.rotateProperty().bind(Bindings.createDoubleBinding(() -> aircraftIcon.canRotate() ?  aircraftState.trackOrHeadingProperty().get() : 0.0,
                iconProperty, aircraftState.trackOrHeadingProperty()));


        return svgPath;
    }
}


