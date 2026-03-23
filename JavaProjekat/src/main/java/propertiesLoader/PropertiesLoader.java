package propertiesLoader;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Klasa koja pruža pomoćne metode za rad sa konfiguracionim datotekama.
 */
public class PropertiesLoader {

    /**
     * Učitava properties iz datoteke sa specificiranom putanjom.
     * Ako dođe do greške prilikom otvaranja datoteke ili učitavanja,
     * ispisuje poruku o grešci na standardni izlaz.
     * U slučaju greške, vraća prazne properties.
     *
     * @param putanjaDoPropertiesFajla Putanja do properties datoteke.
     * @return Properties objekat učitan iz datoteke.
     */
    public static Properties ucitajProperties(String putanjaDoPropertiesFajla) {
        Properties properties = new Properties();
        Path putanja = Paths.get(putanjaDoPropertiesFajla);

        try (InputStream input = new FileInputStream(putanja.toString())) {
            properties.load(input);
        } catch (IOException e) {
            System.out.println("Greška pri učitavanju properties fajla: " + e.getMessage());
        }

        return properties;
    }
}