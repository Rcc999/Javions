package ch.epfl.javions.adsb;

import ch.epfl.javions.GeoPos;
import ch.epfl.javions.Preconditions;
import ch.epfl.javions.Units;

/**
 * A class that decodes the position of an aircraft from the CPR encoded position
 *
 * @author Tuan Dang Nguyen (361089)
 * @author Rayane Charif Chefchouni (339839)
 */
public class CprDecoder {

    private static final double HALF_A_TURN = 0.5;
    private static final double ZONE_LATITUDE0 = 60.0;
    private static final double ZONE_LATITUDE1 = 59.0;
    private static final double ZONE_LONGITUDE_NAN = 1.0;

    /**
     * Private constructor - non instantiable
     */
    private CprDecoder() {
    }

    /**
     * Get the position component of an aircraft
     *
     * @param x0         local longitude of even message
     * @param y0         local latitude of even message
     * @param x1         local longitude of odd message
     * @param y1         local latitude of odd message
     * @param mostRecent message
     * @return longitude and latitude of the aircraft
     * or null if the latitude of the decoded position is invalid (i.e. within ±90°)
     * or if the position cannot be determined due to a latitude band change
     * @throws IllegalStateException if most recent isn't 1 nor 0
     */
    public static GeoPos decodePosition(double x0, double y0, double x1, double y1, int mostRecent) {
        Preconditions.checkArgument(mostRecent == 1 || mostRecent == 0);

        //Calculate les numéros de zones de latitude
        double nb_zone_lat = Math.rint(y0 * ZONE_LATITUDE1 - y1 * ZONE_LATITUDE0);

        double index_zone_lat0 = indexZoneCalculator(nb_zone_lat, ZONE_LATITUDE0);
        double index_zone_lat1 = indexZoneCalculator(nb_zone_lat, ZONE_LATITUDE1);

        //Latitude in TURN
        double latitude0 = longOrLatCalculator(ZONE_LATITUDE0, index_zone_lat0, y0);
        double latitude1 = longOrLatCalculator(ZONE_LATITUDE1, index_zone_lat1, y1);

        //Calculate zone_longitude
        double B0 = calculateB(latitude0);
        double B1 = calculateB(latitude1);
        double A0 = Math.acos(1 - B0);
        double A1 = Math.acos(1 - B1);

        double zone_longitude0;
        if (Double.isNaN(A0) && Double.isNaN(A1)) {
            zone_longitude0 = ZONE_LONGITUDE_NAN;
        } else {
            if (!compare(A0, A1)) {
                return null;
            }
            zone_longitude0 = Math.floor((2 * Math.PI) / A0);
        }
        double zone_longitude1 = zone_longitude0 - 1;

        //Calculate longitude in TURN
        double longitude0;
        double longitude1;
        if (zone_longitude0 == ZONE_LONGITUDE_NAN) {
            longitude0 = x0;
            longitude1 = x1;
        } else {
            double index_zone_long = Math.rint(x0 * zone_longitude1 - x1 * zone_longitude0);

            double index_zone_long0 = indexZoneCalculator(index_zone_long, zone_longitude0);
            double index_zone_long1 = indexZoneCalculator(index_zone_long, zone_longitude1);

            longitude0 = longOrLatCalculator(zone_longitude0, index_zone_long0, x0);
            longitude1 = longOrLatCalculator(zone_longitude1, index_zone_long1, x1);
        }

        //Center at 0
        latitude0 = centerAt0(latitude0);
        latitude1 = centerAt0(latitude1);
        longitude0 = centerAt0(longitude0);
        longitude1 = centerAt0(longitude1);

        int latitude0_T32 = toT32(latitude0);
        int latitude1_T32 = toT32(latitude1);
        int longitude0_T32 = toT32(longitude0);
        int longitude1_T32 = toT32(longitude1);

        //Return longitude and latitude depend on most recent (even or odd)
        if (mostRecent == 1) {
            return !GeoPos.isValidLatitudeT32(latitude1_T32) ? null : new GeoPos(longitude1_T32, latitude1_T32);
        } else {
            return !GeoPos.isValidLatitudeT32(latitude0_T32) ? null : new GeoPos(longitude0_T32, latitude0_T32);
        }
    }

    /**
     * Calculate the index zone either of longitude or latitude
     *
     * @param index_zone : number of zone
     * @param zone of the aircraft
     * @return the result of the calculation
     */
    private static double indexZoneCalculator(double index_zone, double zone) {
        return (index_zone < 0) ? (index_zone + zone) : index_zone;
    }

    /**
     * It is used to calculate A later
     *
     * @param a: latitude of the aircraft
     * @return the result of the calculation
     */
    private static double calculateB(double a) {
        return (1 - Math.cos(2 * Math.PI * (1 / ZONE_LATITUDE0))) / Math.pow(Math.cos(Units.convert(a, Units.Angle.TURN, Units.Angle.RADIAN)), 2);
    }

    /**
     * Compare x and y
     *
     * @param x : "A0" calculated by latitude0
     * @param y : "A1" calculated by latitude1
     * @return true if A0 = A1, otherwise false
     */
    private static boolean compare(double x, double y)  {
        double a1 = Math.floor((2 * Math.PI) / x);
        double a2 = Math.floor((2 * Math.PI) / y);
        return a1 == a2;
    }

    /**
     * Calculate longitude or latitude of an aircraft
     *
     * @param zone of the aircraft
     * @param index_zone of the aircraft
     * @param a : previous or current latitude or longitude
     * @return the result of the calculation given different values
     */
    private static double longOrLatCalculator(double zone, double index_zone, double a) {
        return (1 / zone) * (index_zone + a);
    }

    /**
     * Center latitude and longitude around 0
     *
     * @param a : longitude or latitude of the aircraft
     * @return value of longitude or latitude after re-center at 0
     */
    private static double centerAt0(double a) {
        return a >= HALF_A_TURN ? a - 1 : a;
    }

    /**
     * Convert latitude or longitude to T32
     *
     * @param a : longitude or latitude
     * @return correspond value in T32
     */
    private static int toT32(double a) {
        return (int) Math.rint(Units.convert(a, Units.Angle.TURN, Units.Angle.T32));
    }

}
