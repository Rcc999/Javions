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

/**
 * Background map controller
 *
 * @author Tuan Dang Nguyen (361089)
 * @author Rayane Charif Chefchouni (339839)
 */
public final class BaseMapController {

    private static final int PIXEL_PER_TILE = 256;
    private final TileManager tileManager;
    private final MapParameters mapParameters;
    private boolean redrawNeeded;
    private final Canvas canvas;
    private final Pane mainPane;

    /**
     * Construction of the map using the tiles, coordinate x, y of top left corner and zoom level
     *
     * @param tileManager   :   managing the tiles
     * @param mapParameters : contain x,y of the top left corner and zoom level
     */
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

    /**
     * Get the pane of the map in the background
     *
     * @return the pane correspondent to the map
     */
    public Pane pane() {
        return mainPane;
    }

    /**
     * Center the view on the selected aircraft
     *
     * @param geoPos : the position of the aircraft
     */
    public void centerOn(GeoPos geoPos) {

        double xImage = WebMercator.x(mapParameters.getZoomLevel(), geoPos.longitude());
        double yImage = WebMercator.y(mapParameters.getZoomLevel(), geoPos.latitude());

        double shiftX = xImage - canvas.getWidth() / 2 - mapParameters.getMinX();
        double shiftY = yImage - canvas.getHeight() / 2 - mapParameters.getMinY();

        mapParameters.scroll((int) shiftX, (int) shiftY);

        redrawOnNextPulse();
    }

    /**
     * Redraw the map if needed
     */
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

    /**
     * Redraw the map on the next pulse
     */
    private void redrawOnNextPulse() {
        redrawNeeded = true;
        Platform.requestNextPulse();
    }

    /**
     * Handler of all the mouse events
     * <p>
     * Mouse Pressed:  set new position when press
     * Mouse Dragged:  set new position when drag by translating the position
     * Mouse Released: no interaction then position of the mouse is null
     * Mouse scrolled: change zoom level and updating new coordinate of top left corner of the map
     */
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
