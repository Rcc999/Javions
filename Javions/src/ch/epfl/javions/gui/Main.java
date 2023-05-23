package ch.epfl.javions.gui;

import ch.epfl.javions.ByteString;
import ch.epfl.javions.adsb.Message;
import ch.epfl.javions.adsb.MessageParser;
import ch.epfl.javions.adsb.RawMessage;
import ch.epfl.javions.aircraft.AircraftDatabase;
import ch.epfl.javions.demodulation.AdsbDemodulator;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.nio.file.Path;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;


// TODO:
//  Maybe Something about system.nano time for sleep
//  Check the 1 second purge
//  Direction of scrolling: zoom de-zoom
//  Check trajectory stuff: OK
//  Re-check all the classes (it is not that much)


/**
 * Main program
 *
 * @author Tuan Dang Nguyen (361089)
 * @author Rayane Charif Chefchouni (339839)
 */
public final class Main extends Application {

    private static final String RESOURCE_FILE = "/aircraft.zip";
    private static final String TITLE = "Javion";
    private static final String TILE_ORG = "tile.openstreetmap.org";
    private static final String TILE_FOLDER = "tile-cache";
    private static final long SECOND_NANO = 1_000_000_000L;
    private static final int ZOOM_INIT = 8;
    private static final int X_COORDINATE = 33530;
    private static final int Y_COORDINATE = 23070;
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;

    /**
     * Entry point for a JavaFX application, launching the JavaFX runtime and starting the application's user interface
     *
     * @param args: command-line arguments passed to the program,
     *              allowing you to customize its behavior or provide input from the command line.
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Initialize and configure the initial user interface of the application.
     * <p>
     * We have all the panes of base map, aircraft, table and status line
     * Also a read file or read radio methods to read the messages from different ways
     * Finally an animation timer to handle animation loop - used for continuous animation updates
     *
     * @param primaryStage the primary stage for this application, onto which
     * the application scene can be set.
     * Applications may create other stages, if needed, but they will not be
     * primary stages.
     * @throws Exception if received any from the program
     */
    @Override
    public void start(Stage primaryStage) throws Exception {

        URL url = getClass().getResource(RESOURCE_FILE);
        assert url != null;
        Path path = Path.of(url.toURI());
        Path tilePath = Path.of(TILE_FOLDER);

        var tileManager = new TileManager(tilePath, TILE_ORG);
        var database = new AircraftDatabase(path.toString());
        var aircraftStateManager = new AircraftStateManager(database);
        var mapParameters = new MapParameters(ZOOM_INIT, X_COORDINATE, Y_COORDINATE);
        var baseMapController = new BaseMapController(tileManager, mapParameters);

        ObjectProperty<ObservableAircraftState> selectedAircraft = new SimpleObjectProperty<>(null);

        var aircraftController = new AircraftController(mapParameters, aircraftStateManager.states(), selectedAircraft);
        var aircraftTableController = new AircraftTableController(aircraftStateManager.states(), selectedAircraft);
        aircraftTableController.setOnDoubleClick(e -> baseMapController.centerOn(e.getPosition()));
        var statusLineController = new StatusLineController();

        statusLineController.aircraftCountProperty().bind(Bindings.size(aircraftStateManager.states()));

        var splitPane = new SplitPane(
                new StackPane(baseMapController.pane(), aircraftController.pane()),
                new BorderPane(aircraftTableController.pane(), statusLineController.pane(), null, null, null)
        );
        splitPane.setOrientation(Orientation.VERTICAL);
        primaryStage.setTitle(TITLE);
        primaryStage.setMinWidth(WIDTH);
        primaryStage.setMinHeight(HEIGHT);
        primaryStage.setScene(new Scene(splitPane));
        primaryStage.show();

        Queue<Message> messages = new ConcurrentLinkedQueue<>();

        if (getParameters().getRaw().size() > 0)
            fileRead(getParameters().getRaw().get(0), messages);
        else radioRead(messages);

        animationTimer(aircraftStateManager, messages, statusLineController);

    }

    /**
     * Execution thread: read message from file and add the valid one to the queue passed by the parameter
     *
     * @param fileName: name of the file that contains the messages
     * @param messagesQueue: queue of the valid message read
     */
    private void fileRead(String fileName, Queue<Message> messagesQueue) {

        Thread readFile = new Thread(() -> {
            int bytesRead;
            long currentTimeStampNs;
            byte[] bytes = new byte[RawMessage.LENGTH];
            long previousTime = 0L;
            try (DataInputStream s = new DataInputStream(
                    new FileInputStream(fileName))) {
                do {
                    currentTimeStampNs = s.readLong();
                    bytesRead = s.readNBytes(bytes, 0, bytes.length);
                    RawMessage rawMessage = new RawMessage(currentTimeStampNs, new ByteString(bytes));

                    if(currentTimeStampNs - previousTime > 0){
                        Thread.sleep((currentTimeStampNs - previousTime) / 1_000_000L);
                    }

                    previousTime = currentTimeStampNs;
                    Message message = MessageParser.parse(rawMessage);
                    if(message != null){
                        messagesQueue.add(message);
                    }

                } while (bytesRead == RawMessage.LENGTH);
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        readFile.setDaemon(true);
        readFile.start();
    }

    /**
     * Execution thread: demodulating the signal from the radio to extract messages and add into queue all the valid one
     *
     * @param messages: queue of the message
     */
    private void radioRead(Queue<Message> messages) {
        Thread readRadio = new Thread(() -> {

            try {
                AdsbDemodulator demodulator = new AdsbDemodulator(System.in);
                while (true) {
                    RawMessage rawMessage = demodulator.nextMessage();
                    if (rawMessage != null) {
                        Message message = MessageParser.parse(rawMessage);
                        if (message != null) {
                            messages.add(message);
                        }
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        readRadio.setDaemon(true);
        readRadio.start();
    }

    /**
     * Animation timer to handle animation loop - for continuous animation updates
     *
     * @param aircraftStateManager: use messages in queue to update aircraft state
     * @param messages: contain valid messages
     * @param statusLineController: counting the number aircraft and number of messages
     */
    private void animationTimer(AircraftStateManager aircraftStateManager, Queue<Message> messages,
                                StatusLineController statusLineController) {
        new AnimationTimer() {
            long count = 0L;
            long lastPurge = 0L;
            @Override
            public void handle(long now) {
                for (int i = 0; i < 10; ++i) {
                    while (!messages.isEmpty()) {
                        Message message = messages.poll();
                        if (message != null) {
                            statusLineController.messageCountProperty().set(++count);
                            try {
                                aircraftStateManager.updateWithMessage(message);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                    if(now - lastPurge > SECOND_NANO){
                        aircraftStateManager.purge();
                        lastPurge = now;
                    }
                }
            }
        }.start();
    }
}
