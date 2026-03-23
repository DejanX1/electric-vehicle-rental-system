package main;

import kontroleri.MapaController;
import kontroleri.PrikazSvihPrevoznihSredstavaController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import iznajmljivanje.*;
import vozila.*;

/**
 * Predstavlja glavni dio aplikacije koji upravlja pokretanjem i
 * obradom podataka u sistemu za iznajmljivanje prevoznih sredstava.
 *
 * Ova klasa služi kao ulazna tačka za aplikaciju i upravlja inicijalizacijom i učitavanjem ulaznih podataka,
 * omogućava integraciju svih komponenti sistema i koordinira izvršenje ključnih funkcionalnosti aplikacije.
 */
public class Main extends Application {

    public static List<Iznajmljivanje> validnaIznajmljivanja = new ArrayList<>();
    public static List<Vozilo> validnaUlaznaVozila = new ArrayList<>();
    public static Path path = Paths.get("src", "main", "resources", "racuni");

    public static FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/fxml/MapaController.fxml"));
    public static Parent root;

    private static final List<DateTimeFormatter> DATE_FORMATTERS = Arrays.asList(
            DateTimeFormatter.ofPattern("d.M.yyyy HH:mm")
            //DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"),
            //DateTimeFormatter.ofPattern("d.MM.yyyy HH:mm"),
            //DateTimeFormatter.ofPattern("dd.M.yyyy HH:mm")
    );

    static {
        try {
            root = fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static MapaController controller = fxmlLoader.getController();
    public static PrikazSvihPrevoznihSredstavaController kontroler = new PrikazSvihPrevoznihSredstavaController();

    /**
     * Početna metoda aplikacije koja se poziva prilikom pokretanja JavaFX aplikacije.
     *
     * Ova metoda postavlja scenu i naslov prozora, zatim učitava ulazne podatke o vozilima
     * i iznajmljivanjima, i dodaje vozila u kontroler.
     *
     * Ako dođe do greške prilikom učitavanja ulaznih podataka, ispisuje se poruka na konzoli.
     *
     * @param stage Glavna pozornica (prozor) aplikacije
     */
    @Override
    public void start(Stage stage) {

        Scene scene = new Scene(root);
        stage.setTitle("ePJ2 E-Mobility");
        stage.setScene(scene);
        stage.show();

        try {
            ucitajUlaznaVozila();
            ucitajUlaznaIznajmljivanja();
        } catch (IOException e) {
            System.out.println("Greska prilikom ucitavanja ulaznih podataka!");
            e.printStackTrace();
        }

        for(Vozilo vozilo : Main.validnaUlaznaVozila) {
            kontroler.dodajVozilo(vozilo);
        }
    }

    /**
     * Glavna metoda aplikacije koja služi kao ulazna tačka programa.
     *
     * Ova metoda pokreće JavaFX aplikaciju pozivom metode {@link javafx.application.Application#launch launch()},
     * koja inicijalizuje JavaFX runtime i poziva metodu {@link #start start(Stage)}.
     *
     * @param args Argumenti komandne linije (ne koriste se)
     */
    public static void main(String[] args) {
        launch();
    }

    /**
     * Učitava ulazna vozila iz specificirane datoteke tako što čita sve linije iz datoteke i dodaje ih u novu listu,
     * nakon čega briše zaglavlje te liste. Dalje parsira i validira podatke te kreira objekte za validna vozila.
     *
     * U slučaju da određeni podaci nisu validni ispisuje se poruka na konzoli koja to naglašava, vozilo se preskače i
     * nastavlja se sa učitavanjem preostalih ulaznih vozila.
     *
     * @throws IOException Ako dođe do greške prilikom čitanja datoteke.
     */
    private static void ucitajUlaznaVozila() throws IOException {

        // Dohvatanje putanje do ulazne datoteke
        String inputFilePath = Iznajmljivanje.properties.getProperty("FAJL_PREVOZNA_SREDSTVA");

        // Čitanje svih linija iz datoteke, smjestanje u listu i brisanje zaglavlja u listi
        List<String> listaUlaznihVozila = Files.readAllLines(Paths.get(inputFilePath));
        listaUlaznihVozila.removeFirst();

        validnaUlaznaVozila = new ArrayList<>(listaUlaznihVozila.size());
        int brojacUlaznihVozila = 0;
        int brojacValidnihUlaznihVozila = 0;

        while(brojacUlaznihVozila < listaUlaznihVozila.size()) {

            String[] parametriVozila = parseCSVLine(listaUlaznihVozila.get(brojacUlaznihVozila));
            if (parametriVozila.length != 7 && parametriVozila.length != 6) {
                brojacUlaznihVozila++;
                System.out.println("NESPRAVAN BROJ PARAMETARA KOD UCITAVANJA PREVOZNOG SREDSTVA: " + parametriVozila[0]);
                continue;
            }

            boolean jesteDuplikat = false;
            for(Vozilo vozilo : Main.validnaUlaznaVozila) {
                if(parametriVozila[0].equals(vozilo.getId())) {
                    jesteDuplikat = true;
                    System.out.println("VOZILO JE DUPLIKAT: " + vozilo);
                    break;
                }
            }

            if(jesteDuplikat) {
                brojacUlaznihVozila++;
                continue;
            }

            String idVozila = parametriVozila[0];
            String proizvodjac = parametriVozila[1];
            String model = parametriVozila[2];
            String vrsta = parametriVozila[parametriVozila.length - 1];

            String datumNabavke = "";
            double cijenaNabavke = 0;
            String opis = "";
            double domet = 0;
            double maksimalnaBrzina = 0;

            try {
                if (parametriVozila.length == 7) {
                    datumNabavke = parametriVozila[3];

                    try {
                        LocalDate.parse(datumNabavke, DateTimeFormatter.ofPattern("d.M.yyyy."));
                    } catch (DateTimeParseException e) {
                        System.out.println("NIJE VALIDAN DATUM NABAVKE: " + datumNabavke + " ZA VOZILO: " + idVozila);
                        brojacUlaznihVozila++;
                        continue;
                    }

                    cijenaNabavke = Double.parseDouble(parametriVozila[4]);
                    opis = parametriVozila[5];
                } else {
                    cijenaNabavke = Double.parseDouble(parametriVozila[4]);

                    if ("bicikl".equals(vrsta)) {
                        domet = Double.parseDouble(parametriVozila[4]);
                    } else {
                        maksimalnaBrzina = Double.parseDouble(parametriVozila[4]);
                    }
                }
            } catch (Exception e) {
                System.out.println("GREŠKA PRILIKOM PARSIRANJA CIJENE/DOMETA/MAX-BRZINE PREVOZNOG SREDSTVA: " + parametriVozila[0]);
                brojacUlaznihVozila++;
                continue;
            }

            if(cijenaNabavke < 0 || domet < 0 || maksimalnaBrzina < 0) {
                brojacUlaznihVozila++;
                System.out.println("ODREDJENI PARAMETRI NE SMIJU BITI NEGATIVNI ZA PREVOZNO SREDSTVO: " + parametriVozila[0]);
                continue;
            }

            boolean prevoziViseLjudi = true;
            if (brojacValidnihUlaznihVozila % 2 == 0) {
                prevoziViseLjudi = false;
            }

            try {
                if ("automobil".equals(vrsta)) {
                    validnaUlaznaVozila.add(brojacValidnihUlaznihVozila, new ElektricniAutomobil(idVozila, datumNabavke, cijenaNabavke, proizvodjac, model, opis, 100.0, prevoziViseLjudi));
                } else if ("bicikl".equals(vrsta)) {
                    validnaUlaznaVozila.add(brojacValidnihUlaznihVozila, new ElektricniBicikl(idVozila, cijenaNabavke, proizvodjac, model, 100.0, domet));
                } else if ("trotinet".equals(vrsta)) {
                    validnaUlaznaVozila.add(brojacValidnihUlaznihVozila, new ElektricniTrotinet(idVozila, cijenaNabavke, proizvodjac, model, 100.0, maksimalnaBrzina));
                } else {
                    throw new Exception("Vozilo ne postoji!");
                }
            } catch (Exception e) {
                System.out.println("VRSTA: '" + vrsta + "' PREVOZNOG SREDSTVA: '" + parametriVozila[0] + "' JE NEPOSTOJECA!");
                brojacUlaznihVozila++;
                continue;
            }

            brojacUlaznihVozila++;
            brojacValidnihUlaznihVozila++;
        }
    }

    /**
     * Učitava ulazna iznajmljivanja iz specificirane datoteke tako što čita sve linije iz datoteke i dodaje ih u novu listu
     * kojoj briše zaglavlje, pa istu sortira po datumu. Dalje parsira i validira podatke, pa kreira objekte validnih iznajmljivanja.
     *
     * U slučaju da određeni podaci nisu validni ispisuje se poruka na konzoli koja to naglašava, iznajmljivanje se preskače i
     * nastavlja se sa učitavanjem preostalih ulaznih iznajmljivanja.
     *
     * @throws IOException Ako dođe do greške prilikom čitanja datoteke
     */
    private static void ucitajUlaznaIznajmljivanja() throws IOException {

        // Dobijanje putanje do ulazne datoteke
        String inputFilePath = Iznajmljivanje.properties.getProperty("FAJL_IZNAJMLJIVANJE");

        // Čitanje svih linija iz datoteke i brisanje zaglavlja u listi
        List<String> list = Files.readAllLines(Paths.get(inputFilePath));
        list.removeFirst();

        // Definisanje formata datuma u datoteci
        SimpleDateFormat dateFormat = new SimpleDateFormat("d.M.yyyy HH:mm");

        // Sortiranje liste na osnovu datuma
        list.sort((s1, s2) -> {
            try {
                // Parsiranje linija CSV-a
                String[] parts1 = parseCSVLine(s1);
                String[] parts2 = parseCSVLine(s2);

                // Ekstrakcija datuma iz stringova
                Date date1 = dateFormat.parse(parts1[0].trim());
                Date date2 = dateFormat.parse(parts2[0].trim());

                // Uporedba datuma
                return date1.compareTo(date2);

            } catch (Exception e) {
                return 0;
            }
        });

        int brojacUlaznihIznajmljivanja = 0;
        int brojacValidnihUlaznihIznajmljivanja = 0;

        while(brojacUlaznihIznajmljivanja < list.size()) {

            String[] parametriIznajmljivanja = parseCSVLine(list.get(brojacUlaznihIznajmljivanja));

            if(parametriIznajmljivanja.length == 0) {
                System.out.println("IMAMO PRAZNU LINIJU KOJA SE PRESKACE!");
                brojacUlaznihIznajmljivanja++;
                continue;
            }

            if(parametriIznajmljivanja.length != 8) {
                System.out.println("NEISPRAVAN BROJ ARGUMENATA ZA: " + list.get(brojacUlaznihIznajmljivanja));
                brojacUlaznihIznajmljivanja++;
                continue;
            }

            // Provjera da li je vozilo kreirano, ne moze se iznajmiti ako ne postoji
            boolean jeKreiranoVozilo = false;
            for(Vozilo v : Main.validnaUlaznaVozila) {
                if(parametriIznajmljivanja[2].equals(v.getId())) {
                    jeKreiranoVozilo = true;
                    break;
                }
            }

            if(!jeKreiranoVozilo) {
                System.out.println("NE POSTOJI VOZILO: " + parametriIznajmljivanja[2]);
                brojacUlaznihIznajmljivanja++;
                continue;
            }

            String datumIVrijemeIznajmljivanja = parametriIznajmljivanja[0];
            String idVozila = parametriIznajmljivanja[2]; // Provjeri se da li postoji vozilo sa datim idVozila

            boolean jeZauzetoVozilo = false;
            for(Iznajmljivanje iznajmljivanje : validnaIznajmljivanja) {
                if(datumIVrijemeIznajmljivanja.equals(iznajmljivanje.getDatumIVrijemeIznajmljivanja())
                        && idVozila.equals(iznajmljivanje.getVozilo().getId())) {
                    jeZauzetoVozilo = true;
                    break;
                }
            }

            if(jeZauzetoVozilo) {
                System.out.println("VOZILO " + idVozila + " JE VEC IZNAJMLJENO PA NIJE MOGUCE IZNAJMLJIVANJE: " + datumIVrijemeIznajmljivanja + ", ID: " + idVozila);
                brojacUlaznihIznajmljivanja++;
                continue;
            }

            if(!isValidDate(datumIVrijemeIznajmljivanja)) {
                System.out.println("NIJE VALIDAN DATUM: " + datumIVrijemeIznajmljivanja + " ZA IZNAJMLJIVANJE VOZILA: " + idVozila);
                brojacUlaznihIznajmljivanja++;
                continue;
            }

            String lokacijaPreuzimanja = parametriIznajmljivanja[3];
            String lokacijaVracanja = parametriIznajmljivanja[4];

            if(!provjeriIspravnostKoordinata(lokacijaPreuzimanja, lokacijaVracanja)) {
                System.out.println("NEISPRAVNE KOORDINATE ZA IZNAJMLJIVANJE: " + parametriIznajmljivanja[3] + ", " + parametriIznajmljivanja[4]);
                brojacUlaznihIznajmljivanja++;
                continue;
            }

            String imeKorisnika = parametriIznajmljivanja[1];
            double trajanje = 0;

            try {
                trajanje = Double.parseDouble(parametriIznajmljivanja[5]);
            } catch (Exception e) {
                System.out.println("NEMOGUCE PARSIRANJE PRAMATRA TRAJANJE: '" + parametriIznajmljivanja[5] + "' ZA VOZILO: " + idVozila);
                brojacUlaznihIznajmljivanja++;
                continue;
            }

            if(trajanje <= 0) {
                System.out.println("NIJE MOGUC NEGATIVAN BROJ SEKUNDI IZNAJMLJIVANJA: " + datumIVrijemeIznajmljivanja + ", ID: " + idVozila);
                brojacUlaznihIznajmljivanja++;
                continue;
            }

            if((!"da".equals(parametriIznajmljivanja[6]) && !"ne".equals(parametriIznajmljivanja[6]))
                    || (!"da".equals(parametriIznajmljivanja[7]) && !"ne".equals(parametriIznajmljivanja[7]))) {
                System.out.println("PARAMETAR ZA PROMOCIJU I KVAR MOZE BITI SAMO 'da' ILI 'ne', NEPRAVILNI PARAMETRI ZA: " + datumIVrijemeIznajmljivanja + ", ID: " + idVozila);
                brojacUlaznihIznajmljivanja++;
                continue;
            }

            boolean imaKvar = false;
            if ("da".equals(parametriIznajmljivanja[6])) {
                imaKvar = true;
            }

            boolean imaPromociju = false;
            if ("da".equals(parametriIznajmljivanja[7])) {
                imaPromociju = true;
            }

            for (Vozilo vozilo : Main.validnaUlaznaVozila) {
                if (idVozila.equals(vozilo.getId())) {

                    validnaIznajmljivanja.add(brojacValidnihUlaznihIznajmljivanja, new Iznajmljivanje(datumIVrijemeIznajmljivanja, vozilo, imeKorisnika, lokacijaPreuzimanja, lokacijaVracanja, trajanje, imaKvar, imaPromociju));

                    brojacValidnihUlaznihIznajmljivanja++;
                    break;
                }
            }

            brojacUlaznihIznajmljivanja++;
        }
    }

    /**
     * Proverava da li je dati string `dateStr` validan datum koristeći niz mogućih formata datuma.
     *
     * @param dateStr String koji predstavlja datum koji treba proveriti.
     *                Očekuje se da bude u jednom od formata navedenih u `DATE_FORMATTERS`.
     * @return `true` ako je `dateStr` validan datum u jednom od formata; `false` inače.
     */
    private static boolean isValidDate(String dateStr) {
        for (DateTimeFormatter formatter : DATE_FORMATTERS) {
            try {
                LocalDateTime.parse(dateStr, formatter);
                return true;
            } catch (DateTimeParseException e) {
                // Continue to the next formatter
            }
        }
        return false;
    }

    /**
     * Proverava ispravnost koordinata lokacije preuzimanja i vraćanja.
     *
     * Ova metoda prihvata dva stringa koji predstavljaju koordinate lokacije preuzimanja i vraćanja,
     * parsira ih u integer nizove i proverava da li su sve koordinate u opsegu od 0 do 19.
     *
     * @param lokacijaPreuzimanja String koji predstavlja koordinate lokacije preuzimanja.
     * @param lokacijaVracanja String koji predstavlja koordinate lokacije vraćanja.
     * @return boolean koji označava da li su koordinate za obe lokacije validne (true) ili ne (false).
     */
    private static boolean provjeriIspravnostKoordinata(String lokacijaPreuzimanja, String lokacijaVracanja) {

        try {
            int[] startCoords = parseCoordinates(lokacijaPreuzimanja);
            int[] endCoords = parseCoordinates(lokacijaVracanja);

            for (int temp : startCoords) {
                if (temp < 0 || temp > 19) {
                    return false;
                }
            }

            for (int temp : endCoords) {
                if (temp < 0 || temp > 19) {
                    return false;
                }
            }

            return true;

        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Parsira string koji predstavlja koordinate u integer niz.
     *
     * Ova metoda pretvara string u integer niz tako što dijeli string na osnovu zareza
     * i konvertuje dijelove u cjelobrojne vrijednosti. Ako string sadrži samo jedan deo (x koordinatu),
     * y koordinata se postavlja na 0.
     *
     * @param location String koji predstavlja koordinate u formatu "x,y".
     * @return int[] niz cjelobrojnih vrijednosti gdje je prvi element x koordinata, a drugi y koordinata.
     */
    private static int[] parseCoordinates(String location) {
        String[] parts = location.split(",");
        int x = Integer.parseInt(parts[0]);
        int y = (parts.length == 2) ? Integer.parseInt(parts[1]) : 0;
        return new int[]{x, y};
    }

    /**
     * Parsira jednu liniju iz CSV datoteke u niz stringova.
     *
     * Ova metoda koristi regularni izraz za identifikaciju i izdvajanje polja iz linije CSV datoteke.
     * Polja mogu biti obuhvaćena navodnicima, što omogućava prisustvo zareza unutar navodnika.
     * Metoda vraća niz stringova gdje svaki string predstavlja jedno polje iz CSV linije.
     *
     * @param line Linija CSV datoteke koja treba da bude parsirana.
     * @return String[] niz stringova gdje svaki string predstavlja jedno polje iz CSV linije.
     */
    private static String[] parseCSVLine(String line) {
        List<String> fields = new ArrayList<>();
        String regex = "\"([^\"]*)\"|([^,]+)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(line);

        while (matcher.find()) {
            if (matcher.group(1) != null) {
                // Polje je bilo unutar navodnika
                fields.add(matcher.group(1));
            } else {
                // Polje nije bilo unutar navodnika
                fields.add(matcher.group(2));
            }
        }
        return fields.toArray(new String[0]);
    }
}