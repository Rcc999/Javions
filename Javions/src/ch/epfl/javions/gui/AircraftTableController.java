package ch.epfl.javions.gui;

import ch.epfl.javions.adsb.CallSign;
import ch.epfl.javions.aircraft.AircraftData;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
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

public final class AircraftTableController {

    private final ObservableSet<ObservableAircraftState> observableAircraftStates;
    private final ObjectProperty<ObservableAircraftState> selectedAircraft;
    private final TableView<ObservableAircraftState> tableView;
    private Consumer<ObservableAircraftState> aircraftStateConsumer;

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
        //handler();


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

    public Node pane() {
        return tableView;
    }

    public void setOnDoubleClick(Consumer<ObservableAircraftState> aircraftStateConsumer) {
        this.aircraftStateConsumer = aircraftStateConsumer;
    }

    private void handler() {
        tableView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && event.getButton().equals(MouseButton.PRIMARY)) {
                aircraftStateConsumer.accept(tableView.getSelectionModel().getSelectedItem());
            }
        });
    }

    //Have to take into account cases where stuffs are null
    private void setTableStringColumn() {
        //Add constants too

        setStringColumnToTable("OACI", WIDTH_OACI_COLUMN, state -> new ReadOnlyObjectWrapper<>(state.getIcaoAddress().string()));
        setStringColumnToTable("Call Sign", WIDTH_CALL_SIGN_COLUMN, state -> state.callSignProperty().map(CallSign::string));
        setStringColumnToTable("Registration", WIDTH_REGISTRATION_COLUMN, state -> {
            AircraftData data = state.getAircraftData();
            return new ReadOnlyObjectWrapper<>(data).map(d -> d.registration().string());
        });
        setStringColumnToTable("Model", WIDTH_MODELE_COLUMN, state -> {
            AircraftData data = state.getAircraftData();
            return new ReadOnlyObjectWrapper<>(data).map(AircraftData::model);
        });
        setStringColumnToTable("Type", WIDTH_TYPE_COLUMN, state -> {
            AircraftData data = state.getAircraftData();
            return new ReadOnlyObjectWrapper<>(data).map(d -> d.typeDesignator().string());
        });
        setStringColumnToTable("Description", WIDTH_DESCRIPTION_COLUMN, state -> {
            AircraftData data = state.getAircraftData();
            return new ReadOnlyObjectWrapper<>(data).map(d -> d.description().string());
        });
    }

    private void setStringColumnToTable(String title, double width,
                                        Function<ObservableAircraftState, ObservableValue<String>> stringValue) {
        TableColumn<ObservableAircraftState, String> column = new TableColumn<>(title);
        column.setPrefWidth(width);
        column.setCellValueFactory(f -> stringValue.apply(f.getValue()));
        tableView.getColumns().add(column);

    }

    private void setNumericalColumnToTable(String title, Function<ObservableAircraftState,
            ObservableValue<String>> numericalValue, int fractionDigitsMax) {

        TableColumn<ObservableAircraftState, String> column = new TableColumn<>(title);
        column.setPrefWidth(NUMERICAL_COLUMN_WIDTH);

        NumberFormat nf = NumberFormat.getInstance();

        nf.setMaximumFractionDigits(fractionDigitsMax);

        column.setCellValueFactory(f -> numericalValue.apply(f.getValue()));// not sure

        column.setComparator((s1, s2) ->{
            if(s1.isEmpty() || s2.isEmpty()) {
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

        tableView.getColumns().add(column);
    }

}
