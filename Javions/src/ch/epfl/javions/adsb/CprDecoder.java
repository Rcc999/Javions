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

        //Calculate les numéros de zones de latitude
        double nb_zone_lat = Math.rint(y0 * ZONE_LATITUDE1 - y1 * ZONE_LATITUDE0);

        if (nb_zone_lat < 0) {
            index_zone_lat0 = nb_zone_lat + ZONE_LATITUDE0;
            index_zone_lat1 = nb_zone_lat + ZONE_LATITUDE1;
        } else {
            index_zone_lat0 = nb_zone_lat;
            index_zone_lat1 = index_zone_lat0;
        }

        //Latitude in TURN
        double latitude0 = (1 / ZONE_LATITUDE0) * (index_zone_lat0 + y0);
        double latitude1 = (1 / ZONE_LATITUDE1) * (index_zone_lat1 + y1);

        //Calculate ZONE_LONGITUDE
        double B0 = calculateB(latitude0);
        double B1 = calculateB(latitude1);
        double A0 = Math.acos(1 - B0);
        double A1 = Math.acos(1 - B1);

        if (Double.isNaN(A0) && Double.isNaN(A1)){
            ZONE_LONGITUDE0 = 1;
        }
        else {
            if (!compare(A0, A1)){
                return null;
            }
            ZONE_LONGITUDE0 = Math.floor((2 * Math.PI) / A0);
        }
        ZONE_LONGITUDE1 = ZONE_LONGITUDE0 - 1;

        //Calculate longitude in TURN
        double longitude0;
        double longitude1;
        if (ZONE_LONGITUDE0 == 1) {
            longitude0 = x0;
            longitude1 = x1;
        } else {
            double index_zone_long = Math.rint(x0 * ZONE_LONGITUDE1 - x1 * ZONE_LONGITUDE0);
            if (index_zone_long < 0) {
                index_zone_long0 = index_zone_long + ZONE_LONGITUDE0;
                index_zone_long1 = index_zone_long + ZONE_LONGITUDE1;
            } else {
                index_zone_long0 = index_zone_long;
                index_zone_long1 = index_zone_long0;
            }
            longitude0 = (1 / ZONE_LONGITUDE0) * (index_zone_long0 + x0);
            longitude1 = (1 / ZONE_LONGITUDE1) * (index_zone_long1 + x0);
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

    private static double calculateB(double a){
        return (1 - Math.cos(2 * Math.PI * 1 / ZONE_LATITUDE0)) / Math.pow(Math.cos(Units.convert(a,Units.Angle.TURN ,Units.Angle.DEGREE)), 2);
    }

    private static boolean compare(double x, double y){
        double a1 = Math.floor((2 * Math.PI) / x);
        double a2 = Math.floor((2 * Math.PI) / y);
        return a1 == a2;
    }

    private static double centerAt0(double a){
        if(a >= 0.5){
            return a - 1;
        }
        return a;
    }

    /**
    public static void main(String[] args){
        GeoPos geoPos = decodePosition(Math.scalb(111600, -17) , Math.scalb(94445, -17) , Math.scalb(108865, -17) , Math.scalb(77558, -17) , 0);
        System.out.println(geoPos);
    }*/
}
