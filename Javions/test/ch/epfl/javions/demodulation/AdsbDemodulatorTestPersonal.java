package ch.epfl.javions.demodulation;

import ch.epfl.javions.adsb.RawMessage;

import java.io.*;
import java.util.Objects;

public class AdsbDemodulatorTestPersonal {
    public static void main(String[] args) throws IOException {

        String file = AdsbDemodulatorTestPersonal.class.getResource("/samples_20230304_1442.bin").getFile();
        try (InputStream stream = new FileInputStream(file)) {
            AdsbDemodulator demodulator = new AdsbDemodulator(stream);
            RawMessage message;
            int length = 0;
            while ((message = demodulator.nextMessage()) != null) {
                System.out.print(message);
                ++length;
            }
            System.out.println(length);
        }


        /*DataInputStream stream = new DataInputStream(
                new BufferedInputStream(
                        new FileInputStream(AdsbDemodulatorTestPersonal.class.getResource("/samples_20230304_1442.bin").getFile())));

        PowerWindow powerWindow = new PowerWindow(stream, 1200);
        System.out.println("Size of the window: "  + powerWindow.size());
        System.out.println("Position current of the window is: " + powerWindow.position());
        System.out.println("Window is full ? " + powerWindow.isFull());
        powerWindow.advanceBy(80962);
        System.out.println("Position current of the window is: " + powerWindow.position());

        for(int i = 0; i < 46; ++i){
            if(i % 5 == 0){
                System.out.println(i + " = " + powerWindow.get(i));
            }
        }

        int p = powerWindow.get(0) + powerWindow.get(10) + powerWindow.get(35) + powerWindow.get(45);
        int v = powerWindow.get(5) + powerWindow.get(15) + powerWindow.get(20) + powerWindow.get(25) + powerWindow.get(30) + powerWindow.get(40);
        System.out.println("p = " + p);
        System.out.println("v = " + v);

        System.out.println("p >= 2*v ? " + (p >= 2*v));

        byte[] message = new byte[14];

        for(int i = 0; i < message.length; ++i){
            int a = 0;
            for(int j = 0; j < 8; ++j){
                if(powerWindow.get(80 + 10 * (j + i * 8)) < powerWindow.get(85 + 10 * (j + i * 8))){
                    a = (a << 1); //tableau de byte, concartener chaque 8 bits
                    System.out.println( "i = " + i + ", j = " + j  + " Binary = " + Integer.toBinaryString(a));
                }else{
                    a = ((a << 1) | 1);
                    System.out.println("i = " + i + ", j = " + j  + " Binary = " + Integer.toBinaryString(a));
                }
            }
            message[i] = (byte) a;
        }

        for (int i = 0; i < 14; i++) {
            System.out.println(message[i]);
        }



        RawMessage rawMessage = RawMessage.of(8096200, message);
        System.out.println(rawMessage);

        /**
        byte[] a = new byte[]{(byte) 0b10001011};

        int b = Byte.toUnsignedInt(a[0]);
        b = b >>> 3;
        System.out.println(b);*/
    }
}
