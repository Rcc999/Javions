package ch.epfl.javions.gui;

import ch.epfl.javions.Math2;
import ch.epfl.javions.Preconditions;
import javafx.beans.property.*;

//check scroll up scroll down

/**
 * Parameters of the map in the background
 *
 * @author Tuan Dang Nguyen (361089)
 * @author Rayane Charif Chefchouni (339839)
 */
public final class MapParameters {

    private final static int MINIMUM_ZOOM_LEVEL = 6;
    private final static int MAXIMUM_ZOOM_LEVEL = 19;
    private final static int SCALE_FACTOR = 2;
    private final DoubleProperty minXProperty, minYProperty;
    private final IntegerProperty zoomLevelProperty;

    /**
     * Construction of a map that depends on coordinate x, y and the zoom level
     *
     * @param zoomLevel of the map
     * @param x:        coordinate top left x of the map
     * @param y:        coordinate top left y of the map
     * @throws IllegalArgumentException if zoom level is less than 6 or more than 19
     */
    public MapParameters(int zoomLevel, double x, double y) {
        Preconditions.checkArgument(zoomLevel >= MINIMUM_ZOOM_LEVEL && zoomLevel <= MAXIMUM_ZOOM_LEVEL);

        this.zoomLevelProperty = new SimpleIntegerProperty(zoomLevel);
        this.minXProperty = new SimpleDoubleProperty(x);
        this.minYProperty = new SimpleDoubleProperty(y);
    }

    /**
     * Get the property of the top left coordinate x
     *
     * @return read-only property top left coordinate x
     */
    public ReadOnlyDoubleProperty minXProperty() {
        return minXProperty;
    }

    /**
     * Get the value of the top left coordinate x
     *
     * @return value of the top left coordinate x
     */
    public double getMinX() {
        return minXProperty.get();
    }

    /**
     * Get the property of the top left coordinate y
     *
     * @return read-only property top left coordinate y
     */
    public ReadOnlyDoubleProperty minYProperty() {
        return minYProperty;
    }

    /**
     * Get the value of the top left coordinate y
     *
     * @return value of the top left coordinate y
     */
    public double getMinY() {
        return minYProperty.get();
    }

    /**
     * Get the value of the top left coordinate x
     *
     * @return value of the top left coordinate x
     */
    public ReadOnlyIntegerProperty zoomLevelProperty() {
        return zoomLevelProperty;
    }

    /**
     * Get the property of the zoom level
     *
     * @return read-only property the zoom level
     */
    public int getZoomLevel() {
        return zoomLevelProperty.get();
    }

    /**
     * Translation of coordinate x and y of the top left corner
     *
     * @param x: coordinate of top left corner
     * @param y: coordinate of top left corner
     */
    public void scroll(int x, int y) {
        minYProperty.set(getMinY() + y);
        minXProperty.set(getMinX() + x);
    }

    /**
     * Updating coordinate x and y of the top left corner, depending on zoom level
     *
     * @param zoomDiff is the different between the new zoom level and old zoom level
     * @throws IllegalArgumentException if the new zoom level is less than 6 or more than 19
     */
    public void changeZoomLevel(int zoomDiff) {
        int newZoomLevel = Math2.clamp(MINIMUM_ZOOM_LEVEL, getZoomLevel() + zoomDiff, MAXIMUM_ZOOM_LEVEL);
        double scaleFactor = Math.pow(SCALE_FACTOR, newZoomLevel - getZoomLevel());
        minXProperty.set(getMinX() * scaleFactor);
        minYProperty.set(getMinY() * scaleFactor);
        zoomLevelProperty.set(newZoomLevel);
    }

}
