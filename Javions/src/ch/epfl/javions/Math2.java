package ch.epfl.javions;

public final class Math2 {
    /**
     * Constructor: private - non instantiable
     */
    private Math2(){}

    /**
     *
     * @param min: minimum value
     * @param v: a value
     * @param max: maximum value
     * @return min if v <= min or max if v >= max or v if min < v < max
     */
    public static int clamp(int min, int v, int max){

        Preconditions.checkArgument(!(min > max));

        if(v <= min){
            return min;
        }
        return Math.min(v, max);
    }

    /**
     *
     * @param x: a value (normally will be converted to Radian)
     * @return value of arsinh x
     */
    public static double asinh(double x){
        return Math.log(x + Math.sqrt(1+x*x));
    }
}
