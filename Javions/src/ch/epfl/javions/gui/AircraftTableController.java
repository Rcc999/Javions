package ch.epfl.javions.gui;

import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableSet;
import javafx.scene.Node;
import javafx.scene.control.TableView;

import static javafx.scene.control.TableView.CONSTRAINED_RESIZE_POLICY_SUBSEQUENT_COLUMNS;

public final class AircraftTableController {

    private final ObservableSet<ObservableAircraftState> observableAircraftStates;
    private final ObjectProperty<ObservableAircraftState> selectedAircraft;
    private final TableView<ObservableAircraftState> tableView;

    public AircraftTableController(ObservableSet<ObservableAircraftState> observableAircraftStates, ObjectProperty<ObservableAircraftState> selectedAircraft) {
        this.observableAircraftStates = observableAircraftStates;
        this.selectedAircraft = selectedAircraft;
        tableView = new TableView<>();
    }

    public Node pane(){
        return tableView;
    }

    private void setTableView(){
        tableView.getStylesheets().add("table.css");
        tableView.setColumnResizePolicy(CONSTRAINED_RESIZE_POLICY_SUBSEQUENT_COLUMNS);

    }
}
