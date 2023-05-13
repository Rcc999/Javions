package ch.epfl.javions.gui;

import ch.epfl.javions.adsb.Message;
import ch.epfl.javions.adsb.MessageParser;
import ch.epfl.javions.aircraft.AircraftDatabase;
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

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.nio.file.Path;
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
    private Queue<Message> messages = new ConcurrentLinkedQueue<>();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        URL url = getClass().getResource("/aircraft.zip");
        assert url != null;
        Path path = Path.of(url.toURI()); // need to check for tile-cach

        TileManager tileManager = new TileManager(path, TILE_ORG);
        AircraftDatabase database = new AircraftDatabase(path.toString());
        AircraftStateManager aircraftStateManager = new AircraftStateManager(database);
        MapParameters mapParameters = new MapParameters(ZOOM_INIT, X_COORDINATE, Y_COORDINATE);
        BaseMapController baseMapController = new BaseMapController(tileManager, mapParameters);

        ObjectProperty<ObservableAircraftState> selectedAircraft = new SimpleObjectProperty<>(null);

        AircraftController aircraftController = new AircraftController(mapParameters, aircraftStateManager.states(), selectedAircraft);
        AircraftTableController aircraftTableController = new AircraftTableController(aircraftStateManager.states(), selectedAircraft);
        StatusLineController statusLineController = new StatusLineController();

        statusLineController.aircraftCountProperty().bind(Bindings.size(aircraftStateManager.states()));

        SplitPane splitPane = new SplitPane(
                new StackPane(baseMapController.pane(), aircraftController.pane()),
                new BorderPane(aircraftTableController.pane(), statusLineController.pane(), null, null, null)
        );
        splitPane.setOrientation(Orientation.VERTICAL);
        primaryStage.setTitle(TITLE);
        primaryStage.setMinWidth(WIDTH);
        primaryStage.setMinHeight(HEIGHT);
        primaryStage.setScene(new Scene(splitPane));
        primaryStage.show();

        if(getParameters().getRaw().size() > 0) fileRead();
        else radioRead();


        //TODO: one thread for file, one for System.in and animation timer (update the message, take message from queue)
        // getParam.getRaw.size > 0 --> file read else radio read
    }

    private void fileRead(String fileName, Queue<Message> messages){
        new Thread(() -> {
            var file = getClass().getResource(fileName).getFile();

        });
    }

    private void radioRead(){
        new Thread(() -> {

        });
    }

    private void animationTimer(AircraftStateManager aircraftStateManager, Queue<Message> messages){
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                try {
                    for (int i = 0; i < 10; ++i) {
                        Message message = MessageParser.parse(messages.next());
                        if (message != null) aircraftStateManager.updateWithMessage(message);
                    }
                    aircraftStateManager.purge();
                }
                catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }
        }.start();
    }
}
