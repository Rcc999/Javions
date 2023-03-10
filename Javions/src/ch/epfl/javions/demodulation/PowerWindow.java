package ch.epfl.javions.demodulation;

import ch.epfl.javions.Preconditions;
import com.sun.source.tree.UsesTree;

import java.io.IOException;
import java.io.InputStream;

public final class PowerWindow {

    private final int windowSize;
    private PowerComputer powerComputer;
    private int numberOfSamples;
    private int [] window;
    private int [] firstLot;
    private int []  secondLot;
    private int actualWindowPosition;
    private int position;



    public PowerWindow(InputStream stream, int windowSize) throws IOException{
        Preconditions.checkArgument(windowSize > 0 && windowSize <= Math.pow(2, 16));
        powerComputer = new PowerComputer(stream, (int)Math.pow(2, 16));
        this.windowSize = windowSize;
        this.numberOfSamples = powerComputer.readBatch(firstLot);
        this.secondLot =  new int[(int)Math.pow(2,16)];
        this.window = new int[windowSize];
        this.actualWindowPosition = 0;
        this.position = 0;
    }

    public int size () { return this.windowSize; }

    public long position() { return this.position; }

    public boolean isFull() {
        return numberOfSamples + windowSize <= numberOfSamples;
    }

    public int get(int i){
        if(i >= 0 && i < windowSize){throw new IndexOutOfBoundsException("the index must be included in the correct interval");}
        if(actualWindowPosition + i <= firstLot.length){
            return firstLot[i];
        } else {
            return secondLot[actualWindowPosition + i - firstLot.length];
        }
        }

    public void advance() throws IOException {
        position++;
        actualWindowPosition++;
        if(actualWindowPosition + windowSize - 1 == firstLot.length){
            powerComputer.readBatch(secondLot);
        }
        if(actualWindowPosition == firstLot.length) {
            int[] temporary = secondLot;
            secondLot = firstLot;
            firstLot = temporary;
        }
    }

    public void advanceBy(int offset) throws IOException{
        Preconditions.checkArgument(offset > 0);
        for (int i = 0; i < offset; i++) {
            advance();
        }
    }


}


