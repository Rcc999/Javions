package ch.epfl.javions.gui;

import ch.epfl.javions.Units;
import ch.epfl.javions.adsb.CallSign;
import ch.epfl.javions.aircraft.AircraftData;
import javafx.beans.binding.DoubleExpression;
import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

import static javafx.scene.control.TableView.CONSTRAINED_RESIZE_POLICY_SUBSEQUENT_COLUMNS;

/**
 * Visible table controller
 *
 * @author Tuan Dang Nguyen (361089)
 * @author Rayane Charif Chefchouni (339839)
 */
public final class AircraftTableController {

    private static final int DOUBLE_CLICK = 2;
    private static final int FRACTION_DIGITS_LAT_LON = 4;
    private static final int FRACTION_DIGITS_ALT_VEL = 0;
    private static final int WIDTH_TYPE_COLUMN = 50;
    private static final int NUMERICAL_COLUMN_WIDTH = 85;
    private static final int WIDTH_OACI_COLUMN = 60;
    private static final int WIDTH_CALL_SIGN_COLUMN = 70;
    private static final int WIDTH_REGISTRATION_COLUMN = 90;
    private static final int WIDTH_MODELE_COLUMN = 230;
    private static final int WIDTH_DESCRIPTION_COLUMN = 70;
    private static final String OACI = "OACI";
    private static final String CALL_SIGN = "Call sign";
    private static final String REGISTRATION = "Registration";
    private static final String MODEL = "Model";
    private static final String TYPE = "Type";
    private static final String DESCRIPTION = "Description";
    private static final String LONGITUDE = "Longitude (°)";
    private static final String LATITUDE = "Latitude (°)";
    private static final String ALTITUDE = "Altitude (m)";
    private static final String VELOCITY = "Velocity (km/h)";
    private static final String EMPTY = "";
    private final ObservableSet<ObservableAircraftState> observableAircraftStates;
    private final ObjectProperty<ObservableAircraftState> selectedAircraft;
    private final TableView<ObservableAircraftState> tableView;
    private Consumer<ObservableAircraftState> aircraftStateConsumer;

    /**
     * Construct a table that contain information and data of an aircraft
     *
     * @param observableAircraftStates: State of aircraft
     * @param selectedAircraft:         Chosen aircraft by clicking on one
     */
    public AircraftTableController(ObservableSet<ObservableAircraftState> observableAircraftStates,
                                   ObjectProperty<ObservableAircraftState> selectedAircraft) {

        this.observableAircraftStates = observableAircraftStates;
        this.selectedAircraft = selectedAircraft;

        tableView = new TableView<>();
        tableView.getStylesheets().add("table.css");
        tableView.setColumnResizePolicy(CONSTRAINED_RESIZE_POLICY_SUBSEQUENT_COLUMNS);
        tableView.setTableMenuButtonVisible(true);

        setTableStringColumn();
        setTableNumericalColumn();
        handler();

        observableAircraftStates.addListener((SetChangeListener<ObservableAircraftState>)
                items -> {
                    if (items.wasAdded()) {
                        tableView.getItems().add(items.getElementAdded());
                        tableView.sort();
                    } else if (items.wasRemoved()) {
                        tableView.getItems().remove(items.getElementRemoved());
                    }
                });

        selectedAircraft.addListener((observable, oldValue, newValue) -> {
            if (!Objects.equals(tableView.getSelectionModel().getSelectedItem(), newValue)) {
                tableView.scrollTo(newValue);
            }
            tableView.getSelectionModel().select(newValue);
        });

        tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
                selectedAircraft.set(newValue));
    }

    /**
     * Get the pane in which drawn the table of visible aircraft
     *
     * @return the pane that draws the table
     */
    public Node pane() {
        return tableView;
    }

    public void setOnDoubleClick(Consumer<ObservableAircraftState> aircraftStateConsumer) {
        this.aircraftStateConsumer = aircraftStateConsumer;
    }

    private void handler() {
        tableView.setOnMouseClicked(event -> {
            if (event.getClickCount() == DOUBLE_CLICK && event.getButton().equals(MouseButton.PRIMARY) && aircraftStateConsumer != null) {
                aircraftStateConsumer.accept(tableView.getSelectionModel().getSelectedItem());
            }
        });
    }

    /**
     * Set string text columns to the table
     */
    private void setTableStringColumn() {
        //Add constants too
        setStringColumnToTable(OACI, WIDTH_OACI_COLUMN, state ->
                new ReadOnlyObjectWrapper<>(state.getIcaoAddress().string()));
        setStringColumnToTable(CALL_SIGN, WIDTH_CALL_SIGN_COLUMN, state ->
                state.callSignProperty().map(CallSign::string));
        setStringColumnToTable(REGISTRATION, WIDTH_REGISTRATION_COLUMN, state -> {
            AircraftData data = state.getAircraftData(); //maybe it can be optimised more
            return new ReadOnlyObjectWrapper<>(data).map(d -> d.registration().string());
        });
        setStringColumnToTable(MODEL, WIDTH_MODELE_COLUMN, state -> {
            AircraftData data = state.getAircraftData();
            return new ReadOnlyObjectWrapper<>(data).map(AircraftData::model);
        });
        setStringColumnToTable(TYPE, WIDTH_TYPE_COLUMN, state -> {
            AircraftData data = state.getAircraftData();
            return new ReadOnlyObjectWrapper<>(data).map(d -> d.typeDesignator().string());
        });
        setStringColumnToTable(DESCRIPTION, WIDTH_DESCRIPTION_COLUMN, state -> {
            AircraftData data = state.getAircraftData();
            return new ReadOnlyObjectWrapper<>(data).map(d -> d.description().string());
        });
    }

    /**
     * Construct a text column
     *
     * @param title       of the column
     * @param width       of the column
     * @param stringValue that will be put in the column
     */
    private void setStringColumnToTable(String title, double width,
                                        Function<ObservableAircraftState, ObservableValue<String>> stringValue) {

        TableColumn<ObservableAircraftState, String> column = new TableColumn<>(title);
        column.setPrefWidth(width);
        column.setCellValueFactory(f -> stringValue.apply(f.getValue()));
        tableView.getColumns().add(column);

    }

    /**
     * Set numerical text columns to the table
     */
    private void setTableNumericalColumn() {
        //Haven't taken into account cases where stuffs are 0: SOLVED
        //Number behind comma doesn't work yet: SOLVED

        setNumericalColumnToTable(LONGITUDE, state ->
                new SimpleDoubleProperty(state.getPosition().longitude()), FRACTION_DIGITS_LAT_LON, Units.Angle.DEGREE);
        setNumericalColumnToTable(LATITUDE, state ->
                new SimpleDoubleProperty(state.getPosition().latitude()), FRACTION_DIGITS_LAT_LON, Units.Angle.DEGREE);
        setNumericalColumnToTable(ALTITUDE, state ->
                new SimpleDoubleProperty(state.getAltitude()), FRACTION_DIGITS_ALT_VEL, Units.Length.METER);
        setNumericalColumnToTable(VELOCITY, state ->
                new SimpleDoubleProperty(state.getVelocity()), FRACTION_DIGITS_ALT_VEL, Units.Speed.KILOMETER_PER_HOUR);
    }

    /**
     * Construct a numerical column
     *
     * @param title             of the column
     * @param numericalValue    that will be put in the column
     * @param fractionDigitsMax is number of decimal of that will be displayed on the table
     * @param unity             of the value
     */
    private void setNumericalColumnToTable(String title, Function<ObservableAircraftState,
            DoubleExpression> numericalValue, int fractionDigitsMax, double unity) {

        TableColumn<ObservableAircraftState, String> numericalColumn = new TableColumn<>(title);
        numericalColumn.getStyleClass().add("numeric");
        numericalColumn.setPrefWidth(NUMERICAL_COLUMN_WIDTH);

        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(fractionDigitsMax);
        nf.setMinimumFractionDigits(fractionDigitsMax);

        numericalColumn.setCellValueFactory(f -> numericalValue.apply(f.getValue()).map(d -> d.doubleValue() == 0
                ? EMPTY
                : nf.format(Units.convertTo(d.doubleValue(), unity))));

        numericalColumn.setComparator((s1, s2) -> {
            if (s1.isEmpty() || s2.isEmpty()) {
                return s1.compareTo(s2);
            } else {
                try {
                    Number n1 = nf.parse(s1);
                    Number n2 = nf.parse(s2);
                    return Double.compare(n1.doubleValue(), n2.doubleValue());
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        tableView.getColumns().add(numericalColumn);
    }

}
