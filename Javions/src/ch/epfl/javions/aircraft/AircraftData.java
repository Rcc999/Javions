package ch.epfl.javions.aircraft;

import java.util.Objects;

public record AircraftData(AircraftRegistration registration, AircraftTypeDesignator typeDesignator,
                           String model, AircraftDescription description, WakeTurbulenceCategory wakeTurbulenceCategory) {

    public AircraftData{
        Objects.requireNonNull(registration);
        Objects.requireNonNull(typeDesignator);
        Objects.requireNonNull(description);
        Objects.requireNonNull(wakeTurbulenceCategory);
        Objects.requireNonNull(model);
    }

}

