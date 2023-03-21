package ch.epfl.javions;

public final class Units {

    /**
     * Constructor: private - non instantiable
     */
    private Units() {}

    /**
     * Constants
     */
    public static final double CENTI = 1e-2;
    public static final double KILO = 1e3;


    /**
     * Nested Class: Angle
     * It contains constants: RADIAN, TURN, DEGREE, T32
     */
    public static class Angle{
        private Angle() {}
        public static final double RADIAN = 1.0;
        public static final double TURN = 2*Math.PI* RADIAN;
        public static final double DEGREE = TURN/360;
        public static final double T32 = TURN/Math.scalb(1,32);
    }

    /**
     * Nested Class: Length
     * It contains constants: METER, CENTIMETER, KILOMETER, INCH, FOOT, NAUTICAL_MILE
     */
    public static class Length{
        private Length() {}
        public static final double METER = 1;
        public static final double CENTIMETER = CENTI * METER;
        public static final double KILOMETER = KILO * METER;
        public static final double INCH = 2.54 * CENTIMETER;
        public static final double FOOT = 12 * INCH;
        public static final double NAUTICAL_MILE = 1852 * METER;
    }

    /**
     * Nested Class: Time
     * It contains constants: SECOND, MINUTE, HOUR
     */
    public static class Time{
        private Time() {}
        public static final double SECOND = 1;
        public static final double MINUTE = 60 * SECOND;
        public static final double HOUR = 60 * MINUTE;
    }

    /**
     * Nested Class: Speed
     * It contains constants: KNOT, KILOMETER_PER_HOUR
     */
    public static class Speed{
        private Speed() {}
        public static final double KNOT = Length.NAUTICAL_MILE/Time.HOUR;
        public static final double KILOMETER_PER_HOUR = Length.KILOMETER/Time.HOUR;
    }

    /**
     *
     * @param value: type double, a value that will be converted from one to another unit
     * @param fromUnit: initial unit
     * @param toUnit: value converted to unit
     * @return value in the another unit
     */
    public static double convert(double value, double fromUnit, double toUnit){
        return value*(fromUnit/toUnit);
    }

    /**
     *
     * @param value: type double, a value that will be converted from one to base unit
     * @param fromUnit: initial unit
     * @return value in fromUnit
     */
    public static double convertFrom(double value, double fromUnit){
        return value*fromUnit;
    }

    /**
     *
     * @param value:  type double, a value that will be converted from one to another unit
     * @param toUnit: final unit
     * @return value in toUnit
     */
    public static double convertTo(double value, double toUnit){
        return value*(1/toUnit);
    }

}
