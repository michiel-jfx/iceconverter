package nl.dotjava.javafx.support;

import com.gluonhq.attach.storage.StorageService;
import javafx.scene.text.Font;
import nl.dotjava.javafx.iceconverter.IceController;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

public class StorageSupport {

    private static final String FONT_UBUNTU = "ubuntu_medium.ttf";
    private static final double FONT_SIZE = 24.0;

    /** Get and return custom font to use in mobile app (using local storage technique) */
    public static Font loadCustomFont() {
        Optional<File> privateStorage = StorageService.create().flatMap(StorageService::getPrivateStorage);
        if (privateStorage.isPresent()) {
            File fontFile = new File(privateStorage.get(), FONT_UBUNTU);
            if (!fontFile.exists()) {
                copyFontToStorage(fontFile);
            }
            // if the font was not found initially, it was copied and is existing now ('cause parameter by reference ;-)
            if (fontFile.exists()) {
                Font font = Font.loadFont(fontFile.toURI().toString(), FONT_SIZE);
                if (font != null) {
                    System.out.println("***** Font loaded from storage (" + fontFile.getAbsolutePath() + "), name '" + font.getName() + "', family '" + font.getFamily() + "', size " + font.getSize());
                    return font;
                }
            }
        }
        return null;
    }

    /** Copy a fontFile present in the resources to the local storage for faster usage */
    private static void copyFontToStorage(File fontFile) {
        try {
            InputStream inputStream = IceController.class.getResourceAsStream("fonts/" + FONT_UBUNTU);
            if (inputStream != null) {
                try (FileOutputStream fos = new FileOutputStream(fontFile)) {
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        fos.write(buffer, 0, bytesRead);
                    }
                }
                inputStream.close();
                System.out.println("***** Font copied from resources to storage: " + fontFile.getAbsolutePath());
            }
        } catch (FileNotFoundException fnfe) {
            System.err.println("***** Error copying font to storage: " + fnfe.getMessage());
        } catch (IOException ioe) {
            System.err.println("***** I/O Error copying font: " + ioe.getMessage());
        }
    }
}
