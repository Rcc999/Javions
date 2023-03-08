package ch.epfl.javions.demodulation;

import ch.epfl.javions.Preconditions;

import java.io.IOException;
import java.io.InputStream;

public final class PowerComputer {

    private SamplesDecoder samplesDecoder;
    private final InputStream stream;
    private final int batchSize;
    private short [] samplesContained;
    // Tableau stockant les huit derniers échantillons produits par la radio
    //Ce tableau doit être circulaire
    private int [] windowTable;


    public PowerComputer (InputStream stream, int batchSize){
        Preconditions.checkArgument(batchSize % Byte.SIZE == 0);
        Preconditions.checkArgument(batchSize > 0);
        this.stream = stream;
        this.batchSize = batchSize;
        this.samplesDecoder = new SamplesDecoder(stream, batchSize);
        this.samplesContained = new short[batchSize];
        this.windowTable = new int [Byte.SIZE];
    }


    public int readBatch(int [] batch) throws IOException {
        Preconditions.checkArgument(batch.length == batchSize);
        int d = 0;
        int numberSamples = samplesDecoder.readBatch(samplesContained);
        for (int i = 0; i < numberSamples; i++) {
            windowTable[i % 8] = samplesContained[i];
            if(i % 2 == 1){
                double f = Math.pow( windowTable[0] - windowTable[2] + windowTable[4] - windowTable[6], 2);
                double g = Math.pow( windowTable[1] - windowTable[3] + windowTable[5] - windowTable[7], 2);
                int P = (int) (f+g);
                batch[d] = P;
                ++d;
            }
        }
        return batch.length/2;
    }
}