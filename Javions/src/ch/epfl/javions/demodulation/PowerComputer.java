package ch.epfl.javions.demodulation;

import ch.epfl.javions.Preconditions;

import java.io.IOException;
import java.io.InputStream;

public final class PowerComputer {

    private SamplesDecoder samplesDecoder;
    private final InputStream stream;
    private final int batchSize;
    private short [] samplesContained;
    private int [] windowTable;


    public PowerComputer (InputStream stream, int batchSize){
        Preconditions.checkArgument(batchSize % Byte.SIZE == 0);
        Preconditions.checkArgument(batchSize > 0);
        this.stream = stream;
        this.batchSize = batchSize;
        this.samplesDecoder = new SamplesDecoder(stream, batchSize);
        this.windowTable = new int [Byte.SIZE];
    }


    public int readBatch(int [] batch) throws IOException {
        Preconditions.checkArgument(batch.length == batchSize);
        int c = samplesDecoder.readBatch( samplesContained);
        for (int i = 0; i < c; i++) {

        }
        return 0;
    }
}
