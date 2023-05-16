package ch.epfl.javions.gui;

import javafx.beans.property.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

public final class StatusLineController {

    private final BorderPane pane;
    private final IntegerProperty aircraftCountProperty;
    private final LongProperty messageCountProperty;

    public StatusLineController() {

        aircraftCountProperty = new SimpleIntegerProperty(0);
        messageCountProperty = new SimpleLongProperty(0);

        Text aircraftCountPropertyText = new Text();
        Text messageCountPropertyText = new Text();

        aircraftCountPropertyText.textProperty().bind(aircraftCountProperty.asString("Aéronefs visibles : %d"));
        messageCountPropertyText.textProperty().bind(messageCountProperty.asString("Messages reçus : %d"));

        pane = new BorderPane();
        pane.getStylesheets().add("status.css");
        pane.setLeft(aircraftCountPropertyText);
        pane.setRight(messageCountPropertyText);
    }

    public Pane pane (){
        return pane;
    }

    public IntegerProperty aircraftCountProperty() {
        return aircraftCountProperty;
    }

    public LongProperty messageCountProperty() {
        return messageCountProperty;
    }
}
