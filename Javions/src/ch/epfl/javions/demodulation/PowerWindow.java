package ch.epfl.javions.demodulation;

import ch.epfl.javions.Preconditions;

import java.io.*;

/**
 * A window of fixed size on sequence of power samples
 *
 * @author Tuan Dang Nguyen (361089)
 * @author Rayane Charif Chefchouni (339839)
 */
public final class PowerWindow {

    private static final int BATCH_SIZE = (int) Math.pow(2, 16);
    private final int windowSize;
    private final PowerComputer powerComputer;
    private int numberOfSamples;
    private int[] firstLot;
    private int[] secondLot;
    private int actualWindowPosition;
    private int position;


    /**
     * Construct a window that superposes on power samples
     *
     * @param stream     :  the input flow
     * @param windowSize :  the size of the window
     * @throws IOException              when there is an error with input/output of the stream
     * @throws IllegalArgumentException if the size of the window is invalid
     */
    public PowerWindow(InputStream stream, int windowSize) throws IOException {
        Preconditions.checkArgument(windowSize > 0 && windowSize <= BATCH_SIZE);
        powerComputer = new PowerComputer(stream, BATCH_SIZE);
        this.windowSize = windowSize;
        this.firstLot = new int[BATCH_SIZE];
        this.numberOfSamples = powerComputer.readBatch(firstLot);
        this.secondLot = new int[BATCH_SIZE];
        this.actualWindowPosition = 0;
        this.position = 0;
    }

    /**
     * Get size of the window
     *
     * @return the window size
     */
    public int size() {
        return this.windowSize;
    }

    /**
     * Get the current position of the window
     *
     * @return the window position
     */
    public long position() {
        return this.position;
    }

    /**
     * Check if the window is full of samples
     *
     * @return :  a boolean value to see if the window is actually full of samples or not
     */
    public boolean isFull() {
        return this.position + windowSize <= numberOfSamples;
    }

    /**
     * Get a value at a given index in the window
     *
     * @param i : index
     * @return : the Powered Sample at the index i, given by the window
     * @throws IndexOutOfBoundsException if value of index i is bigger than window size
     */
    public int get(int i) {
        if (!(i >= 0 && i < windowSize)) {
            throw new IndexOutOfBoundsException("the index must be included in the correct interval");
        }

        return actualWindowPosition + i < firstLot.length
                ? firstLot[i + actualWindowPosition]
                : secondLot[actualWindowPosition + i - firstLot.length];
    }

    /**
     * Advance the window by one sample
     *
     * @throws IOException if there is an error in inputs/outputs of the stream
     */
    public void advance() throws IOException {
        this.position++;
        actualWindowPosition++;
        if (actualWindowPosition + windowSize - 1 == firstLot.length) {
            numberOfSamples += powerComputer.readBatch(secondLot);
        }
        if (actualWindowPosition == firstLot.length) {
            int[] temporary = secondLot;
            secondLot = firstLot;
            firstLot = temporary;
            actualWindowPosition = 0;
        }
    }

    /**
     * Advance the window given the offset
     *
     * @param offset : number chosen to advance the window by this number of samples
     * @throws IOException              if there is an error in inputs/outputs of the stream
     * @throws IllegalArgumentException if offset is negative or null
     */
    public void advanceBy(int offset) throws IOException {
        Preconditions.checkArgument(offset > 0);
        for (int i = 0; i < offset; i++) {
            advance();
        }
    }

}


