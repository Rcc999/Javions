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

    private final BorderPane pane;
    private final IntegerProperty aircraftCountProperty;
    private final LongProperty messageCountProperty;
    private static final int INITIAL_COUNT_INTEGER = 0;
    private static final long INITIAL_COUNT_LONG = 0L;
    private static final String VISIBLE_AIRCRAFT = "Aéronefs visibles : %d";
    private static final String RECEIVED_MESSAGES = "Messages reçus : %d";

    /**
     * Constructs a new StatusLineController
     */
    public StatusLineController() {

        aircraftCountProperty = new SimpleIntegerProperty(INITIAL_COUNT_INTEGER);
        messageCountProperty = new SimpleLongProperty(INITIAL_COUNT_LONG);

        Text aircraftCountPropertyText = new Text();
        Text messageCountPropertyText = new Text();

        aircraftCountPropertyText.textProperty().bind(aircraftCountProperty.asString(VISIBLE_AIRCRAFT));
        messageCountPropertyText.textProperty().bind(messageCountProperty.asString(RECEIVED_MESSAGES));

        pane = new BorderPane();
        pane.getStylesheets().add("status.css");
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
     * Returns the aircraft count property of all the visible aircraft
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
