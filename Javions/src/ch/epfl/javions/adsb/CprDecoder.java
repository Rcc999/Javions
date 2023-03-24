package ch.epfl.javions.adsb;

import ch.epfl.javions.GeoPos;
import ch.epfl.javions.Preconditions;
import ch.epfl.javions.Units;

public class CprDecoder {

    private CprDecoder() {}

    private static final double ZONE_LATITUDE0 = 60.0;
    private static final double ZONE_LATITUDE1 = 59.0;
    private static double ZONE_LONGITUDE0, ZONE_LONGITUDE1 = 0;
    private static double index_zone_lat0, index_zone_lat1 = 0;
    private static double index_zone_long0, index_zone_long1 = 0;

    public static GeoPos decodePosition(double x0, double y0, double x1, double y1, int mostRecent) {
        Preconditions.checkArgument(mostRecent == 1 || mostRecent == 0);

        //Normalize y0 and y1, x0 and x1
        double y0_normalized = normalized(y0);
        double y1_normalized = normalized(y1);
        double x0_normalized = normalized(x0);
        double x1_normalized = normalized(x1);

        //Calculate les numéros de zones de latitude
        double nb_zone_lat = Math.rint(y0_normalized * ZONE_LATITUDE1 - y1_normalized * ZONE_LATITUDE0);

        if (nb_zone_lat < 0) {
            index_zone_lat0 = nb_zone_lat + ZONE_LATITUDE0;
            index_zone_lat1 = nb_zone_lat + ZONE_LATITUDE1;
        } else {
            index_zone_lat0 = nb_zone_lat;
            index_zone_lat1 = index_zone_lat0;
        }

        //Latitude in TURN
        double latitude0 = (1 / ZONE_LATITUDE0) * (index_zone_lat0 + y0_normalized);
        double latitude1 = (1 / ZONE_LATITUDE1) * (index_zone_lat1 + y1_normalized);

        //Calculate ZONE_LONGITUDE
        double B = calculateB(latitude0);
        double A = Math.acos(1 - B);

        if (Double.isNaN(A)){
            ZONE_LONGITUDE0 = 1;
        }
        else {
            ZONE_LONGITUDE0 = Math.floor((2 * Math.PI) / A);
        }
        ZONE_LONGITUDE1 = ZONE_LONGITUDE0 - 1;

        //Calculate longitude in TURN
        double longitude0;
        double longitude1;
        if (ZONE_LONGITUDE0 == 1) {
            longitude0 = x0_normalized;
            longitude1 = x1_normalized;
        } else {
            double index_zone_long = Math.rint(x0_normalized * ZONE_LONGITUDE1 - x1_normalized * ZONE_LONGITUDE0);
            if (index_zone_long < 0) {
                index_zone_long0 = index_zone_long + ZONE_LONGITUDE0;
                index_zone_long1 = index_zone_long + ZONE_LONGITUDE1;
            } else {
                index_zone_long0 = index_zone_long;
                index_zone_long1 = index_zone_long0;
            }
            longitude0 = (1 / ZONE_LONGITUDE0) * (index_zone_long0 + x0_normalized);
            longitude1 = (1 / ZONE_LONGITUDE1) * (index_zone_long1 + x0_normalized);
        }

        //Center At 0
        latitude0 = centerAt0(latitude0);
        latitude1 = centerAt0(latitude1);
        longitude0 = centerAt0(longitude0);
        longitude1 = centerAt0(longitude1);

        int latitude0_T32 = (int) Math.rint(Units.convert(latitude0, Units.Angle.TURN, Units.Angle.T32));
        int latitude1_T32 = (int) Math.rint(Units.convert(latitude1, Units.Angle.TURN, Units.Angle.T32));
        int longitude0_T32 = (int) Math.rint(Units.convert(longitude0, Units.Angle.TURN, Units.Angle.T32));
        int longitude1_T32 = (int) Math.rint(Units.convert(longitude1, Units.Angle.TURN, Units.Angle.T32));

        //Return longitude and latitude depend on most recent (even or odd)
        if (mostRecent == 1) {
            if (!GeoPos.isValidLatitudeT32(latitude1_T32)) return null;
            else return new GeoPos(longitude1_T32, latitude1_T32);
        } else {
            if (!GeoPos.isValidLatitudeT32(latitude0_T32)) return null;
            else return new GeoPos(longitude0_T32, latitude0_T32);
        }
    }

    private static double normalized(double a){
        return Math.scalb(a, -17);
    }

    private static double calculateB(double a){
        return (1 - Math.cos(2 * Math.PI * 1 / ZONE_LATITUDE0)) / Math.pow(Math.cos(Units.convert(a,Units.Angle.TURN ,Units.Angle.DEGREE)), 2);
    }

    private static double centerAt0(double a){
        if(a >= 0.5){
            return a - 1;
        }
        return a;
    }

    public static void main(String[] args){
        GeoPos geoPos = decodePosition(111600, 94445, 108865, 77558, 0);
        System.out.println(geoPos);
    }
}
