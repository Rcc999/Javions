package ch.epfl.javions;

public class Math2 {
    private Math2(){}

    public static int clamp(int min, int v, int max){
        if(min > max){
            throw new IllegalArgumentException();
        }
        if(v <= min){
            return min;
        }
        return Math.min(v, max);
    }

    public static double asinh(double x){
        return Math.log(x + Math.sqrt(1+x*x));
    }
}
