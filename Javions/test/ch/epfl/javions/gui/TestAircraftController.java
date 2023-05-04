package ch.epfl.javions.gui;

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

public class TestAircraftController extends Application {
    public static void main(String[] args) { launch(args); }

    static List<RawMessage> readAllMessages() throws IOException {
        List<RawMessage> rawMessages = new ArrayList<>();
        try (DataInputStream s = new DataInputStream(
                new BufferedInputStream(
                        new FileInputStream(TestAircraftController.class.getResource("/messages_20230318_0915.bin").getFile())))){
            byte[] bytes = new byte[RawMessage.LENGTH];
            while (s.read() != -1) {
                long timeStampNs = s.readLong();
                int bytesRead = s.readNBytes(bytes, 0, bytes.length);
                assert bytesRead == RawMessage.LENGTH;
                RawMessage rawMessage = RawMessage.of(timeStampNs, bytes);
                if(rawMessage != null) rawMessages.add(rawMessage);

            }
        }
        return rawMessages;
    }


    @Override
    public void start(Stage primaryStage) throws Exception {
        Path tileCache = Path.of("tile-cache");
        TileManager tm =
                new TileManager(tileCache, "tile.openstreetmap.org");
        MapParameters mp =
                new MapParameters(17, 17_389_327, 11_867_430);
        BaseMapController bmc = new BaseMapController(tm, mp);


        // Creation de la base de data
        URL dbUrl = getClass().getResource("/aircraft.zip");
        assert dbUrl != null;
        String f = Path.of(dbUrl.toURI()).toString();
        var db = new AircraftDatabase(f);

        AircraftStateManager asm = new AircraftStateManager(db);
        ObjectProperty<ObservableAircraftState> sap =
                new SimpleObjectProperty<>();
        AircraftController ac =
                new AircraftController(mp, asm.states(), sap);
        var root = new StackPane(bmc.pane(), ac.pane());
        primaryStage.setScene(new Scene(root));
        primaryStage.show();

        var mi = readAllMessages()
                .iterator();

        // Animation des aircraft
        new AnimationTimer() {
            @Override

            public void handle(long now) {
                try {
                    for (int i = 0; i < 10; i += 1) {
                        Message m = mi.hasNext() ? MessageParser.parse(mi.next()) : null;
                        if (m != null)
                            asm.updateWithMessage(m);
                        //asm.purge();
                    }
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }
        }.start();

    }
}
