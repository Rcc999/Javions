package ch.epfl.javions.demodulation;

import ch.epfl.javions.adsb.RawMessage;
import java.io.IOException;
import java.io.InputStream;

/**
 * Demodulation of ADS-B message
 *
 * @author Tuan Dang Nguyen (361089)
 * @author Rayane Charif Chefchouni (339839)
 */
public final class AdsbDemodulator {

    private final PowerWindow powerWindow;
    private final static  int WINDOW_SIZE = 1200;
    private int sumActualWindow = 0;

    /**
     * Construct a window over the samples data
     *
     * @param samplesStream contains samples data
     * @throws IOException if there is an input/output error from the samples stream
     */
    public AdsbDemodulator(InputStream samplesStream) throws IOException{
        powerWindow = new PowerWindow(samplesStream, WINDOW_SIZE);
    }

    /**
     * Return a raw ADS-B message from samples data
     *
     * @return valid raw message obtained after demodulating the samples data
     * @throws IOException if there is an input/output error from the samples stream
     */
    public RawMessage nextMessage() throws IOException{

        while(powerWindow.isFull()) {
            byte[] message = new byte[14];
            int sumPreviousWindow = sumActualWindow;
            sumActualWindow = powerWindow.get(0) + powerWindow.get(10) + powerWindow.get(35) + powerWindow.get(45);
            int sumNextWindow = powerWindow.get(1) + powerWindow.get(11) + powerWindow.get(36) + powerWindow.get(46);
            int v = powerWindow.get(5) + powerWindow.get(15) + powerWindow.get(20) + powerWindow.get(25) + powerWindow.get(30) + powerWindow.get(40);
            int diff_2 = sumActualWindow - sumNextWindow;
            int diff_1 = sumActualWindow - sumPreviousWindow;

            if((diff_1 > 0) && (diff_2 > 0) && (sumActualWindow >= 2 * v) ){
                // Decode to get the message and put each byte (8 bits) in the table
                for(int i = 0; i < message.length; ++i){
                    int a = 0;
                    for(int j = 0; j < 8; ++j){
                        if(powerWindow.get(80 + 10 * (j + i * 8)) < powerWindow.get(85 + 10 * (j + i * 8))){
                            a = (a << 1);
                        }else{
                            a = ((a << 1) | 1);
                        }
                    }
                    message[i] = (byte) a;
                }
                RawMessage rawMessage = RawMessage.of(powerWindow.position() * 100, message);

                if((RawMessage.size(message[0]) == RawMessage.LENGTH) && rawMessage != null){
                    powerWindow.advanceBy(WINDOW_SIZE);
                    return rawMessage;
                } else powerWindow.advance();
            } else powerWindow.advance();
        }
        return null;
    }
}
