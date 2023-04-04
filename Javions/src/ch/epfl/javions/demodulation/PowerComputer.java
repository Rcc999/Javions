package ch.epfl.javions.demodulation;

import ch.epfl.javions.Preconditions;

import java.io.IOException;
import java.io.InputStream;

/**
 * Power calculator
 *
 * @author Tuan Dang Nguyen (361089)
 * @author Rayane Charif Chefchouni (339839)
 */
public final class PowerComputer {

    private final SamplesDecoder samplesDecoder;
    private final int batchSize;
    private final short[] samplesContained;
    private final int[] windowTable;


    /**
     * Construct a table of power samples from the power calculation of samples data
     *
     * @param stream:    the stream input of binary numbers
     * @param batchSize: the size of portion that we are going to divide the stream into after the calculation
     * @throws IllegalArgumentException if batch size is not multiple of 8 or batch size is not strictly bigger than 0
     */
    public PowerComputer(InputStream stream, int batchSize) {
        Preconditions.checkArgument(batchSize % Byte.SIZE == 0);
        Preconditions.checkArgument(batchSize > 0);
        this.batchSize = batchSize;
        this.samplesDecoder = new SamplesDecoder(stream, batchSize * 2);
        this.samplesContained = new short[batchSize * 2];
        this.windowTable = new int[Byte.SIZE];
    }

    /**
     * Fill the table with power samples and return number of samples placed in the table
     *
     * @param batch: the table in which will be filled
     * @return the number of samples whose power is calculated
     * @throws IOException              if inputs or outputs have problems
     * @throws IllegalArgumentException if the length of
     */
    public int readBatch(int[] batch) throws IOException {
        Preconditions.checkArgument(batch.length == batchSize);
        int d = 0;
        int numberSamples = samplesDecoder.readBatch(samplesContained);
        for (int i = 0; i < numberSamples; i++) {
            windowTable[i % 8] = samplesContained[i];
            if (i % 2 == 1) {
                double f = Math.pow(windowTable[0] - windowTable[2] + windowTable[4] - windowTable[6], 2);
                double g = Math.pow(windowTable[1] - windowTable[3] + windowTable[5] - windowTable[7], 2);
                int P = (int) (f + g);
                batch[d] = P;
                ++d;
            }
        }
        return numberSamples / 2;
    }
}
