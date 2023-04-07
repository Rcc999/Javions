package ch.epfl.javions.demodulation;

import ch.epfl.javions.Preconditions;


import java.io.IOException;
import java.io.InputStream;

/**
 * Read the samples of an input stream and convert them into power samples
 *
 * @author Tuan Dang Nguyen (361089)
 * @author Rayane Charif Chefchouni (339839)
 */
public final class SamplesDecoder {

    private final InputStream stream;
    private final int batchSize;
    private final byte[] buffer;
    private final int RECENTERING_SUBSTRACTION = 2048;

    /**
     * Public constructor of SamplesDecoder, it takes a stream and a batch size
     *
     * @param stream    : stream to read from
     * @param batchSize : number of samples to read
     */
    public SamplesDecoder(InputStream stream, int batchSize) {
        Preconditions.checkArgument(batchSize > 0);
        if (stream == null) {
            throw new NullPointerException("Stream must not be null");
        }
        this.stream = stream;
        this.batchSize = batchSize;
        this.buffer = new byte[batchSize * 2];
    }

    /**
     * Reads a batch of samples from the stream and convert it into Power Samples
     *
     * @param batch : the array to fill with the samples
     * @return :  the number of samples read
     * @throws IOException              : if the stream is closed
     * @throws IllegalArgumentException : if the batch size is not equal to the batch size of the decoder
     */
    public int readBatch(short[] batch) throws IOException {
        Preconditions.checkArgument(batch.length == batchSize);
        int bytesRead = stream.readNBytes(buffer, 0, buffer.length);
        int samplesComputed = bytesRead / 2;
        for (int i = 0, j = 0; i < bytesRead; i += 2, j++) {
            short sample = (short) ((buffer[i] & 0xFF) | ((buffer[i + 1] & 0xF) << Byte.SIZE));
            sample -= RECENTERING_SUBSTRACTION;
            batch[j] = sample;
        }
        return samplesComputed;
    }
}
