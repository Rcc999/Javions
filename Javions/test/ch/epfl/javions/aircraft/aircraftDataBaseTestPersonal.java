package ch.epfl.javions.aircraft;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URLDecoder;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class aircraftDataBaseTestPersonal {

    @Test
    void NotFoundCorrectValueGet() throws IOException {

        String d = getClass().getResource("/aircraft.zip").getFile();
        d = URLDecoder.decode(d, UTF_8);

        AircraftDatabase test = new AircraftDatabase(d);

        assertNull(test.get(new IcaoAddress("018A01")));

    }

    @Test
    void FoundCorrectValueGet() throws IOException {

        String d = getClass().getResource("/aircraft.zip").getFile();
        d = URLDecoder.decode(d, UTF_8);

        AircraftDatabase test = new AircraftDatabase(d);
        AircraftData output = test.get(new IcaoAddress("31DD01"));
        assert output != null;
        String a = output.description().string();
        assertEquals("L0-", a);
    }
}
