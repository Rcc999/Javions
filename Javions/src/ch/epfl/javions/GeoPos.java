package ch.epfl.javions;

public record GeoPos(int longitudeT32, int latitudeT32) {
    public GeoPos {
        if(!isValidLatitudeT32(latitudeT32)){
            throw new IllegalArgumentException();
        }
    }

    public static boolean isValidLatitudeT32(int latitudeT32){
        return latitudeT32 >= Math.scalb(-1, 30) && latitudeT32 <= Math.scalb(1, 30);
    }

    public double longitude(){
        return Units.convertTo(longitudeT32, Units.Angle.RADIAN);
    }

    public double latitude() {
        return Units.convertTo(latitudeT32, Units.Angle.RADIAN);
    }

    // To check
    @Override
    public String toString() {
        return Units.convertTo(longitudeT32, Units.Angle.DEGREE)+", "+ Units.convertTo(latitudeT32, Units.Angle.DEGREE);
    }
}
