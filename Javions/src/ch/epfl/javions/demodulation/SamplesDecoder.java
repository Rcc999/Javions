package ch.epfl.javions.demodulation;

import ch.epfl.javions.Preconditions;


import java.io.IOException;
import java.io.InputStream;

public final class SamplesDecoder {

    private final InputStream stream;
    private final int batchSize;
    private final byte[] buffer;

    public SamplesDecoder (InputStream stream, int batchSize){
        Preconditions.checkArgument(!(batchSize <= 0));
        if (stream == null){ throw new NullPointerException("Stream must not be null"); }
        this.stream =  stream;
        this.batchSize = batchSize;
        this.buffer = new byte[batchSize * 2];
    }

    public int readBatch(short[] batch) throws IOException {
        Preconditions.checkArgument(batch.length == batchSize);
        int bytesRead = stream.readNBytes(buffer, 0, buffer.length);

        if (bytesRead == -1) {
            return 0;
        }

        int samplesComputed = bytesRead / 2;

        for (int i = 0; i < bytesRead; i += 2) {
            for (int j = 0; j < bytesRead; j++) {
                short samples = (short) ((buffer[i] & 0xFF) | ((buffer[i + 1] & 0xF) << 8));
                samples -= 2048;
                batch[j] = samples;
            }
        }
        return samplesComputed;
    }

}
