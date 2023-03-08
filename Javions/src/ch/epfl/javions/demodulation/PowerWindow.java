package ch.epfl.javions.demodulation;

import ch.epfl.javions.Preconditions;

import java.io.IOException;
import java.io.InputStream;

public final class PowerWindow {

    private int windowSize;
    private PowerComputer powerComputer;
    private int [] firstLot;
    private int []  secondLot;
    private int actualWindowPosition;
    private int actualFirstLotIndex;
    private int actualSecondLotIndex;


    public PowerWindow(InputStream stream, int windowSize) throws IOException{
        Preconditions.checkArgument(windowSize > 0 || windowSize <= Math.pow(2, 16));
        powerComputer = new PowerComputer(stream, (int)Math.pow(2, 16));
        this.windowSize = windowSize;
        //this.firstLot = powerComputer.readBatch();
        this.secondLot =  new int[(int)Math.pow(2,16)];
        this.actualWindowPosition = 0;
        this.actualFirstLotIndex = 0;
        this.actualSecondLotIndex = 0;
    }


    public int size () {return windowSize; }

    public long position() { return actualWindowPosition - actualFirstLotIndex; }

    public boolean isFull() {
        return firstLot.length == windowSize && secondLot.length == windowSize;
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
        //Not sure about that possibility of using advanceBy in advance
        //advanceBy(1);
        actualWindowPosition++;
    }

    public void advanceBy(int offset) throws IOException{
        Preconditions.checkArgument(offset >= 0);

    }


}


