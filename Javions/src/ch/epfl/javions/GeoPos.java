package ch.epfl.javions;

/**
 * Coordinates Geographic - longitude and latitude
 *
 * @author Tuan Dang Nguyen (361089)
 * @author Rayane Charif Chefchouni (339839)
 */
public record GeoPos(int longitudeT32, int latitudeT32) {

    /**
     * Constructs a geographic position (containing longitude and latitude)
     *
     * @param longitudeT32 of an object
     * @param latitudeT32  of an object (must be between -90 and +90 degree)
     * @throws IllegalArgumentException if latitude in T32 is invalid
     */
    public GeoPos {
        Preconditions.checkArgument(isValidLatitudeT32(latitudeT32));
    }

    /**
     * Check if latitude is between -90 and +90 degree
     *
     * @param latitudeT32: type int, value of latitude
     * @return check if it is valid or not
     */
    public static boolean isValidLatitudeT32(int latitudeT32) {
        return latitudeT32 >= (int) Math.scalb(-1, 30) && latitudeT32 <= (int) Math.scalb(1, 30);
    }

    /**
     * Get longitude in radian
     *
     * @return longitude in radian
     */
    public double longitude() {
        return Units.convert(longitudeT32, Units.Angle.T32, Units.Angle.RADIAN);
    }

    /**
     * Get latitude in radian
     *
     * @return latitude in radian
     */
    public double latitude() {
        return Units.convert(latitudeT32, Units.Angle.T32, Units.Angle.RADIAN);
    }

    /**
     * Print out the string contains information about longitude and latitude in degree
     *
     * @return a string message of value of longitude and latitude, each in degree
     */
    @Override
    public String toString() {
        return "(" + Units.convert(longitudeT32, Units.Angle.T32, Units.Angle.DEGREE) + "\u00B0, " + Units.convert(latitudeT32, Units.Angle.T32, Units.Angle.DEGREE) + "\u00B0)";
    }
}
