package ch.epfl.javions.gui;

import ch.epfl.javions.ByteString;
import ch.epfl.javions.GeoPos;
import ch.epfl.javions.adsb.Message;
import ch.epfl.javions.adsb.MessageParser;
import ch.epfl.javions.adsb.RawMessage;
import ch.epfl.javions.aircraft.AircraftDatabase;
import ch.epfl.javions.aircraft.IcaoAddress;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public final class AircraftControllerTest extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    static List<RawMessage> readAllMessages(String fileName) throws IOException {
        List<RawMessage> messages = new ArrayList<>();
        int bytesRead;
        long timeStampNs;
        byte[] bytes = new byte[RawMessage.LENGTH];
        try (DataInputStream s = new DataInputStream(
                new BufferedInputStream(
                        new FileInputStream(fileName)))) {
            do {
                timeStampNs = s.readLong();
                bytesRead = s.readNBytes(bytes, 0, bytes.length);
                messages.add(new RawMessage(timeStampNs, new ByteString(bytes)));
            } while (bytesRead == RawMessage.LENGTH);
        }
        catch (EOFException e) { /* ignore */ }
        return messages;

    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        var tileCache = Path.of("tile-cache");
        var tileManager = new TileManager(tileCache, "tile.openstreetmap.org");
        var mapParameters = new MapParameters(17, 17_389_327, 11_867_430);
        var baseMapController = new BaseMapController(tileManager, mapParameters);

        var dataBaseURL = getClass().getResource("/aircraft.zip");
        assert dataBaseURL != null;
        var dataBase = new AircraftDatabase(Path.of(dataBaseURL.toURI()).toString());

        var aircraftStateManager = new AircraftStateManager(dataBase);
        ObjectProperty<ObservableAircraftState> selectedAircraftState =
                new SimpleObjectProperty<>(new ObservableAircraftState(
                        new IcaoAddress("4B9F9C"), null));
        var aircraftController = new AircraftController(mapParameters,
                aircraftStateManager.states(), selectedAircraftState);

        ObjectProperty<ChangeListener<GeoPos>> listenerProperty = new SimpleObjectProperty<>();
        selectedAircraftState.addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                if (listenerProperty.get() != null) {
                    oldValue.positionProperty().removeListener(listenerProperty.get());
                }
                listenerProperty.set((observable1, oldValue1, newValue1) -> baseMapController.centerOn(newValue1));
                baseMapController.centerOn(newValue.getPosition());
                newValue.positionProperty().addListener(listenerProperty.get());
            }
        });

        var root = new StackPane(baseMapController.pane(), aircraftController.pane());
        primaryStage.setScene(new Scene(root));
        primaryStage.show();

        var file = Objects.requireNonNull(getClass().getResource("/messages_20230318_0915.bin")).getFile();
        var messages =
                readAllMessages(file).iterator();

        new AnimationTimer() {
            @Override
            public void handle(long now) {
                try {
                    for (int i = 0; i < 10; ++i) {
                        Message message = MessageParser.parse(messages.next());
                        if (message != null) {
                            aircraftStateManager.updateWithMessage(message);
                        }
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