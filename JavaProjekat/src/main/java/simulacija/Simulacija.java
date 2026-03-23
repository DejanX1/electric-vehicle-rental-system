package simulacija;

import kontroleri.MapaController;
import iznajmljivanje.Iznajmljivanje;
import main.Main;
import vozila.Vozilo;
import serialization.Deserijalizacija;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Predstavlja simulaciju i nasleđuje Thread.
 * U okviru ove klase se vrši simulacija iznajmljivanja i obrada podataka o vozilima.
 */
public class Simulacija extends Thread {

    // Staticki objekat klase Mapa koji se koristi u simulaciji
    public static Mapa mapa;

    // Vreme pauze između datuma simulacije u milisekundama (5 sekundi)
    private static final int PAUSE_BETWEEN_DATES = 5000;

    /**
     * Konstruktor klase Simulacija.
     * Inicijalizuje objekat klase Mapa koji se koristi tokom simulacije.
     */
    public Simulacija(){
        mapa = new Mapa();
    }

    /**
     * Glavna metoda koja se pokreće pri izvršavanju niti.
     * Ova metoda obavlja simulaciju iznajmljivanja i ažurira podatke o vozilima.
     */
    public void run() {

        List<Iznajmljivanje> pomocnaIznajmljivanja = new ArrayList<>();
        String lastDate = null;

        // Briše sadržaj direktorijuma sa pokvarenim vozilima
        Simulacija.deleteContents("src/main/resources/pokvarenaVozila");

        // Inicijalizuje datoteku za upis računa
        String fileName = "racuni.txt";
        Main.path = Main.path.resolve(fileName);

        try (PrintWriter writer = new PrintWriter(Main.path.toString())) {
            writer.print("");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Obrada svih iznajmljivanja prema datumima
        for (Iznajmljivanje iznajmljivanje : Main.validnaIznajmljivanja) {
            String currentDate = iznajmljivanje.getDatumIVrijemeIznajmljivanja();

            // Ako se promijenio datum, izvršava iznajmljivanja za prethodni datum
            if (lastDate != null && !lastDate.equals(currentDate)) {
                izvrsiIznajmljivanjaZaDatum(pomocnaIznajmljivanja, lastDate);
                pomocnaIznajmljivanja.clear();
            }

            pomocnaIznajmljivanja.add(iznajmljivanje);
            lastDate = currentDate;
        }

        // Izvršava preostala iznajmljivanja za poslednji datum
        if (!pomocnaIznajmljivanja.isEmpty()) {
            izvrsiIznajmljivanjaZaDatum(pomocnaIznajmljivanja, lastDate);
        }

        // Postavlja oznaku da je simulacija završena
        MapaController.seZavrsilaSimulacija = true;

        // Učitava pokvarena vozila iz serijalizovane datoteke
        List<Vozilo> pokvarenaVozila = Deserijalizacija.deserijalizujSvaVozila(Iznajmljivanje.pokvarenaVozilaPath);
        for(Vozilo vozilo : pokvarenaVozila) {
            Iznajmljivanje.prikazSvihVozilaKontroler.dodajPokvarenoVozilo(vozilo);
        }
    }

    /**
     * Izvršava iznajmljivanja za dati datum.
     * Pokreće niti za svako iznajmljivanje, čeka njihov završetak i pravi pauzu između datuma.
     *
     * @param iznajmljivanja Lista iznajmljivanja za koje treba izvršiti radnje
     * @param datum Datum za koji se vrši simulacija
     */
    private void izvrsiIznajmljivanjaZaDatum(List<Iznajmljivanje> iznajmljivanja, String datum) {
        try {

            // Pokreće niti za svako iznajmljivanje
            for (Iznajmljivanje iznajmljivanje : iznajmljivanja) {
                iznajmljivanje.start();
            }

            // Čeka da se sve niti završe
            for (Iznajmljivanje iznajmljivanje : iznajmljivanja) {
                iznajmljivanje.join();
            }

            // Pauza između datuma
            Thread.sleep(PAUSE_BETWEEN_DATES);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Briše sadržaj datog direktorijuma, uključujući sve poddirektorijume i datoteke.
     *
     * @param directoryPath Putanja do direktorijuma čiji sadržaj treba obrisati
     */
    private static void deleteContents(String directoryPath) {
        Path directory = Paths.get(directoryPath);

        if (Files.exists(directory) && Files.isDirectory(directory)) {
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory)) {
                for (Path file : stream) {
                    if (Files.isDirectory(file)) {
                        deleteContents(file.toString());
                    }
                    Files.delete(file);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.err.println("Direktorijum ne postoji ili nije direktorijum.");
        }
    }
}
