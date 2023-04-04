package ch.epfl.javions.aircraft;

/**
 * The wake turbulence category of an aircraft
 *
 * @author Tuan Dang Nguyen (361089)
 * @author Rayane Charif Chefchouni (339839)
 */
public enum WakeTurbulenceCategory {
    LIGHT, MEDIUM, HEAVY, UNKNOWN;

    /**
     * Get the wake turbulence category correspond to a given character
     *
     * @param s: indication of the status of a flying object in abbreviation
     * @return the weight of the flying object
     */
    public static WakeTurbulenceCategory of(String s){
        return switch (s) {
            case "L" -> WakeTurbulenceCategory.LIGHT;
            case "M" -> WakeTurbulenceCategory.MEDIUM;
            case "H" -> WakeTurbulenceCategory.HEAVY;
            default -> WakeTurbulenceCategory.UNKNOWN;
        };
    }
}
