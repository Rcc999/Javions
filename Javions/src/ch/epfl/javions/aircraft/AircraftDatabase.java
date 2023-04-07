package ch.epfl.javions.aircraft;


import java.io.*;
import java.util.Objects;
import java.util.zip.ZipFile;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Represents the aircraft mictronics databas
 *
 * @author Tuan Dang Nguyen (361089)
 * @author Rayane Charif Chefchouni (339839)
 */
public final class AircraftDatabase {

    private final String fileName;

    /**
     * Constructor of AircraftDatabase
     *
     * @param fileName: address resulted from getResource that leads the program to the correspond file
     * @throws NullPointerException if fileName is null
     */
    public AircraftDatabase(String fileName) {
        Objects.requireNonNull(fileName);
        this.fileName = fileName;
    }

    /**
     * Return the AircraftData that correspond to the address
     *
     * @param address: IcaoAddress of the Aircraft
     * @return AircraftData that correspond to the address
     * @throws IOException if the file is not found
     */
    public AircraftData get(IcaoAddress address) throws IOException {

        //Extract 2 last digits of the address
        String nameOfFileInFileName = address.string().substring(address.string().length() - 2);

        try (ZipFile z = new ZipFile(fileName);
             //Get the correct file
             InputStream s = z.getInputStream(z.getEntry(nameOfFileInFileName + ".csv"));
             Reader r = new InputStreamReader(s, UTF_8);
             BufferedReader b = new BufferedReader(r)) {
            String l;
            String correct_line;
            String[] string_table;

            //Finding the line that correspond to the address (if that line exists)
            while ((l = b.readLine()) != null) {
                if (l.startsWith(address.string())) {
                    correct_line = l;
                    string_table = correct_line.split(",", -1);

                    //Return Aircraft's Data from the address
                    return new AircraftData(new AircraftRegistration(string_table[1]), new AircraftTypeDesignator(string_table[2]),
                            string_table[3], new AircraftDescription(string_table[4]), WakeTurbulenceCategory.of(string_table[5]));
                }
                //Since the list is sorted in the alphabetic order then at a certain tries, there isn't any need to continue the search
                if (address.string().compareTo(l) < 0) {
                    break;
                }
            }
            return null;
        }
    }
}
