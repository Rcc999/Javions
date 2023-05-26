package ch.epfl.javions.gui;

import ch.epfl.javions.Preconditions;
import javafx.scene.paint.Color;
import java.util.Arrays;
import java.util.List;

/**
 * A class that represents the color of the trajectory and the aircraft given their altitude
 *
 * @author Tuan Dang Nguyen (361089)
 * @author Rayane Charif Chefchouni (339839)
 */
public final class ColorRamp {

    private static final int MAX_ALTITUDE = 12000;
    private static final int MINIMUM_NUMBER_OF_COLORS = 2;
    private final List<Color> colors;

    /**
     * Constructs a new ColorRamp with the given colors
     *
     * @param colors the colors to use
     * @throws IllegalArgumentException if the number of colors is less than 2
     */
    public ColorRamp(Color... colors) {
        Preconditions.checkArgument(colors.length >= MINIMUM_NUMBER_OF_COLORS);
        this.colors = List.copyOf(Arrays.asList(colors));
    }

    /**
     * Returns the color corresponding to the given value
     *
     * @param value : the value to get the color for
     * @return the color corresponding to the given value
     */
    public Color at(double value) {

        double floorResult = value / MAX_ALTITUDE;
        double colorFade = Math.cbrt(floorResult);

        if (colorFade <= 0) return colors.get(0);
        if (colorFade >= 1) return colors.get(colors.size() - 1);

        double position = colorFade * (colors.size() - 1);
        int index = (int) Math.floor(position);
        double fade = position - index;

        return colors.get(index).interpolate(colors.get(index + 1), fade);
    }

    /**
     * The color ramp used to display the aircraft
     */
    public static final ColorRamp PLASMA = new ColorRamp(
            Color.valueOf("0x0d0887ff"), Color.valueOf("0x220690ff"),
            Color.valueOf("0x320597ff"), Color.valueOf("0x40049dff"),
            Color.valueOf("0x4e02a2ff"), Color.valueOf("0x5b01a5ff"),
            Color.valueOf("0x6800a8ff"), Color.valueOf("0x7501a8ff"),
            Color.valueOf("0x8104a7ff"), Color.valueOf("0x8d0ba5ff"),
            Color.valueOf("0x9814a0ff"), Color.valueOf("0xa31d9aff"),
            Color.valueOf("0xad2693ff"), Color.valueOf("0xb6308bff"),
            Color.valueOf("0xbf3984ff"), Color.valueOf("0xc7427cff"),
            Color.valueOf("0xcf4c74ff"), Color.valueOf("0xd6556dff"),
            Color.valueOf("0xdd5e66ff"), Color.valueOf("0xe3685fff"),
            Color.valueOf("0xe97258ff"), Color.valueOf("0xee7c51ff"),
            Color.valueOf("0xf3874aff"), Color.valueOf("0xf79243ff"),
            Color.valueOf("0xfa9d3bff"), Color.valueOf("0xfca935ff"),
            Color.valueOf("0xfdb52eff"), Color.valueOf("0xfdc229ff"),
            Color.valueOf("0xfccf25ff"), Color.valueOf("0xf9dd24ff"),
            Color.valueOf("0xf5eb27ff"), Color.valueOf("0xf0f921ff"));
}
