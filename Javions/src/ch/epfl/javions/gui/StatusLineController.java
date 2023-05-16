package ch.epfl.javions.gui;

import javafx.beans.property.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

/**
 * This class is the controller of the status line.
 *
 * @author Tuan Dang Nguyen (361089)
 * @author Rayane Charif Chefchouni (339839)
 */
public final class StatusLineController {

    //The style sheet used to display the status line
    public static final String STYLE_SHEET_TITLE = "status.css";

    private final BorderPane pane;
    private final IntegerProperty aircraftCountProperty;
    private final LongProperty messageCountProperty;

    /**
     * Constructs a new StatusLineController
     */
    public StatusLineController() {

        aircraftCountProperty = new SimpleIntegerProperty(0);
        messageCountProperty = new SimpleLongProperty(0);

        Text aircraftCountPropertyText = new Text();
        Text messageCountPropertyText = new Text();

        aircraftCountPropertyText.textProperty().bind(aircraftCountProperty.asString("Aéronefs visibles : %d"));
        messageCountPropertyText.textProperty().bind(messageCountProperty.asString("Messages reçus : %d"));

        pane = new BorderPane();
        pane.getStylesheets().add(STYLE_SHEET_TITLE);
        pane.setLeft(aircraftCountPropertyText);
        pane.setRight(messageCountPropertyText);
    }

    /**
     * Returns the pane of the status line
     *
     * @return the pane of the status line
     */
    public Pane pane() {
        return pane;
    }

    /**
     * Returns the aircraft count property of the visible aircrafts
     *
     * @return the aircraft count property
     */
    public IntegerProperty aircraftCountProperty() {
        return aircraftCountProperty;
    }

    /**
     * Returns the message count property of the received messages since the beginning of the program
     *
     * @return the message count property
     */
    public LongProperty messageCountProperty() {
        return messageCountProperty;
    }
}
