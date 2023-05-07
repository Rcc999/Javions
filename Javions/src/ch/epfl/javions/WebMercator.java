package ch.epfl.javions;

/**
 * Projection of geographical coordinates
 *
 * @author Tuan Dang Nguyen (361089)
 * @author Rayane Charif Chefchouni (339839)
 */
public class WebMercator {

    private static final int ZOOM_LEVEL_0 = 8;

    /**
     * Constructor: private - non instantiable
     */
    private WebMercator(){}

    /**
     * Calculate x at a given longitude and zoom level
     *
     * @param zoomLevel: zoom level
     * @param longitude: longitude
     * @return value of longitude in radian corresponds to zoom level
     */
    public static double x(int zoomLevel, double longitude) {
        double x = 0.5 + Units.convertTo(longitude, Units.Angle.TURN);
        return Math.scalb(x, ZOOM_LEVEL_0 + zoomLevel);
    }

    /**
     * Calculate y at a given longitude and zoom level
     *
     * @param zoomLevel: zoom level
     * @param latitude:  latitude
     * @return value of latitude in radian corresponds to zoom level
     */
    public static double y(int zoomLevel, double latitude) {
        double y = 0.5 + Units.convertTo(-Math2.asinh(Math.tan(latitude)), Units.Angle.TURN);
        return Math.scalb(y, ZOOM_LEVEL_0 + zoomLevel);
    }
}
