package ch.epfl.javions.demodulation;

import ch.epfl.javions.adsb.RawMessage;

import java.io.IOException;
import java.io.InputStream;

public final class AdsbDemodulator {

    private final PowerWindow powerWindow;
    private final static  int WINDOW_SIZE = 1200;
    private int sumActualWindow = 0;
    private int v;
    private int sumNextWindow = 0;
    private int sumPreviousWindow = 0;
    private byte[] message;


    public AdsbDemodulator(InputStream samplesStream) throws IOException{
        powerWindow = new PowerWindow(samplesStream, WINDOW_SIZE);
    }

    public RawMessage nextMessage() throws IOException{

        while(powerWindow.isFull()) {
            //System.out.println("In loop");
            message = new byte[14];
            sumPreviousWindow = sumActualWindow;
            sumActualWindow = powerWindow.get(0) + powerWindow.get(10) + powerWindow.get(35) + powerWindow.get(45);
            sumNextWindow = powerWindow.get(1) + powerWindow.get(11) + powerWindow.get(36) + powerWindow.get(46);
            v = powerWindow.get(5) + powerWindow.get(15) + powerWindow.get(20) + powerWindow.get(25) + powerWindow.get(30) + powerWindow.get(40);
            int diff_1 = sumActualWindow - sumNextWindow;
            int diff_2 = sumActualWindow - sumPreviousWindow;

            // Check if the preamble is there with the different between the previous - actual and next - actual
            if((sumActualWindow >= 2 * v) && (diff_1 > 0) && (diff_2 > 0)){

                // Decode to get the message and put each byte (8 bits) in the table
                for(int i = 0; i < message.length; ++i){
                    int a = 0;
                    for(int j = 0; j < 8; ++j){
                        if(powerWindow.get(80 + 10 * (j + i * 8)) < powerWindow.get(85 + 10 * (j + i * 8))){
                            a = (a << 1); //tableau de byte, concartener chaque 8 bits
                            //System.out.println( "i = " + i + ", j = " + j  + " Binary = " + Integer.toBinaryString(a));
                        }else{
                            a = ((a << 1) | 1);
                            //System.out.println("i = " + i + ", j = " + j  + " Binary = " + Integer.toBinaryString(a));
                        }
                    }
                    message[i] = (byte) a;
                }

                RawMessage rawMessage = RawMessage.of(powerWindow.position() * 100, message);
                if((RawMessage.size(message[0]) == RawMessage.LENGTH) && rawMessage != null){
                    powerWindow.advanceBy(1200);
                    return rawMessage;
                }else{
                    powerWindow.advance();
                }

            }else{
                powerWindow.advance();
            }
        }
        return null;
    }
}
