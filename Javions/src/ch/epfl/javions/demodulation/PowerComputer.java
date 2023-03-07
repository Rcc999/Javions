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
        int numberSamples = samplesDecoder.readBatch( samplesContained);
        for (int i = 0; i < numberSamples; i++) {

        }
        return 0;
    }
}
