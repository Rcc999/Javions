package ch.epfl.javions;

/**
 * Projection of geographical coordinates
 *
 * @author Tuan Dang Nguyen (361089)
 * @author Rayane Charif Chefchouni (339839)
 */
public class WebMercator {

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
    public static double x(int zoomLevel, double longitude){
        double longitudeEnRad = Units.convertTo(longitude, Units.Angle.RADIAN);
        return (double) Math.scalb(1, 8+zoomLevel) * (longitudeEnRad/(2*Math.PI) + 0.5);
    }

    /**
     * Calculate y at a given longitude and zoom level
     *
     * @param zoomLevel: zoom level
     * @param latitude: latitude
     * @return value of latitude in radian corresponds to zoom level
     */
    public static double y(int zoomLevel, double latitude){
        double latitudeEnRad = Units.convertTo(latitude, Units.Angle.RADIAN);
        return Math.scalb(1, 8+zoomLevel) * ( -Math2.asinh(Math.tan(latitudeEnRad))/(2 * Math.PI) +0.5);
    }
}
