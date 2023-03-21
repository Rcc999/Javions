package ch.epfl.javions.demodulation;

import org.junit.jupiter.api.Test;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PowerComputerTestPersonal {

    @Test
    void readBatchValueTest() throws IOException {
        DataInputStream stream = new DataInputStream(
                new BufferedInputStream(
                        new FileInputStream(getClass().getResource("/samples.bin").getFile())));

        PowerComputer powerComputer = new PowerComputer(stream, (int) Math.pow(2,16) );
        int[] a = new int[ (int) Math.pow(2,16)];

        int b = powerComputer.readBatch(a);

        System.out.println("The number of elements in a is: " + b);

        for(int var: a){
            System.out.println(var);
        }

        assertEquals(73, a[0]);
    }

}
