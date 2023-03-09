package ch.epfl.javions.demodulation;

import org.junit.jupiter.api.Test;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PowerComputerTest {

    @Test
    void readBatchValueTest() throws IOException {
        DataInputStream stream = new DataInputStream(
                new BufferedInputStream(
                        new FileInputStream(getClass().getResource("/samples.bin").getFile())));

        PowerComputer powerComputer = new PowerComputer(stream, 16);
        int[] a = new int[16];

        int b = powerComputer.readBatch(a);

        System.out.println("The number of elements in a is: " + b);

        for(int var: a){
            System.out.println(var);
        }

        assertEquals(73, a[0]);
    }

    public static void main(String[] args){

        short[] a = {-3, 8, -9, -8, -5, -8, -12, -16, -23, -9, -17, -66, 6, 37, -21, -65};
        int[] b = new int[8];
        int[] c = new int[8];
        int d = 0;

        for(int i = 0; i < a.length; ++i){
            b[i % 8] = a[i];
            if(i % 2 == 1){
                double f = Math.pow( b[0] - b[2] + b[4] - b[6], 2);
                double g = Math.pow( b[1] - b[3] + b[5] - b[7], 2);
                int P = (int) (f+g);
                c[d] = P;
                ++d;
            }
        }

        for(int var : c){
            System.out.println(var);
        }

        //Calculating n = 0
        double e =  Math.pow((-b[0]), 2);
        double f = Math.pow(-b[1], 2);
        int P0 = (int) (f+e);
        System.out.println(P0);

        //Calculating n = 3
        double g = Math.pow( b[0] - b[2] + b[4] - b[6], 2);
        double h = Math.pow( b[1] - b[3] + b[5] - b[7], 2);
        int P3 = (int) (h+g);
        System.out.println(P3);

        //Calculating n = 4 (remove b[0] and b[1])
        double i = Math.pow( b[2] - b[4] + b[6] - b[8], 2);
        double j = Math.pow( b[3] - b[5] + b[7] - b[9], 2);
        int P4 = (int) (j+i);
        System.out.println(P4);

        //Calculating n = 5 (remove b[2] and b[3])
        double k = Math.pow( (b[4] - b[6] + b[8] - b[10]) ,2);
        double l = Math.pow( (b[5] - b[7] + b[9] - b[11]) ,2);
        int P5 = (int) (l+k);
        System.out.println(P5);

    }

}
