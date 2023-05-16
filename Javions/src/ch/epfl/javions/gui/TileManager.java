package ch.epfl.javions.gui;

import ch.epfl.javions.Preconditions;
import javafx.scene.image.Image;

import java.awt.*;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.LinkedHashMap;

/**
 * A class that manages the tiles of a map.
 *
 * @author Tuan Dang Nguyen (361089)
 * @author Rayane Charif Chefchouni (339839)
 */
public class TileManager {

    // The maximum number of tiles that can be stored in memory.
    private final static int MAX_CAPACITY = 100;

    // The load factor of the memory cache.
    public static final float LOAD_FACTOR = 0.75F;

    // The title of the application.
    public static final String TITLE = "Javions";

    private final Path pathToDisk;
    private final String name;
    private final LinkedHashMap<TileId, Image> memory;

    /**
     * Creates a new TileManager.
     *
     * @param pathToDisk : the path to the disk cache
     * @param name : the name of the server
     */
    public TileManager(Path pathToDisk, String name) {
        this.pathToDisk = pathToDisk;
        this.name = name;
        memory = new LinkedHashMap<>(MAX_CAPACITY, LOAD_FACTOR, true);
    }

    /**
     * Returns the image for the tile at the given tile id.
     *
     * @param tileId : the tile id
     * @return the image for the tile at the given tile id
     * @throws IOException if an I/O error occurs
     */
    public Image imageForTileAt(TileId tileId) throws IOException {

        Path pathToDirectory = pathToDisk.resolve(String.valueOf(tileId.zoomLevel)).resolve(String.valueOf(tileId.x));
        Path pathToY = pathToDirectory.resolve((tileId.y) + ".png");
        Preconditions.checkArgument(TileId.isValid(tileId.zoomLevel, tileId.x, tileId.y));

        if (memory.containsKey(tileId)) {
            return memory.get(tileId);
        }

        Iterator<TileId> it = memory.keySet().iterator();

        if (memory.size() == MAX_CAPACITY) {
            memory.remove(it.next());
        }

        Image image;

        if (Files.exists(pathToY)) {
            try (FileInputStream stream = new FileInputStream(pathToY.toFile())) {
                image = new Image(stream);
            }
        } else {
            Files.createDirectories(pathToDirectory);

            URL u = new URL("https://" + name + "/" + tileId.zoomLevel + "/" + tileId.x + "/" + tileId.y + ".png");
            URLConnection c = u.openConnection();
            c.setRequestProperty("User-Agent", TITLE);

            try (InputStream stream = c.getInputStream(); OutputStream o = new FileOutputStream(pathToY.toFile())) {
                byte[] imageData = stream.readAllBytes();
                o.write(imageData); //to write the image from the stream to the cache disk
                image = new Image(new ByteArrayInputStream(imageData));
            }
        }
        memory.put(tileId, image);
        return image;
    }


    /**
     * Imbricated record class representing a tile id.
     */
    public record TileId(int zoomLevel, int x, int y) {

        /**
         * Returns true if the given tile id is valid.
         *
         * @param zoomLevel : the zoom level
         * @param x : the x coordinate
         * @param y : the y coordinate
         * @return true if the given tile id is valid
         */
        public static boolean isValid(int zoomLevel, int x, int y) {
            int max = 1 << zoomLevel;
            return x >= 0
                    && x <= max
                    && y >= 0
                    && y <= max;
        }
    }
}
