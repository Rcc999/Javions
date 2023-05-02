package ch.epfl.javions.gui;

import ch.epfl.javions.Units;
import ch.epfl.javions.WebMercator;
import ch.epfl.javions.aircraft.*;
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

import java.util.Iterator;

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
        //data = observableAircraftStates.iterator().next().getAircraftData() != null ? observableAircraftStates.iterator().next().getAircraftData() : null;
        data = selectedAircraftState.get() != null
                ? selectedAircraftState.get().getAircraftData()
                : null;

        pane = new Pane();
        pane.getStyleClass().add("aircraft.css");
        pane.setPickOnBounds(false);

        observableAircraftStates.addListener((SetChangeListener<ObservableAircraftState>)
                change -> {
                    if (change.wasAdded()) {
                        pane.getChildren().add(annotatedAircraftGroup(change.getElementAdded()));
                    } else if (change.wasRemoved()) {
                        pane.getChildren().remove(annotatedAircraftGroup(change.getElementRemoved()));
                    }
                });
    }

    public Pane pane() {
        return pane;
    }


    private Group annotatedAircraftGroup(ObservableAircraftState aircraftState) {
        Group aircraftGroup = new Group();
        aircraftGroup.setId(aircraftState.getIcaoAddress().toString());
        aircraftGroup.getChildren().add(iconAndLabelGroup(aircraftState));
        aircraftGroup.viewOrderProperty().bind(aircraftState.altitudeProperty().negate());
        return aircraftGroup;
    }

    private Group iconAndLabelGroup(ObservableAircraftState aircraftState) {
        Group iconAndLabelGroup = new Group();
        iconAndLabelGroup.getChildren().add(icon(aircraftState));

        iconAndLabelGroup.layoutXProperty().bind(Bindings.createDoubleBinding(() -> {
            double x = WebMercator.x(mapParameters.getZoomLevel(), aircraftState.positionProperty().get().longitude());
            return x - mapParameters.getMinX();
        })); //aircraftState.positionProperty(), mapParameters.zoomLevelProperty(), mapParameters.minXProperty())); // not so sure

        iconAndLabelGroup.layoutYProperty().bind(Bindings.createDoubleBinding(() -> {
            double y = WebMercator.y(mapParameters.getZoomLevel(), aircraftState.positionProperty().get().latitude());
            return y - mapParameters.getMinY();
        })); //aircraftState.positionProperty(), mapParameters.zoomLevelProperty(), mapParameters.minYProperty())); // not so sure
        return iconAndLabelGroup;
    }


    private SVGPath icon(ObservableAircraftState aircraftState) {
        SVGPath svgPath = new SVGPath();
        svgPath.getStyleClass().add("aircraft");
        //Haven't gérer le case where data is null

        AircraftTypeDesignator typeDesignator = data == null
                ? new AircraftTypeDesignator("") : data.typeDesignator();
        AircraftDescription description = data == null
                ? new AircraftDescription("") : data.description();

        AircraftIcon aircraftIcon = AircraftIcon.iconFor(typeDesignator, description,
                aircraftState.getCategory(), data.wakeTurbulenceCategory());
        ObjectProperty<AircraftIcon> iconProperty = new SimpleObjectProperty<>(aircraftIcon);

        var iconCategory = aircraftState.categoryProperty().map(e -> aircraftIcon);
        svgPath.contentProperty().bind(iconCategory.map(AircraftIcon::svgPath));

        svgPath.rotateProperty().bind(Bindings.createDoubleBinding(() -> aircraftIcon.canRotate() ?  aircraftState.trackOrHeadingProperty().get() : 0.0,
                iconProperty, aircraftState.trackOrHeadingProperty()));

        //svgPath.fillProperty().bind();


        return svgPath;
    }
}


