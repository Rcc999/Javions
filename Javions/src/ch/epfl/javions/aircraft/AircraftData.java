package ch.epfl.javions.aircraft;

import java.util.Objects;

/**
 * Data of an aircraft
 *
 * @author Tuan Dang Nguyen (361089)
 * @author Rayane Charif Chefchouni (339839)
 */
public record AircraftData(AircraftRegistration registration, AircraftTypeDesignator typeDesignator,
                           String model, AircraftDescription description,
                           WakeTurbulenceCategory wakeTurbulenceCategory) {

    /**
     * Collect fixed data of an aircraft
     *
     * @param registration
     * @param typeDesignator
     * @param model
     * @param description
     * @param wakeTurbulenceCategory
     * @throws NullPointerException if one of the parameter is null
     */
    public AircraftData {
        Objects.requireNonNull(registration);
        Objects.requireNonNull(typeDesignator);
        Objects.requireNonNull(description);
        Objects.requireNonNull(wakeTurbulenceCategory);
        Objects.requireNonNull(model);
    }

}

