package ch.epfl.javions.gui;

import javafx.scene.image.Image;

import java.awt.*;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.LinkedHashMap;

public class TileManager {

    private final static int MAX_CAPACITY = 100;
    private final Path pathToDisk;
    private final String name;
    private final LinkedHashMap<TileId, Image> memory;

    public TileManager(Path pathToDisk, String name) {
        this.pathToDisk = pathToDisk;
        this.name = name;
        memory = new LinkedHashMap<>(MAX_CAPACITY, 0.75F, true);
    }

    public Image imageForTileAt(TileId tileId) throws IOException {

        Path pathToDirectory = pathToDisk.resolve(String.valueOf(tileId.zoomLevel)).resolve(String.valueOf(tileId.x));
        Path pathToY = pathToDirectory.resolve((tileId.y) + ".png");

        if (memory.containsKey(tileId)) {
            return memory.get(tileId);
        }

        Iterator<TileId> it = memory.keySet().iterator();

        if (memory.size() == MAX_CAPACITY) {
            memory.remove(it.next());
        }

        Image image;

        if(Files.exists(pathToY)){
            try(FileInputStream stream = new FileInputStream(pathToY.toFile())){
                image = new Image(stream);
            }
        } else {
            Files.createDirectories(pathToDirectory);

            URL u = new URL("https://" + name + "/" + tileId.zoomLevel + "/" + tileId.x + "/" + tileId.y + ".png");
            URLConnection c = u.openConnection();
            c.setRequestProperty("User-Agent", "Javions");

            try(InputStream stream = c.getInputStream() ; OutputStream o = new FileOutputStream(pathToY.toFile())){
                byte[] imageData =  stream.readAllBytes();
                o.write(imageData); //to write the image from the stream to the cache disk
                image = new Image(new ByteArrayInputStream(imageData));
            }
        }
        memory.put(tileId, image);
        return  image;
    }


    public record TileId(int x, int y, int zoomLevel) {

        public static boolean isValid(int zoomLevel, int x, int y) {
            int max = 1 << zoomLevel;
            return x >= 0
                    && x <= max
                    && y >= 0
                    && y <= max;

        }
    }
}
