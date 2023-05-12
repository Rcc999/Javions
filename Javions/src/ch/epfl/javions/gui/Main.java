package ch.epfl.javions.gui;

import ch.epfl.javions.adsb.Message;
import ch.epfl.javions.adsb.MessageParser;
import ch.epfl.javions.aircraft.AircraftDatabase;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.nio.file.Path;

public final class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void start(Stage primaryStage) throws Exception {

        URL url = getClass().getResource("/aircraft.zip");
        assert url != null;
        Path path = Path.of(url.toURI());

        TileManager tileManager = new TileManager(path, "tile.openstreetmap.org");
        AircraftDatabase database = new AircraftDatabase(path.toString());
        AircraftStateManager aircraftStateManager = new AircraftStateManager(database);
        MapParameters mapParameters = new MapParameters(8, 33530, 23070);
        BaseMapController baseMapController = new BaseMapController(tileManager, mapParameters);



        //AircraftController aircraftController = new AircraftController(mapParameters, aircraftStateManager.states(), );
        //AircraftTableController aircraftTableController = new AircraftTableController(aircraftStateManager.states(), );
        StatusLineController statusLineController = new StatusLineController();

        statusLineController.aircraftCountProperty().bind(Bindings.size(aircraftStateManager.states()));

        SplitPane splitPane = new SplitPane();
        StackPane aircraftMapPane = new StackPane();
        aircraftMapPane.getChildren().add(baseMapController.pane());

        BorderPane tableAndStatusPane = new BorderPane();
        tableAndStatusPane.setTop(statusLineController.pane());
        //tableAndStatusPane.setCenter(.pane());

        //SplitPane splitPane = new SplitPane();

        primaryStage.setTitle("Javions");
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);

        /*new AnimationTimer() {
            @Override
            public void handle(long now) {
                try {
                    for (int i = 0; i < 10; ++i) {
                        Message message = MessageParser.parse(messages.next());
                        if (message != null) asm.updateWithMessage(message);
                    }
                    asm.purge();
                }
                catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }
        }.start();*/
    }
}
