package ch.epfl.javions;

/**
 * Some mathematics calculations
 *
 * @author Tuan Dang Nguyen (361089)
 * @author Rayane Charif Chefchouni (339839)
 */
public final class Math2 {

    /**
     * Constructor: private - non instantiable
     */
    private Math2(){}

    /**
     * Choosing a minimum value between 3 given values
     *
     * @param min: minimum value
     * @param v: a value
     * @param max: maximum value
     * @return min if v <= min or max if v >= max or v if min < v < max
     * @throws IllegalArgumentException if min value is bigger than max value
     */
    public static int clamp(int min, int v, int max){
        Preconditions.checkArgument(!(min > max));
        return v <= min ? min : Math.min(v, max);
    }

    /**
     * Calculate asinh of a value x
     *
     * @param x: a value (normally will be converted to Radian)
     * @return value of asinh x
     */
    public static double asinh(double x){
        return Math.log(x + Math.sqrt(1+x*x));
    }
}
