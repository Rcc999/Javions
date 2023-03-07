package ch.epfl.javions.demodulation;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class SamplesDecoderTest {

    @Test
    public void testReadBatch() throws IOException {
        byte[] testBits = {(byte) 0xFD, (byte) 0x07};
        InputStream stream = new ByteArrayInputStream(testBits);
        SamplesDecoder decoder = new SamplesDecoder(stream, 1);
        short[] batch = new short[1];
        decoder.readBatch(batch);
        assertEquals(-3, batch[0]);
    }
}
