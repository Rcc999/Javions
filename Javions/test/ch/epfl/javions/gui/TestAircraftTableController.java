package ch.epfl.javions.gui;

import ch.epfl.javions.ByteString;
import ch.epfl.javions.adsb.Message;
import ch.epfl.javions.adsb.MessageParser;
import ch.epfl.javions.adsb.RawMessage;
import ch.epfl.javions.aircraft.AircraftDatabase;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class TestAircraftTableController extends Application {
    public static void main(String[] args) { launch(args); }

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
        Path tileCache = Path.of("tile-cache");
        TileManager tm = new TileManager(tileCache, "tile.openstreetmap.org");
        MapParameters mp = new MapParameters(17, 17_389_327, 11_867_430);
        //BaseMapController bmc = new BaseMapController(tm, mp);


        // Creation de la base de data
        URL dbUrl = getClass().getResource("/aircraft.zip");
        assert dbUrl != null;
        String f = Path.of(dbUrl.toURI()).toString();
        var db = new AircraftDatabase(f);

        AircraftStateManager asm = new AircraftStateManager(db);
        ObjectProperty<ObservableAircraftState> sap = new SimpleObjectProperty<>();
        //AircraftController ac = new AircraftController(mp, asm.states(), sap);
        AircraftTableController atc = new AircraftTableController(asm.states(), sap);
        var root = new StackPane(atc.pane());
        primaryStage.setScene(new Scene(root));
        primaryStage.show();

        var file = getClass().getResource("/messages_20230318_0915.bin").getFile();
        var messages =
                readAllMessages(file).iterator();

        // Animation des aircraft
        new AnimationTimer() {
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
        }.start();

    }
}
