package ch.epfl.javions.demodulation;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.*;

public class SamplesDecoderTest {

    @Test
    public void testReadBatch() throws IOException {
        /*
        byte[] testBits = {(byte) 0xFD, (byte) 0x07};
        InputStream stream = new ByteArrayInputStream(testBits);
         */
        DataInputStream stream = new DataInputStream(
                new BufferedInputStream(
                        new FileInputStream(getClass().getResource("/samples.bin").getFile())));
        SamplesDecoder decoder = new SamplesDecoder(stream, 10);
        short[] batch = new short[10];
        decoder.readBatch(batch);
        for(short var : batch){
            System.out.println(var);
        }
        assertEquals(-3, batch[0]);
    }
}
