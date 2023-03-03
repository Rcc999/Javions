package ch.epfl.javions.aircraft;


import java.io.*;
import java.net.URLDecoder;
import java.util.Objects;
import java.util.zip.ZipFile;

import static java.nio.charset.StandardCharsets.UTF_8;

public final class AircraftDatabase {

    private final String fileName;

    public AircraftDatabase(String fileName){
        Objects.requireNonNull(fileName);
        this.fileName = fileName;
    }

    public AircraftData get(IcaoAddress address) throws IOException {

        String nameOfFileInFileName = address.string().substring(address.string().length()-2);

        try (ZipFile z = new ZipFile(fileName);

            InputStream s = z.getInputStream(z.getEntry(nameOfFileInFileName+".csv"));
            Reader r = new InputStreamReader(s, UTF_8);
            BufferedReader b = new BufferedReader(r)) {
            String l;
            String correct_line;
            String[] string_table;

            while ((l = b.readLine()) != null){

                if(l.startsWith(address.string())){
                    correct_line = l;
                    string_table = correct_line.split(",", -1);

                    return new AircraftData(new AircraftRegistration(string_table[1]), new AircraftTypeDesignator(string_table[2]),
                            string_table[3], new AircraftDescription(string_table[4]), WakeTurbulenceCategory.of(string_table[5]));
                }

                if(address.string().compareTo(l) < 0){
                    break;
                }
            }

            return null;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
