package ch.epfl.javions.demodulation;

import ch.epfl.javions.Preconditions;


import java.io.IOException;
import java.io.InputStream;

public final class SamplesDecoder {

    private final InputStream stream;
    private final int batchSize;
    private final byte[] buffer;

    /**
     *Public constructor of SamplesDecoder
     * @param stream : input stream
     * @param batchSize : size of the batch to produce
     */
    public SamplesDecoder (InputStream stream, int batchSize){
        Preconditions.checkArgument(batchSize > 0);
        if (stream == null){ throw new NullPointerException("Stream must not be null"); }
        this.stream =  stream;
        this.batchSize = batchSize;
        this.buffer = new byte[batchSize * 2];
    }

    /**
     *
     * @param batch : table containing the converted samples
     * @return :  the number of converted samples
     * @throws IOException :  to close the flow
     */
    public int readBatch(short[] batch) throws IOException {
        Preconditions.checkArgument(batch.length == batchSize);
        int bytesRead = stream.readNBytes(buffer, 0, buffer.length);
        int samplesComputed = bytesRead / 2;
        for (int i = 0, j = 0; i < bytesRead; i += 2, j++) {
            short sample = (short) ((buffer[i] & 0xFF) | ((buffer[i + 1] & 0xF) << 8));
            sample -= 2048;
            batch[j] = sample;
        }
        return samplesComputed;
    }

}
