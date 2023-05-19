package ch.epfl.javions.gui;

import ch.epfl.javions.Units;
import ch.epfl.javions.WebMercator;
import ch.epfl.javions.aircraft.*;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Text;

/**
 * Visible aircraft controller
 *
 * @author Tuan Dang Nguyen (361089)
 * @author Rayane Charif Chefchouni (339839)
 */
public final class AircraftController {

    private final MapParameters mapParameters;
    private final ObservableSet<ObservableAircraftState> observableAircraftStates;
    private final ObjectProperty<ObservableAircraftState> selectedAircraftState;
    private final AircraftData data;
    private final Pane pane;

    /**
     * Construction of a visible aircraft that appears on the map whose scene graph contains:
     * Icon: color and icon of the aircraft
     * Label: altitude and velocity
     * Trajectory: of the aircraft
     *
     * @param mapParameters:            contain x,y of the top left corner and zoom level
     * @param observableAircraftStates: contains the set of states of a visible aircraft
     * @param selectedAircraftState:    an aircraft that is clicked on
     */
    public AircraftController(MapParameters mapParameters,
                              ObservableSet<ObservableAircraftState> observableAircraftStates,
                              ObjectProperty<ObservableAircraftState> selectedAircraftState) {
        this.mapParameters = mapParameters;
        this.observableAircraftStates = observableAircraftStates;
        this.selectedAircraftState = selectedAircraftState;

        data = selectedAircraftState.get() != null
                ? selectedAircraftState.get().getAircraftData()
                : null;

        aircraftStateAddListener();

        pane = new Pane();
        pane.getStylesheets().add("aircraft.css");
        pane.setPickOnBounds(false);
    }

    private void aircraftStateAddListener(){
        observableAircraftStates.addListener((SetChangeListener<ObservableAircraftState>)
                change -> {
                    if (change.wasAdded()) {
                        pane.getChildren().add(annotatedAircraftGroup(change.getElementAdded()));
                    } else if (change.wasRemoved()) {
                        pane.getChildren().removeIf(node -> node.getId().equals(change.getElementRemoved().getIcaoAddress().string()));
                    }
                });
    }

    /**
     * Get the pane of the visible aircraft
     *
     * @return the pane of the visible aircraft
     */
    public Pane pane() {
        return pane;
    }

    /**
     * Construct a group that contains 2 groups:
     * Group of icon and label
     * Group of trajectory
     *
     * @param aircraftState: characteristic of an aircraft
     * @return a group that contains 2 other groups
     */
    private Group annotatedAircraftGroup(ObservableAircraftState aircraftState) {
        Group aircraftGroup = new Group();
        aircraftGroup.setId(aircraftState.getIcaoAddress().string());
        aircraftGroup.getChildren().add(iconAndLabelGroup(aircraftState));
        aircraftGroup.getChildren().add(trajectoryGroup(aircraftState));
        aircraftGroup.viewOrderProperty().bind(aircraftState.altitudeProperty().negate());
        return aircraftGroup;
    }

    private Group trajectoryGroup(ObservableAircraftState aircraftState) {
        Group trajectoryGroup = new Group();
        trajectoryGroup.getStyleClass().add("trajectory");

        mapParameters.zoomLevelProperty().addListener((observable, oldValue, newValue) ->
                updateTrajectoryLine(trajectoryGroup, aircraftState.trajectory()));

        trajectoryGroup.layoutXProperty().bind(mapParameters.minXProperty().negate());
        trajectoryGroup.layoutYProperty().bind(mapParameters.minYProperty().negate());

        aircraftState.trajectory().addListener((ListChangeListener<ObservableAircraftState.AirbornePos>) change ->
                updateTrajectoryLine(trajectoryGroup, aircraftState.trajectory()));

        trajectoryGroup.visibleProperty().bind(selectedAircraftState.isEqualTo(aircraftState));

        updateTrajectoryLine(trajectoryGroup, aircraftState.trajectory());
        return new Group(trajectoryGroup);
    }

    private void updateTrajectoryLine(Group trajectoryGroup, ObservableList<ObservableAircraftState.AirbornePos> aircraftState) {
        trajectoryGroup.getChildren().clear();
        for (int i = 0; i < aircraftState.size() - 1; i++)
            trajectoryGroup.getChildren().add(trajectoryLine(aircraftState.get(i), aircraftState.get(i + 1)));
    }

    private Line trajectoryLine(ObservableAircraftState.AirbornePos startPoint, ObservableAircraftState.AirbornePos endPoint) {
        Line trajectoryLine = new Line();

        trajectoryLine.setStartX(WebMercator.x(mapParameters.getZoomLevel(), startPoint.pos().longitude()));
        trajectoryLine.setEndX(WebMercator.x(mapParameters.getZoomLevel(), endPoint.pos().longitude()));
        trajectoryLine.setStartY(WebMercator.y(mapParameters.getZoomLevel(), startPoint.pos().latitude()));
        trajectoryLine.setEndY(WebMercator.y(mapParameters.getZoomLevel(), endPoint.pos().latitude()));


        if (startPoint.altitude() == endPoint.altitude())
            trajectoryLine.setStroke(ColorRamp.PLASMA.at(startPoint.altitude()));
        else {
            Color c1 = ColorRamp.PLASMA.at(startPoint.altitude());
            Color c2 = ColorRamp.PLASMA.at(endPoint.altitude());
            Stop s1 = new Stop(0, c1);
            Stop s2 = new Stop(1, c2);
            LinearGradient gradient = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, s1, s2);
            trajectoryLine.setStroke(gradient);
        }

        return trajectoryLine;
    }

    /**
     * Construction of a group that contains icon and label of the aircraft
     *
     * @param aircraftState of the aircraft
     * @return a group that contains icon and label of the aircraft
     */
    private Group iconAndLabelGroup(ObservableAircraftState aircraftState) {
        Group iconAndLabelGroup = new Group();

        iconAndLabelGroup.getChildren().add(icon(aircraftState));
        iconAndLabelGroup.getChildren().add(labelGroup(aircraftState));

        iconAndLabelGroup.layoutXProperty().bind(Bindings.createDoubleBinding(() -> {
            double x = WebMercator.x(mapParameters.getZoomLevel(), aircraftState.positionProperty().get().longitude());
            return x - mapParameters.getMinX();
        }, aircraftState.positionProperty(), mapParameters.zoomLevelProperty(), mapParameters.minXProperty()));

        iconAndLabelGroup.layoutYProperty().bind(Bindings.createDoubleBinding(() -> {
            double y = WebMercator.y(mapParameters.getZoomLevel(), aircraftState.positionProperty().get().latitude());
            return y - mapParameters.getMinY();
        }, aircraftState.positionProperty(), mapParameters.zoomLevelProperty(), mapParameters.minYProperty()));


        return iconAndLabelGroup;
    }

    /**
     * Construction of the icon of the aircraft
     *
     * @param aircraftState: State of the aircraft
     * @return the icon of the aircraft
     */
    private SVGPath icon(ObservableAircraftState aircraftState) {
        SVGPath svgPath = new SVGPath();
        svgPath.getStyleClass().add("aircraft");

        AircraftTypeDesignator typeDesignator = data == null
                ? new AircraftTypeDesignator("") : data.typeDesignator();
        AircraftDescription description = data == null
                ? new AircraftDescription("") : data.description();

        AircraftIcon aircraftIcon = (data == null)
                ? AircraftIcon.iconFor(typeDesignator, description, aircraftState.getCategory(), WakeTurbulenceCategory.UNKNOWN)
                : AircraftIcon.iconFor(typeDesignator, description, aircraftState.getCategory(), data.wakeTurbulenceCategory());


        var iconCategory = aircraftState.categoryProperty().map(e -> aircraftIcon);
        svgPath.contentProperty().bind(iconCategory.map(AircraftIcon::svgPath));

        ObjectProperty<AircraftIcon> iconProperty = new SimpleObjectProperty<>(aircraftIcon);

        svgPath.rotateProperty().bind(Bindings.createDoubleBinding(() -> aircraftIcon.canRotate()
                        ? Units.convertTo(aircraftState.getTrackOrHeading(), Units.Angle.DEGREE)
                        : 0.0,
                iconProperty, aircraftState.trackOrHeadingProperty()));


        svgPath.fillProperty().bind(aircraftState.altitudeProperty().map(b -> ColorRamp.PLASMA.at(b.doubleValue())));

        svgPath.setOnMouseClicked(e -> selectedAircraftState.set(aircraftState));

        return svgPath;
    }

    /**
     * Construction of the label of the aircraft
     *
     * @param aircraftState: State of the aircraft
     * @return the label of the aircraft
     */
    private Group labelGroup(ObservableAircraftState aircraftState) {

        Group rectAndText = new Group();
        rectAndText.getStyleClass().add("label");

        Text text = new Text();
        Rectangle rectangle = new Rectangle();


        var velocityText = aircraftState.velocityProperty().map(e ->
                Units.convertTo(e.doubleValue(), Units.Speed.KILOMETER_PER_HOUR));


        StringBinding textBinding = Bindings.createStringBinding(() -> {
            String velocityString = velocityText.getValue() == 0
                    ? "?"
                    : String.format("%.0f", velocityText.getValue());
            String altitudeString = aircraftState.altitudeProperty().getValue() == 0
                    ? "?"
                    : String.format("%.0f", aircraftState.altitudeProperty().getValue());
            return String.format("%s \n%s km/h" + "\u2002" + "%s m", firstLineLabel(aircraftState), velocityString, altitudeString);
        }, velocityText, aircraftState.altitudeProperty());

        text.textProperty().bind(textBinding);

        rectangle.widthProperty().bind(text.layoutBoundsProperty().map(b -> b.getWidth() + 4));
        rectangle.heightProperty().bind(text.layoutBoundsProperty().map(b -> b.getHeight() + 4));

        rectAndText.getChildren().add(rectangle);
        rectAndText.getChildren().add(text);

        rectAndText.visibleProperty().bind(mapParameters.zoomLevelProperty().greaterThanOrEqualTo(11).or(
                selectedAircraftState.isEqualTo(aircraftState)));

        return rectAndText;
    }

    /**
     * Get different data of the aircraft depends on the availability it
     *
     * @param observableAircraftState: State of the aircraft
     * @return the string of data that is not null
     */
    private String firstLineLabel(ObservableAircraftState observableAircraftState) {
        AircraftData aircraftData = observableAircraftState.getAircraftData();
        if (aircraftData != null && aircraftData.registration() != null) return aircraftData.registration().string();
        if(observableAircraftState.getCallSign() != null) return observableAircraftState.getCallSign().string();
        return observableAircraftState.getIcaoAddress().string();
    }

}


