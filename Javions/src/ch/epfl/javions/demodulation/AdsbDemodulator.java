package ch.epfl.javions.demodulation;

import ch.epfl.javions.adsb.RawMessage;
import ch.epfl.javions.demodulation.PowerWindow;

import java.io.IOException;
import java.io.InputStream;

public final class AdsbDemodulator {

    private final PowerWindow powerWindow;
    private final static  int WINDOW_SIZE =  1200;
    private long timeStampNs;
    private RawMessage rawMessage;
    private int sumOFActualWindow;
    private int v;
    private int sumOfNextPositionWindow;
    private int sumOfPreviousPositionWindow;

    public AdsbDemodulator(InputStream samplesStream) throws IOException{
        powerWindow = new PowerWindow(samplesStream, WINDOW_SIZE);
        this.timeStampNs = 0;
    }

    public RawMessage nextMessage() throws IOException{
        //Start of th message
        while(powerWindow.isFull()) {
            sumOFActualWindow = powerWindow.get(0) + powerWindow.get(10) + powerWindow.get(35) + powerWindow.get(45);
            v = powerWindow.get(5) + powerWindow.get(15) + powerWindow.get(20) + powerWindow.get(25) + powerWindow.get(30) + powerWindow.get(40);
            powerWindow.advance();
            sumOfPreviousPositionWindow = sumOFActualWindow;
            sumOFActualWindow = powerWindow.get(0) + powerWindow.get(10) + powerWindow.get(35) + powerWindow.get(45);


        }
        if (sumOFActualWindow >= 2 * v){

        }

        return null;
    }

}
