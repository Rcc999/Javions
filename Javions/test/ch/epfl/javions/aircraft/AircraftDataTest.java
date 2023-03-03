package ch.epfl.javions.aircraft;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AircraftDataTest {

    @Test
    void AircraftDataThrowIfNul(){
        assertThrows(NullPointerException.class, () -> new AircraftData(aircraftRegistration
        , aircraftTypeDesignator, model, aircraftDescription, wakeTurbulenceCategory));
    }

    AircraftRegistration aircraftRegistration = new AircraftRegistration("HB-JDC");
    AircraftTypeDesignator aircraftTypeDesignator  = new AircraftTypeDesignator("A20N");
    String model  = null;
    AircraftDescription aircraftDescription = new AircraftDescription("L1P");
    WakeTurbulenceCategory wakeTurbulenceCategory = WakeTurbulenceCategory.of("L");

}
