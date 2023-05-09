package ch.epfl.javions.gui;

import ch.epfl.javions.adsb.CallSign;
import ch.epfl.javions.aircraft.AircraftData;
import ch.epfl.javions.aircraft.IcaoAddress;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;

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

        setTableStringColumn();

        observableAircraftStates.addListener((SetChangeListener<ObservableAircraftState>)
                items -> {
                    if(items.wasAdded()){
                        tableView.getItems().add(items.getElementAdded());
                        tableView.sort();
                    } else if(items.wasRemoved()){
                        tableView.getItems().remove(items.getElementRemoved());
                    }
                });

        selectedAircraft.addListener((observable, oldValue, newValue) -> {
            if(!Objects.equals(tableView.getSelectionModel().getSelectedItem(), newValue)){
                 tableView.scrollTo(newValue);
            }
                tableView.getSelectionModel().select(newValue);
                });

        tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
                selectedAircraft.set(newValue));
    }

    public Node pane(){
        return tableView;
    }

    public void setOnDoubleClick(Consumer<ObservableAircraftState> aircraftStateConsumer){
        this.aircraftStateConsumer = aircraftStateConsumer;
    }

    private void handler(){

        tableView.setOnMouseClicked(event -> {
            if(event.getClickCount() == 2 && event.getButton().equals(MouseButton.PRIMARY)){
                aircraftStateConsumer.accept(tableView.getSelectionModel().getSelectedItem());
            }
        });
    }

    //Have to take into account cases where stuffs are null
    private void setTableStringColumn(){
        Button button = new Button();

        tableView.getStylesheets().add("table.css");
        tableView.setColumnResizePolicy(CONSTRAINED_RESIZE_POLICY_SUBSEQUENT_COLUMNS);

        setColumnToTable("OACI", 60, state -> new ReadOnlyObjectWrapper<>(state.getIcaoAddress().string()));
        setColumnToTable("Call Sign", 70, state -> state.callSignProperty().map(CallSign::string));
        setColumnToTable("Registration", 90, state -> {
            AircraftData data = state.getAircraftData();
            return new ReadOnlyObjectWrapper<>(data).map(d -> d.registration().string());
        });
        setColumnToTable("Model", 230, state -> {
            AircraftData data = state.getAircraftData();
            return new ReadOnlyObjectWrapper<>(data).map(AircraftData::model);
        });
        setColumnToTable("Type", 50, state -> {
            AircraftData data = state.getAircraftData();
            return new ReadOnlyObjectWrapper<>(data).map(d -> d.typeDesignator().string());
        });
        setColumnToTable("Description", 70, state -> {
            AircraftData data = state.getAircraftData();
            return new ReadOnlyObjectWrapper<>(data).map(d -> d.description().string());
        });
    }

    private void setTableNumericColumn(){

        tableView.getStyleClass().add("numeric");
    }

    private void setColumnToTable(String title, double width,
                                  Function<ObservableAircraftState, ObservableValue<String>> stringValue){

        TableColumn<ObservableAircraftState, String> column = new TableColumn<>(title);
        column.setPrefWidth(width);
        column.setCellValueFactory(f -> stringValue.apply(f.getValue()));

        tableView.getColumns().add(column);

    }


}
