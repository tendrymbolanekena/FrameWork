package util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

public class FileUtil {

    public static String copierFichier(MapFile fichier, String dossier)
            throws IOException {

        if (fichier == null || fichier.getContenu() == null) {
            return null;
        }

        Path repertoire = Paths.get(dossier);
        Files.createDirectories(repertoire);


        String nomStockage = UUID.randomUUID().toString();

        if (fichier.getExtension() != null
                && !fichier.getExtension().isBlank()) {
            nomStockage += "." + fichier.getExtension();
        }

        Path destination = repertoire.resolve(nomStockage);

        Files.write(destination, fichier.getContenu());

        return destination.toString();
    }
}