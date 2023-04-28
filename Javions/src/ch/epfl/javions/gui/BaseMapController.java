package ch.epfl.javions.gui;

import ch.epfl.javions.GeoPos;
import ch.epfl.javions.WebMercator;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;

import java.io.IOException;


public final class BaseMapController {

    public static final int PIXEL_PER_TILE = 256;
    private final TileManager tileManager;
    private final MapParameters mapParameters;
    private boolean redrawNeeded;
    private final Canvas canvas;
    private final Pane mainPane;

    public BaseMapController(TileManager tileManager, MapParameters mapParameters) {
        this.tileManager = tileManager;

        this.mapParameters = mapParameters;
        this.mapParameters.zoomLevelProperty().addListener((p, oldS, newS) -> redrawOnNextPulse());
        this.mapParameters.minXProperty().addListener((observable, oldValue, newValue) -> redrawOnNextPulse());
        this.mapParameters.minYProperty().addListener((observable, oldValue, newValue) -> redrawOnNextPulse());

        canvas = new Canvas();
        mainPane = new Pane(canvas);

        canvas.widthProperty().bind(mainPane.widthProperty());
        canvas.heightProperty().bind(mainPane.heightProperty());

        canvas.widthProperty().addListener((p, oldS, newS) -> redrawOnNextPulse());
        canvas.heightProperty().addListener((p, oldS, newS) -> redrawOnNextPulse());
        canvas.sceneProperty().addListener((p, oldS, newS) -> {
            assert oldS == null;
            newS.addPreLayoutPulseListener(this::redrawIfNeeded);
        });

        eventHandler();
    }

    public Pane pane() {
        return mainPane;
    }

    public void centerOn(GeoPos geoPos) {
        // Convertir les coordonnées géographiques (longitude, latitude) en coordonnées de l'image (x, y) avec WebMercator
        double xImage = WebMercator.x(mapParameters.getZoomLevel(), geoPos.longitude());
        double yImage = WebMercator.y(mapParameters.getZoomLevel(), geoPos.latitude());

        // Calculer les décalages nécessaires pour centrer la carte sur les coordonnées de l'image
        double decalageX = xImage - canvas.getWidth() / 2 - mapParameters.getMinX(); //Not sure
        double decalageY = yImage - canvas.getHeight() / 2 - mapParameters.getMinY();

        // Mettre à jour les paramètres de la carte pour centrer la vue sur les coordonnées
        mapParameters.scroll((int) decalageX, (int) decalageY);

        redrawOnNextPulse();
    }


    private void redrawIfNeeded() {
        if (!redrawNeeded) return;
        redrawNeeded = false;

        GraphicsContext context = canvas.getGraphicsContext2D();
        context.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        double xImageSpace = mapParameters.getMinX() % PIXEL_PER_TILE;
        double yImageSpace = mapParameters.getMinY() % PIXEL_PER_TILE;
        for (int x = 0; x <= canvas.getWidth() + PIXEL_PER_TILE; x += PIXEL_PER_TILE) {
            for (int y = 0; y <= canvas.getHeight() + PIXEL_PER_TILE; y += PIXEL_PER_TILE) {
                int tileXCoordinate = Math.floorDiv((int) (mapParameters.getMinX() + x), PIXEL_PER_TILE);
                int tileYCoordinate = Math.floorDiv((int) (mapParameters.getMinY() + y), PIXEL_PER_TILE);

                if (TileManager.TileId.isValid(mapParameters.getZoomLevel(), tileXCoordinate, tileYCoordinate)) {
                    TileManager.TileId tiles = new TileManager.TileId(mapParameters.getZoomLevel(),
                            tileXCoordinate,
                            tileYCoordinate);
                    try {
                        context.drawImage(tileManager.imageForTileAt(tiles), x - xImageSpace, y - yImageSpace, PIXEL_PER_TILE, PIXEL_PER_TILE);
                    } catch (IOException ignored) {
                    }
                }
            }
        }
    }

    private void redrawOnNextPulse() {
        redrawNeeded = true;
        Platform.requestNextPulse();
    }

    private void eventHandler() {

        ObjectProperty<Point2D> lastMousePosition = new SimpleObjectProperty<>();

        mainPane.setOnMousePressed(e -> lastMousePosition.set(new Point2D(e.getX(), e.getY())));


        mainPane.setOnMouseDragged(e -> {
            Point2D currentPosition = new Point2D(e.getX(), e.getY());
            Point2D delta = currentPosition.subtract(lastMousePosition.get());
            mapParameters.scroll(-(int) delta.getX(), -(int) delta.getY());
            lastMousePosition.set(currentPosition);
        });

        mainPane.setOnMouseReleased(e -> lastMousePosition.set(null));

        LongProperty minScrollTime = new SimpleLongProperty();
        mainPane.setOnScroll(e -> {
            int zoomDelta = (int) Math.signum(e.getDeltaY());
            if (zoomDelta == 0) return;

            long currentTime = System.currentTimeMillis();
            if (currentTime < minScrollTime.get()) return;
            minScrollTime.set(currentTime + 200);

            Point2D mousePos = new Point2D(e.getX(), e.getY());
            int offsetX = (int) (mousePos.getX());
            int offsetY = (int) (mousePos.getY());
            mapParameters.scroll(offsetX, offsetY);
            mapParameters.changeZoomLevel(-zoomDelta);
            mapParameters.scroll(-offsetX, -offsetY);
        });
    }
}
