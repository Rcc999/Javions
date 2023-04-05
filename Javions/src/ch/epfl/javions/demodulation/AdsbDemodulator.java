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

    private final static int WINDOW_SIZE = 1200;
    private final static int SIZE_DECODED_TABLE = 14;
    private final static int VALUE_INDEX_80 = 80;
    private final static int VALUE_INDEX_85 = 85;
    private final PowerWindow powerWindow;
    private int sumActualWindow = 0;

    /**
     * Construct a window over the samples data
     *
     * @param samplesStream contains samples data
     * @throws IOException if there is an input/output error from the samples stream
     */
    public AdsbDemodulator(InputStream samplesStream) throws IOException {
        powerWindow = new PowerWindow(samplesStream, WINDOW_SIZE);
    }

    /**
     * Return a raw ADS-B message from samples data
     *
     * @return valid raw message obtained after demodulating the samples data
     * @throws IOException if there is an input/output error from the samples stream
     */
    public RawMessage nextMessage() throws IOException {

        while (powerWindow.isFull()) {

            if (checkComparisonCondition()) {
                byte[] message = decodeMessage();
                RawMessage rawMessage = RawMessage.of(powerWindow.position() * 100, message);

                if ((RawMessage.size(message[0]) == RawMessage.LENGTH) && rawMessage != null) {
                    powerWindow.advanceBy(WINDOW_SIZE);
                    return rawMessage;
                } else powerWindow.advance();

            } else powerWindow.advance();
        }
        return null;
    }

    /**
     * Check relation between current sum and v, previous, next sum
     *
     * @return true if the conditions are satisfied
     */
    private boolean checkComparisonCondition() {
        int sumPreviousWindow = sumActualWindow;
        sumActualWindow = powerWindow.get(0) + powerWindow.get(10) + powerWindow.get(35) + powerWindow.get(45);
        int sumNextWindow = powerWindow.get(1) + powerWindow.get(11) + powerWindow.get(36) + powerWindow.get(46);
        int v = powerWindow.get(5) + powerWindow.get(15) + powerWindow.get(20) + powerWindow.get(25) + powerWindow.get(30) + powerWindow.get(40);
        return sumActualWindow > sumNextWindow && sumActualWindow > sumPreviousWindow && sumActualWindow >= 2 * v;
    }

    /**
     * Decode the message
     *
     * @return byte table, each slot contains a decoded byte
     */
    private byte[] decodeMessage() {
        byte[] decoded = new byte[SIZE_DECODED_TABLE];

        for (int i = 0; i < decoded.length; ++i) {
            byte decodedBit = 0;
            for (int j = 0; j < Byte.SIZE; ++j) {
                decodedBit = (byte) (checkBetween8085(j, i) ? (decodedBit << 1) : ((decodedBit << 1) | 1));
            }
            decoded[i] = decodedBit;
        }
        return decoded;
    }

    /**
     * Check value between 2 indexes
     *
     * @return true if value at index 80 < value at index 85 of the window
     */
    private boolean checkBetween8085(int j, int i) {
        return powerWindow.get(VALUE_INDEX_80 + 10 * (j + i * Byte.SIZE)) < powerWindow.get(VALUE_INDEX_85 + 10 * (j + i * Byte.SIZE));
    }
}
