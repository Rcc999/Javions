package ch.epfl.javions.gui;

import ch.epfl.javions.ByteString;
import ch.epfl.javions.adsb.AirbornePositionMessage;
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
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public final class Main extends Application {

    private static final String TITLE = "Javion";
    private static final String TILE_ORG = "tile.openstreetmap.org";
    private static final int ZOOM_INIT = 8;
    private static final int X_COORDINATE = 33530;
    private static final int Y_COORDINATE = 23070;
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        URL url = getClass().getResource("/aircraft.zip");
        assert url != null;
        Path path = Path.of(url.toURI()); // need to check for tile-cache

        var tileManager = new TileManager(path, TILE_ORG);
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




        if(getParameters().getRaw().size() > 0) fileRead( , messages);
        else radioRead(messages);


        animationTimer(aircraftStateManager, messages);

        //TODO: one thread for file, one for System.in and animation timer (update the message, take message from queue)
        // getParam.getRaw.size > 0 --> file read else radio read
        // setDeamon with true as argument
        //  check with all the methods
        //  haven't done the mousing
    }

    private List<RawMessage> readAllMessages(String fileName) throws IOException {
        List<RawMessage> messages = new ArrayList<>();
        int bytesRead;
        long timeStampNs;
        byte[] bytes = new byte[RawMessage.LENGTH];
        try (DataInputStream s = new DataInputStream(
                new FileInputStream(fileName))) {
            do {
                timeStampNs = s.readLong();
                bytesRead = s.readNBytes(bytes, 0, bytes.length);
                messages.add(new RawMessage(timeStampNs, new ByteString(bytes)));
            } while (bytesRead == RawMessage.LENGTH);
        } catch (EOFException e) { /* ignore */ }
        return messages;
    }

    private void fileRead(String fileName, Queue<Message> messagesQueue, long startTime) throws IOException {

        var iterator = readAllMessages(fileName).iterator();

        Thread readFile = new Thread(() -> {
            while (iterator.hasNext()) {
                long currentTime = System.nanoTime();
                if (currentTime - startTime <= iterator.next().timeStampNs()) {
                    //Thread.sleep(iterator.next().timeStampNs());
                } else {
                        Message message = MessageParser.parse(iterator.next());
                        if (message != null) {
                            messagesQueue.add(message);
                        }
                    }
                }
            //Add in queue in here
            // <= à timeStampNs
        });
    }

    private void radioRead(Queue<Message> messages){
        new Thread(() -> {
            try {
                AdsbDemodulator demodulator = new AdsbDemodulator(System.in);
                RawMessage rawMessage;
                while ((rawMessage = demodulator.nextMessage()) != null) {
                    Message message = MessageParser.parse(rawMessage);
                    if(message != null)
                        messages.add(message);
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void animationTimer(AircraftStateManager aircraftStateManager, Queue<Message> messages, StatusLineController statusLineController) {
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                for (int i = 0; i < 10; ++i) {
                    while (!messages.isEmpty()) {
                        Message message = messages.poll();
                        if (message != null) {
                            try {
                                aircraftStateManager.updateWithMessage(message);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }


                }
                aircraftStateManager.purge();
            }
        }.start();
    }
}
