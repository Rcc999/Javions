package ch.epfl.javions.gui;

import ch.epfl.javions.Math2;
import ch.epfl.javions.Preconditions;
import ch.epfl.javions.WebMercator;
import javafx.beans.property.*;

//Need Check

public final class MapParameters {

    private final DoubleProperty minXProperty, minYProperty;
    private final IntegerProperty zoomLevelProperty;

    public MapParameters(int zoomLevel, double x, double y) {
        Preconditions.checkArgument(zoomLevel >= 6 && zoomLevel <= 19);// use clamp

        this.zoomLevelProperty = new SimpleIntegerProperty(zoomLevel);
        this.minXProperty = new SimpleDoubleProperty(x);
        this.minYProperty = new SimpleDoubleProperty(y);
    }

    public ReadOnlyDoubleProperty minXProperty(){
        return minXProperty;
    }

    public double getMinX(){
        return minXProperty.get();
    }

    public ReadOnlyDoubleProperty minYProperty(){
        return minYProperty;
    }

    public double getMinY(){
        return minYProperty.get();
    }

    public ReadOnlyIntegerProperty zoomLevelProperty(){
        return zoomLevelProperty;
    }

    public int getZoomLevel(){
        return zoomLevelProperty.get();
    }

    public void scroll(int x, int y){
        minYProperty.set(getMinY() + y);
        minXProperty.set(getMinX() + x);
    }

    public void changeZoomLevel(int zoomDiff){
        int newZoomLevel = getZoomLevel() + zoomDiff;
        Preconditions.checkArgument(Math2.clamp(6, newZoomLevel, 19) == newZoomLevel);
        zoomLevelProperty.set(newZoomLevel);
        minYProperty.set(getMinY() * Math.pow(2,newZoomLevel));
        minXProperty.set(getMinX() * Math.pow(2,newZoomLevel));
    }


}
