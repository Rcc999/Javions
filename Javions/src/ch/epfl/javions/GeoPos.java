package ch.epfl.javions;

public record GeoPos(int longitudeT32, int latitudeT32) {
    public GeoPos {
        if(!isValidLatitudeT32(latitudeT32)){
            throw new IllegalArgumentException();
        }
    }

    /**
     *
     * @param latitudeT32: type int, value of latitude
     * @return check if it is valid or not
     */
    public static boolean isValidLatitudeT32(int latitudeT32){
        return latitudeT32 >= Math.scalb(-1, 30) && latitudeT32 <= Math.scalb(1, 30);
    }

    /**
     *
     * @return longitude in radian
     */
    public double longitude(){
        return Units.convertTo(longitudeT32, Units.Angle.RADIAN);
    }

    /**
     *
     * @return latitude in radian
     */
    public double latitude() {
        return Units.convertTo(latitudeT32, Units.Angle.RADIAN);
    }

    /**
     *
     * @return a string message of value of longitude and latitude, each in degree
     */
    // To check
    @Override
    public String toString() {
        return Units.convertTo(longitudeT32, Units.Angle.DEGREE)+", "+ Units.convertTo(latitudeT32, Units.Angle.DEGREE);
    }
}
