package ch.epfl.javions.demodulation;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.*;

public class SamplesDecoderTestPersonal {

    @Test
    public void testReadBatch() throws IOException {
        /*
        byte[] testBits = {(byte) 0xFD, (byte) 0x07};
        InputStream stream = new ByteArrayInputStream(testBits);
         */
        DataInputStream stream = new DataInputStream(
                new BufferedInputStream(
                        new FileInputStream(getClass().getResource("/samples.bin").getFile())));
        SamplesDecoder decoder = new SamplesDecoder(stream, 16);
        short[] batch = new short[16];
        int a = decoder.readBatch(batch);
        System.out.println("a = " + a);
        for(short var : batch){
            System.out.println(var);
        }
        assertEquals(-3, batch[0]);
    }
}
