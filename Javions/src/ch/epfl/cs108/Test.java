package ch.epfl.cs108;

public class Test {
    public static void main(String[] args){
        int a = (1 << 30) ;
        System.out.println(a);
        int b = (int) Math.scalb(1, 30);
        System.out.println(b);
        System.out.println(isValidLatitudeT32(a));
        System.out.println(a <= b);

        System.out.println(Integer.MIN_VALUE);

        //1.5707963267948966
        //1.5707963267948966
    }
    public static boolean isValidLatitudeT32(int latitudeT32){
        return (latitudeT32 >= (int) Math.scalb(-1, 30)) && (latitudeT32 <= (int) Math.scalb(1, 30));
    }
}


