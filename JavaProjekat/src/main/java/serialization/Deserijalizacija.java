package serialization;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

import vozila.Vozilo;

import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Klasa za deserijalizaciju vozila.
 */
public class Deserijalizacija {

    /**
     * Deserijalizuje sve objekte tipa 'Vozilo' iz datoteka u datom direktorijumu.
     *
     * Ova metoda pregledava sve datoteke sa ekstenzijom ".ser" u navedenom direktorijumu,
     * deserijalizuje svaki objekat Vozilo i dodaje ga u listu.
     *
     * @param folderPath Putanja do direktorijuma koji sadrži serijalizovane datoteke.
     * @return Lista svih deserijalizovanih objekata tipa 'Vozilo'.
     */
    public static List<Vozilo> deserijalizujSvaVozila(String folderPath) {
        List<Vozilo> vozila = new ArrayList<>();

        // Kreiraj Path objekat za direktorijum
        Path directoryPath = Paths.get(folderPath);

        // Proveri da li direktorijum postoji i da li je direktorijum
        if (Files.exists(directoryPath) && Files.isDirectory(directoryPath)) {
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(directoryPath, "*.ser")) {
                for (Path file : stream) {
                    try (ObjectInputStream in = new ObjectInputStream(Files.newInputStream(file))) {
                        Vozilo vozilo = (Vozilo) in.readObject();
                        double iznosPopravke = in.readDouble();
                        vozila.add(vozilo);
                        // Možeš dodati iznos popravke u objekte vozila ili u zasebnu listu ako je potrebno
                    } catch (ClassNotFoundException | IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.err.println("Direktorijum ne postoji ili nije direktorijum.");
        }

        return vozila;
    }
}
