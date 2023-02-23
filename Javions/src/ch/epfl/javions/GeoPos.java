package ch.epfl.javions;

public record GeoPos(int longitudeT32, int latitudeT32) {

    public GeoPos {
        Preconditions.checkArgument(isValidLatitudeT32(latitudeT32));
    }

    /**
     *
     * @param latitudeT32: type int, value of latitude
     * @return check if it is valid or not
     */
    public static boolean isValidLatitudeT32(int latitudeT32){
        return latitudeT32 >= (int) Math.scalb(-1, 30) && latitudeT32 <= (int) Math.scalb(1, 30);
    }

    /**
     *
     * @return longitude in radian
     */
    public double longitude(){
        return Units.convert(longitudeT32, Units.Angle.T32, Units.Angle.RADIAN);
    }

    /**
     *
     * @return latitude in radian
     */
    public double latitude() {
        return Units.convert(latitudeT32, Units.Angle.T32, Units.Angle.RADIAN);
    }

    /**
     *
     * @return a string message of value of longitude and latitude, each in degree
     */
    // To check
    @Override
    public String toString() {
        return "("+Units.convert(longitudeT32,Units.Angle.T32 ,Units.Angle.DEGREE)+"\u00B0, "+ Units.convert(latitudeT32,Units.Angle.T32 ,Units.Angle.DEGREE)+"\u00B0)";
    }
}
