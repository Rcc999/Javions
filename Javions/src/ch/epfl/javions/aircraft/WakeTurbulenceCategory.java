package ch.epfl.javions.aircraft;

public enum WakeTurbulenceCategory {
    LIGHT, MEDIUM, HEAVY, UNKNOWN;

    /**
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
