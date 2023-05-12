package ch.epfl.javions.gui;

import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

public final class StatusLineController {

    private final BorderPane pane;
    private final LongProperty aircraftCount;
    private final LongProperty messageCount;

    public StatusLineController() {

        aircraftCount = new SimpleLongProperty(0);
        messageCount = new SimpleLongProperty(0);

        Text aircraftCountText = new Text();
        Text messageCountText = new Text();

        aircraftCountText.textProperty().bind(aircraftCount.asString("Aéronefs visibles : %d"));
        messageCountText.textProperty().bind(messageCount.asString("Messages reçus : %d"));

        pane = new BorderPane();
        pane.getStylesheets().add("status.css");
        pane.setLeft(aircraftCountText);
        pane.setRight(messageCountText);
    }

    public Pane pane (){
        return pane;
    }

    public LongProperty aircraftCountProperty() {
        return aircraftCount;
    }

    public LongProperty messageCountProperty() {
        return messageCount;
    }
}
