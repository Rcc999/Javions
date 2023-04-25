package ch.epfl.javions.gui;

import ch.epfl.javions.WebMercator;

public final class MapParameters {

private final int zoomLevel;
    private final double x;
    private final double y;

    public MapParameters(int zoomLevel, double x, double y) {
        this.zoomLevel = zoomLevel;
        this.x = x;
        this.y = y;
    }
}
