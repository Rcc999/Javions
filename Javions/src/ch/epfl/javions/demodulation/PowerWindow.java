package ch.epfl.javions.demodulation;

import ch.epfl.javions.Preconditions;
import com.sun.source.tree.UsesTree;

import java.io.IOException;
import java.io.InputStream;

public final class PowerWindow {

    private final int windowSize;
    private PowerComputer powerComputer;
    int numberOfSamples;
    private int [] window;
    private int [] firstLot;
    private int []  secondLot;
    private int actualWindowPosition;
    private int actualFirstLotIndex;
    private int actualSecondLotIndex;


    public PowerWindow(InputStream stream, int windowSize) throws IOException{
        Preconditions.checkArgument(windowSize > 0 && windowSize <= Math.pow(2, 16));
        powerComputer = new PowerComputer(stream, (int)Math.pow(2, 16));
        this.windowSize = windowSize;
        this.numberOfSamples = powerComputer.readBatch(firstLot);
        this.secondLot =  new int[(int)Math.pow(2,16)];
        this.window = new int[windowSize];
        this.actualWindowPosition = 0;
        this.actualFirstLotIndex = 0;
        this.actualSecondLotIndex = 0;
    }


    public int size () {return windowSize; }

    public long position() { return actualWindowPosition - actualFirstLotIndex; }

    public boolean isFull() {

        return windowSize == numberOfSamples;
    }

    //Doit-on retourner the actual position du premierLot et laisser la méthode advanceBY changer de tableau,
    // en fonction of the advancement de la window and the incrementation of the index i
    //Revoir Schéma de ed pour mieux comprendre
    public int get(int i){
        if(i >= 0 && i < Math.pow(2,16)){throw new IndexOutOfBoundsException("the index must be included in the correct interval");}
        i = actualWindowPosition;
        return firstLot[i];
    }


    public void advance() throws IOException {
        advanceBy(1);
    }

    public void advanceBy(int offset) throws IOException{
        Preconditions.checkArgument(offset > 0);
        actualFirstLotIndex += offset;
        window = new int[actualFirstLotIndex];
    }


}


