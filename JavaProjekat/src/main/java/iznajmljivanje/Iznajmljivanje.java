package iznajmljivanje;

import kontroleri.IzvjestajController;
import kontroleri.PrikazKvarovaController;
import kontroleri.PrikazSvihPrevoznihSredstavaController;
import kompanija.DnevniIzvjestaj;
import kompanija.SumarniIzvjestaj;
import main.Main;
import simulacija.Simulacija;
import vozila.*;
import serialization.Serijalizacija;
import propertiesLoader.PropertiesLoader;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Predstavlja proces iznajmljivanja vozila od strane korisnika,
 * zajedno sa svim relevantnim podacima i funkcionalnostima vezanim za taj proces.
 *
 * Ova klasa nasljeđuje Thread kako bi omogućila paralelno izvršavanje iznajmljivanja
 * u pozadini, čime se simulira stvarni proces iznajmljivanja u stvarnom vremenu.
 *
 * Klasa implementira interfejs Comparable za poređenje iznajmljivanja na osnovu cijene,
 * i Serializable interfejs za omogućavanje serijalizacije objekata ove klase.
 */
public class Iznajmljivanje extends Thread implements Comparable<Iznajmljivanje>, Serializable {

    private Vozilo vozilo;
    private String imeKorisnika;
    private String lokacijaPreuzimanja;
    private String lokacijaVracanja;
    private Double trajanje;
    private String datumIVrijemeIznajmljivanja;
    private Double ukupnaCijena;
    private boolean imaPromociju;
    private boolean imaKvar;
    private final String tipVozila;

    public static Integer brojIznajmljenihVozila = 0;
    public static ArrayList<Vozac> vozaci = new ArrayList<>();
    public static ArrayList<Kvar> kvarovi = new ArrayList<>();
    public static IzvjestajController izvjestajKontroler = new IzvjestajController();
    public static PrikazKvarovaController kvaroviKontroler = new PrikazKvarovaController();
    public static PrikazSvihPrevoznihSredstavaController prikazSvihVozilaKontroler = new PrikazSvihPrevoznihSredstavaController();
    public static Path pathRacuni = Paths.get("src", "main", "resources", "racuni", "racuni.txt");

    /**
     * Statička mapa koja čuva dnevne izvještaje po datumu.
     * Ključ u mapi je datum u formatu "d.M.yyyy", a vrijednost je
     * odgovarajući dnevni izveštaj za taj datum.
     */
    private static HashMap<String, DnevniIzvjestaj> izvjestajiPoDatumu = new HashMap<>();

    /**
     * Statička mapa koja čuva broj iznajmljivanja za svakog korisnika.
     * Ključ u mapi je ime korisnika, a vrednost je broj iznajmljivanja koji je taj korisnik obavio.
     */
    private static HashMap<String, Integer> korisniciIznajmljivanja = new HashMap<>();

    public static Properties properties = PropertiesLoader.ucitajProperties("src/main/java/config/config.properties");
    public static String pokvarenaVozilaPath = "src/main/resources/pokvarenaVozila";

    public static final Object LOCK = new Object();

    public static final double KOEFCIJENT_ZA_POPRAVKU_AUTA = 0.07;
    public static final double KOEFCIJENT_ZA_POPRAVKU_BICIKLA = 0.04;
    public static final double KOEFCIJENT_ZA_POPRAVKU_TROTINETA = 0.02;
    public static final double KOEFCIJENT_ZA_IZNOS_ODRZAVANJA = 0.2;
    public static final double KOEFCIJENT_ZA_UKUPNE_TROSKOVE = 0.2;
    private static final double KOEFCIJENT_ZA_POREZ = 0.1;

    /**
     * Konstruktor za kreiranje novog objekta iznajmljivanja vozila.
     *
     * Inicijalizuje sve potrebne podatke za iznajmljivanje, uključujući vozilo, korisnika, lokaciju, trajanje i eventualne kvarove i promocije.
     *
     * Takođe, ažurira broj iznajmljivanja za datog korisnika i dodaje odgovarajućeg vozača na osnovu vrste vozila i broja vozača.
     * Izračunava ukupnu cijenu iznajmljivanja na osnovu proslijeđenih podataka i povećava broj iznajmljenih vozila.
     *
     * @param datumIVrijemeIznajmljivanja Datum i vreme iznajmljivanja vozila.
     * @param vozilo Vozilo koje se iznajmljuje.
     * @param imeKorisnika Ime korisnika koji iznajmljuje vozilo.
     * @param lokacijaPreuzimanja Lokacija sa koje se vozilo preuzima.
     * @param lokacijaVracanja Lokacija na kojoj se vozilo vraća.
     * @param trajanje Trajanje iznajmljivanja u sekundama.
     * @param imaKvar Da li vozilo ima kvar.
     * @param imaPromociju Da li iznajmljivanje ima promociju.
     */
    public Iznajmljivanje(String datumIVrijemeIznajmljivanja, Vozilo vozilo, String imeKorisnika, String lokacijaPreuzimanja, String lokacijaVracanja, double trajanje, boolean imaKvar, boolean imaPromociju) {

        this.vozilo = vozilo;
        this.imaKvar = imaKvar;
        this.imaPromociju = imaPromociju;

        this.imeKorisnika = imeKorisnika;
        azurirajKorisnike(imeKorisnika);

        this.lokacijaPreuzimanja = lokacijaPreuzimanja;
        this.lokacijaVracanja = lokacijaVracanja;
        this.trajanje = trajanje;
        this.datumIVrijemeIznajmljivanja = datumIVrijemeIznajmljivanja;

        if (vozilo.getClass().equals(ElektricniAutomobil.class)) {
            if (vozaci.size() % 2 == 0)
                vozaci.add(new DomaciVozac(imeKorisnika));
            else
                vozaci.add(new StraniVozac(imeKorisnika));
        }

        tipVozila = vozilo.getTipVozila();

        ukupnaCijena = izracunajUkupnuCijenu(this);
        brojIznajmljenihVozila++;
    }

    /**
     * Izvršava radnje vezane za iznajmljivanje vozila u okviru niti.
     *
     * Metoda obuhvata sledeće korake:
     * - Parsira početnu i završnu lokaciju iz stringova i konvertuje ih u koordinate (X, Y).
     * - Postavlja vozilo na početnu lokaciju na mapi i ažurira prikaz u kontroleru.
     * - Proverava nivo baterije vozila i puni je ako je prazna.
     * - Izračunava vreme zadržavanja na jednom polju i pokreće kretanje vozila od početne do završne lokacije.
     * - Generiše račun za iznajmljivanje.
     * - Obrađuje kvar ako postoji i dodaje ga u kontroler za kvarove.
     *
     * Ako dodje do greske tokom izvrsavanja, bice uhvacena i ispisana na konzolu.
     */
    @Override
    public void run() {
        try {

            String[] start = lokacijaPreuzimanja.split(",");
            String[] end = lokacijaVracanja.split(",");

            int startX;
            int endX;
            int startY = 0;
            int endY = 0;

            startX = Integer.parseInt(start[0]);
            if(start.length == 2)
                startY = Integer.parseInt(start[1]);

            endX = Integer.parseInt(end[0]);
            if(end.length == 2)
                endY = Integer.parseInt(end[1]);

            int brojPolja = (Math.abs(endX - startX)) + (Math.abs(endY - startY)) + 1;
            double vrijemeZadrzavanjaNaJednomPolju = trajanje / brojPolja;

            Simulacija.mapa.postaviVozilo(vozilo, startX, startY);
            Main.controller.updateVehiclePosition(startX, startY, vozilo);
            Thread.sleep((long) (vrijemeZadrzavanjaNaJednomPolju * 1000));

            if(vozilo.getTrenutniNivoBaterije() == 0) {
                vozilo.puniBateriju(100);
            }

            moveVehicle(startX, startY, endX, endY, vrijemeZadrzavanjaNaJednomPolju);

            synchronized (LOCK) {
                generisiRacun();
            }

            if(imaKvar) {
                obradiKvar(Kvar.getOpisiKvarova(), this.datumIVrijemeIznajmljivanja, this.vozilo.getId(), this.vozilo.getTipVozila());
                kvaroviKontroler.dodajKvar(vozilo.getKvar());
            }

        } catch (Exception e) {
            System.out.println("Greška pri iznajmljivanju vozila: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Rukuje kvarom za vozilo sa datim opisom, datumom i vremenom, ID-jem vozila i vrstom prevoznog sredstva.
     * Ovo uključuje dodavanje kvara vozilu, izračunavanje cijene popravke, dodavanje u listu svih kvarova i serijalizaciju.
     *
     * @param opis opis kvara
     * @param datumIVrijeme datum i vrijeme kvara
     * @param idVozila ID vozila na kojem se desio kvar
     * @param vrstaPrevoznogSredstva vrsta prevoznog sredstva na kojem se desio kvar
     */
    private void obradiKvar(String opis, String datumIVrijeme, String idVozila, String vrstaPrevoznogSredstva) {
        vozilo.dodajKvar(opis, datumIVrijeme, idVozila, vrstaPrevoznogSredstva);
        vozilo.setCijenaPopravkeKvara(izracunajCijenuPopravkeKvara());
        Iznajmljivanje.kvarovi.add(vozilo.getKvar());

        Serijalizacija.serijalizuj(vozilo, vozilo.getCijenaPopravkeKvara(), pokvarenaVozilaPath);
    }

    /**
     * Pomiče vozilo sa početne na krajnju poziciju duž X i Y ose, uz zadato kašnjenje između koraka.
     *
     * @param startX početna X koordinata
     * @param startY početna Y koordinata
     * @param endX krajnja X koordinata
     * @param endY krajnja Y koordinata
     * @param delay kašnjenje između koraka u sekundama
     * @throws InterruptedException ako je pomeranje prekinuto
     */
    private void moveVehicle(int startX, int startY, int endX, int endY, double delay) throws InterruptedException {
        int currentX = startX;
        int currentY = startY;

        // Move along X axis
        while (currentX != endX) {
            int nextX = (currentX < endX) ? currentX + 1 : currentX - 1;
            moveOneStep(currentX, currentY, nextX, currentY, delay);
            currentX = nextX;
        }

        if(startX != endX) {
            Simulacija.mapa.ukloniVozilo(currentX, currentY);
            Main.controller.updateVehiclePosition(endX, currentY, vozilo);

            if(vozilo.getTrenutniNivoBaterije() == 0)
                vozilo.puniBateriju(100);
        }

        // Move along Y axis
        while (currentY != endY) {
            int nextY = (currentY < endY) ? currentY + 1 : currentY - 1;
            moveOneStep(currentX, currentY, currentX, nextY, delay);
            currentY = nextY;
        }

        if(startY != endY) {
            Simulacija.mapa.ukloniVozilo(currentX, currentY);
            Main.controller.updateVehiclePosition(currentX, endY, vozilo);

            if(vozilo.getTrenutniNivoBaterije() == 0)
                vozilo.puniBateriju(100);
        }

        if (vozilo.getTrenutniNivoBaterije() > 0) {
            vozilo.setTrenutniNivoBaterije(vozilo.getTrenutniNivoBaterije() - 1);
        }
    }

    /**
     * Pomiče vozilo za jedan korak sa stare na novu poziciju, uz zadato kašnjenje.
     * Ujedno smanjuje nivo baterije vozila za 1 ukoliko baterija nije prazna.
     *
     * @param oldX stara X koordinata
     * @param oldY stara Y koordinata
     * @param newX nova X koordinata
     * @param newY nova Y koordinata
     * @param delay kašnjenje između koraka u sekundama
     * @throws InterruptedException ako je pomeranje prekinuto
     */
    private void moveOneStep(int oldX, int oldY, int newX, int newY, double delay) throws InterruptedException {
        if (vozilo.getTrenutniNivoBaterije() > 0) {
            vozilo.setTrenutniNivoBaterije(vozilo.getTrenutniNivoBaterije() - 1);
        }

        Simulacija.mapa.ukloniVozilo(oldX, oldY);
        Simulacija.mapa.postaviVozilo(vozilo, newX, newY);
        Main.controller.updateVehiclePosition(newX, newY, vozilo);
        Thread.sleep((long) (delay * 1000));
    }

    /**
     * Generiše račun i zapisuje ga u fajl "racuni.txt".
     * Ako dođe do greške prilikom pisanja u fajl ispisuje se poruka o grešci na konzolu.
     */
    private void generisiRacun(){
        try (PrintWriter writer = new PrintWriter(new java.io.FileWriter(pathRacuni.toString(), true))) {

            boolean imaPopust = false;
            writer.print(this);

            if(korisniciIznajmljivanja.get(imeKorisnika) == 10)
                imaPopust = true;

            writer.println(", popust=" + imaPopust);

        } catch (Exception e) {
            System.out.println("Greška pri pisanju u fajl: " + e.getMessage());
        }
    }

    /**
     * Izračunava ukupnu cijenu iznajmljivanja u zavisnosti od udaljenosti, postojanja kvara, popusta i promocije.
     * Takođe, ažurira dnevni i sumarni izveštaj.
     *
     * @param iznajmljivanje objekat klase Iznajmljivanje koji sadrži informacije o iznajmljivanju
     * @return ukupna cijena iznajmljivanja
     */
    private static double izracunajUkupnuCijenu(Iznajmljivanje iznajmljivanje) {

        // Kreira se dnevni izvjestaj
        DnevniIzvjestaj dnevniIzvjestaj = new DnevniIzvjestaj(iznajmljivanje.datumIVrijemeIznajmljivanja);

        double ukupnaCijena = 0;

        // Dohvaćanje vrijednosti iz properties datoteke
        double distanceNarrow = Double.parseDouble(properties.getProperty("DISTANCE_NARROW"));
        double distanceWide = Double.parseDouble(properties.getProperty("DISTANCE_WIDE"));
        double discount = Double.parseDouble(properties.getProperty("DISCOUNT"));
        double discountProm = Double.parseDouble(properties.getProperty("DISCOUNT_PROM"));
        double carUnitPrice = Double.parseDouble(properties.getProperty("CAR_UNIT_PRICE"));
        double bikeUnitPrice = Double.parseDouble(properties.getProperty("BIKE_UNIT_PRICE"));
        double scooterUnitPrice = Double.parseDouble(properties.getProperty("SCOOTER_UNIT_PRICE"));

        // Provjera da li postoji kvar
        if (iznajmljivanje.imaKvar) {

            dnevniIzvjestaj.setIznosPopravkiKvarova(getPrice(iznajmljivanje, carUnitPrice, bikeUnitPrice, scooterUnitPrice, true));
            SumarniIzvjestaj.iznosPopravkiKvarova += dnevniIzvjestaj.getIznosPopravkiKvarova();

            dodajIzvjestaj(iznajmljivanje.datumIVrijemeIznajmljivanja, dnevniIzvjestaj);

            return 0;
        }

        else {
            ukupnaCijena = getBasicCost(iznajmljivanje, carUnitPrice, bikeUnitPrice, scooterUnitPrice);
        }

        // Izračun dodatnih faktora

        // Udaljenost
        boolean jeVoznjaUzimDijelomGrada = false;

        if(iznajmljivanje.lokacijaPreuzimanja.contains(",") || iznajmljivanje.lokacijaVracanja.contains(",")) {

            String[] pom1 = iznajmljivanje.lokacijaPreuzimanja.split(",");
            String[] pom2 = iznajmljivanje.lokacijaVracanja.split(",");

            if(Integer.parseInt(pom1[0]) >= 5 && Integer.parseInt(pom1[0]) <= 14 && Integer.parseInt(pom1[1]) >= 5 && Integer.parseInt(pom1[1]) <= 14
            && Integer.parseInt(pom2[0]) >= 5 && Integer.parseInt(pom2[0]) <= 14 && Integer.parseInt(pom2[1]) >= 5 && Integer.parseInt(pom2[1]) <= 14) {
                ukupnaCijena *= distanceNarrow;
                jeVoznjaUzimDijelomGrada = true;
            }
            else {
                ukupnaCijena *= distanceWide;
            }
        }

        // Popust
        int brojIznajmljivanjaKorisnika = korisniciIznajmljivanja.getOrDefault(iznajmljivanje.imeKorisnika, 0);

        if(brojIznajmljivanjaKorisnika % 10 == 0) {
            double cijenaBezPopusta = ukupnaCijena; // Stvarna ukupna cijena
            ukupnaCijena -= ukupnaCijena * discount; // Ukupna cijena za placanje - dodali i popust

            //SumarniIzvjestaj.popust += (cijenaBezPopusta - ukupnaCijena);
            dnevniIzvjestaj.setPopust(cijenaBezPopusta - ukupnaCijena);
            SumarniIzvjestaj.popust += dnevniIzvjestaj.getPopust();
        }

        // Promocije
        if (iznajmljivanje.imaPromociju) {
            double cijenaBezPromocije = ukupnaCijena;
            ukupnaCijena -= ukupnaCijena * discountProm; // U ukupnu cijenu uracunamo promociju

            //SumarniIzvjestaj.promocije += (cijenaBezPromocije - ukupnaCijena);
            dnevniIzvjestaj.setPromocije(cijenaBezPromocije - ukupnaCijena);
            SumarniIzvjestaj.promocije += dnevniIzvjestaj.getPromocije();
        }

        if(jeVoznjaUzimDijelomGrada) {
            //SumarniIzvjestaj.voznjaUUzemDijeluGrada += ukupnaCijena;
            dnevniIzvjestaj.setVoznjaUUzemDijeluGrada(ukupnaCijena);
            SumarniIzvjestaj.voznjaUUzemDijeluGrada += dnevniIzvjestaj.getVoznjaUUzemDijeluGrada();
        }
        else {
            //SumarniIzvjestaj.voznjaUSiremDijeluGrada += ukupnaCijena;
            dnevniIzvjestaj.setVoznjaUSiremDijeluGrada(ukupnaCijena);
            SumarniIzvjestaj.voznjaUSiremDijeluGrada += dnevniIzvjestaj.getVoznjaUSiremDijeluGrada();
        }

        dnevniIzvjestaj.setPrihod(ukupnaCijena);
        SumarniIzvjestaj.prihod += dnevniIzvjestaj.getPrihod();

        dnevniIzvjestaj.setIznosOdrzavanja(dnevniIzvjestaj.getPrihod() * KOEFCIJENT_ZA_IZNOS_ODRZAVANJA);
        SumarniIzvjestaj.iznosOdrzavanja += dnevniIzvjestaj.getIznosOdrzavanja();

        dodajIzvjestaj(iznajmljivanje.datumIVrijemeIznajmljivanja, dnevniIzvjestaj);

        SumarniIzvjestaj.troskoviKompanije += dnevniIzvjestaj.getPrihod() * KOEFCIJENT_ZA_UKUPNE_TROSKOVE;
        SumarniIzvjestaj.porez += Math.abs((dnevniIzvjestaj.getPrihod() - dnevniIzvjestaj.getIznosOdrzavanja() - dnevniIzvjestaj.getIznosPopravkiKvarova() - dnevniIzvjestaj.getPrihod() * KOEFCIJENT_ZA_UKUPNE_TROSKOVE) * KOEFCIJENT_ZA_POREZ);

        return ukupnaCijena;
    }

    /**
     * Izračunava cijenu popravke kvara na osnovu vrste prevoznog sredstva.
     *
     * Ako vozilo nema kvar, metoda vraća 0.
     * Ako vozilo ima kvar, učitava cijene po jedinici za automobile, bicikle i skutere iz properties fajla
     * i koristi ih za izračunavanje cijene popravke kvara prema vrsti prevoznog sredstva.
     *
     * @return cijena popravke kvara za vozilo, ili 0 ako vozilo nema kvar
     */
    private double izracunajCijenuPopravkeKvara() {
        if(!this.imaKvar)
            return 0;

        double carUnitPrice = Double.parseDouble(properties.getProperty("CAR_UNIT_PRICE"));
        double bikeUnitPrice = Double.parseDouble(properties.getProperty("BIKE_UNIT_PRICE"));
        double scooterUnitPrice = Double.parseDouble(properties.getProperty("SCOOTER_UNIT_PRICE"));

        return DnevniIzvjestaj.zaokruzi(getPrice(this, carUnitPrice, bikeUnitPrice, scooterUnitPrice, true));
    }

    /**
     * Ažurira broj iznajmljivanja za datog korisnika.
     *
     * Ako korisnik već postoji u listi, povećava broj iznajmljivanja za 1.
     * Ako korisnik ne postoji, dodaje ga u listu sa početnim brojem iznajmljivanja postavljenim na 1.
     *
     * @param korisnik korisničko ime koje se koristi kao ključ za ažuriranje broja iznajmljivanja
     */
    private static void azurirajKorisnike(String korisnik) {
        if (korisniciIznajmljivanja.containsKey(korisnik)) {
            korisniciIznajmljivanja.replace(korisnik, (korisniciIznajmljivanja.get(korisnik)) + 1);
        } else {
            korisniciIznajmljivanja.put(korisnik, 1);
        }
    }

    /**
     * Dodaje ili ažurira dnevni izveštaj za dati datum.
     *
     * Ako već postoji izvještaj za dati datum, metoda ažurira postojeći izvještaj
     * dodajući vrijednosti iz novog izvještaja (prihod, popust, promocije, iznos održavanja,
     * iznos popravki kvarova, vožnja u širem i užem dijelu grada).
     * Ako izvještaj za dati datum ne postoji, metoda ga dodaje u kolekciju
     * i prosleđuje izvještaj kontroleru za dalju obradu.
     *
     * @param datumIVrijeme datum i vrijeme iznajmljivanja (samo datum se koristi za ključ)
     * @param dnevniIzvjestaj objekat koji sadrži dnevni izvještaj sa svim relevantnim podacima
     */
    private static void dodajIzvjestaj(String datumIVrijeme, DnevniIzvjestaj dnevniIzvjestaj) {

        String datum = datumIVrijeme.split(" ")[0];

        if (izvjestajiPoDatumu.containsKey(datum)) {
            DnevniIzvjestaj postojeciIzvjestaj = izvjestajiPoDatumu.get(datum);
            postojeciIzvjestaj.setPrihod(postojeciIzvjestaj.getPrihod() + dnevniIzvjestaj.getPrihod());
            postojeciIzvjestaj.setPopust(postojeciIzvjestaj.getPopust() + dnevniIzvjestaj.getPopust());
            postojeciIzvjestaj.setPromocije(postojeciIzvjestaj.getPromocije() + dnevniIzvjestaj.getPromocije());
            postojeciIzvjestaj.setIznosOdrzavanja(postojeciIzvjestaj.getIznosOdrzavanja() + dnevniIzvjestaj.getIznosOdrzavanja());
            postojeciIzvjestaj.setIznosPopravkiKvarova(postojeciIzvjestaj.getIznosPopravkiKvarova() + dnevniIzvjestaj.getIznosPopravkiKvarova());
            postojeciIzvjestaj.setVoznjaUSiremDijeluGrada(postojeciIzvjestaj.getVoznjaUSiremDijeluGrada() + dnevniIzvjestaj.getVoznjaUSiremDijeluGrada());
            postojeciIzvjestaj.setVoznjaUUzemDijeluGrada(postojeciIzvjestaj.getVoznjaUUzemDijeluGrada() + dnevniIzvjestaj.getVoznjaUUzemDijeluGrada());

            izvjestajiPoDatumu.replace(datum, postojeciIzvjestaj);

        } else {
            izvjestajiPoDatumu.put(datum, dnevniIzvjestaj);
            izvjestajKontroler.dodajDnevniIzvjestaj(dnevniIzvjestaj);
        }
    }

    /**
     * Dohvata dnevni izvještaj za dati datum.
     *
     * Metoda vraća izvještaj koji odgovara datom datumu iz kolekcije izvještaja po datumu.
     *
     * @param datum datum za koji se traži izvještaj
     * @return dnevni izvještaj za dati datum
     */
    public static DnevniIzvjestaj getIzvjestaj(String datum) {
        return izvjestajiPoDatumu.get(datum);
    }

    /**
     * Izračunava osnovnu cijenu iznajmljivanja na osnovu tipa prevoznog sredstva i trajanja iznajmljivanja.
     *
     * Metoda koristi cijenu po jedinici za automobil, bicikl ili skuter, u zavisnosti od tipa prevoznog sredstva
     * i množi je sa trajanjima iznajmljivanja kako bi izračunala ukupnu osnovnu cijenu.
     *
     * @param iznajmljivanje objekat koji sadrži podatke o vrsti prevoznog sredstva i trajanju iznajmljivanja
     * @param carUnitPrice cijena po jedinici za automobil
     * @param bikeUnitPrice cijena po jedinici za bicikl
     * @param scooterUnitPrice cijena po jedinici za skuter
     * @return osnovna cijena iznajmljivanja
     */
    private static double getBasicCost(Iznajmljivanje iznajmljivanje, double carUnitPrice, double bikeUnitPrice, double scooterUnitPrice) {
        double unitPrice = getPrice(iznajmljivanje, carUnitPrice, bikeUnitPrice, scooterUnitPrice, false);
        return unitPrice * iznajmljivanje.trajanje;
    }

    /**
     * Vraća cijenu po jedinici za dati tip vozila ili cijenu popravke kvara.
     *
     * Ako je parametar `jeZaIznosPopravkeKvarova` postavljen na `true`, metoda vraća cijenu popravke kvara
     * izračunatu kao proizvod cijene nabavke vozila i odgovarajućeg koeficijenta za popravku.
     * Ako je `jeZaIznosPopravkeKvarova` postavljen na `false`, metoda vraća cijenu po jedinici za odgovarajući
     * tip vozila (automobil, bicikl ili skuter).
     *
     * @param iznajmljivanje objekat koji sadrži informacije o tipu vozila i njegovoj cijeni nabavke
     * @param carUnitPrice cijena po jedinici za automobil
     * @param bikeUnitPrice cijena po jedinici za bicikl
     * @param scooterUnitPrice cijena po jedinici za skuter
     * @param jeZaIznosPopravkeKvarova `true` ako se računa cijena popravke kvara, `false` ako se računa cijena po jedinici
     * @return izračunata cijena na osnovu tipa vozila i da li se radi o cijeni popravke kvara ili cijeni po jedinici
     */
    private static double getPrice(Iznajmljivanje iznajmljivanje, double carUnitPrice, double bikeUnitPrice, double scooterUnitPrice, boolean jeZaIznosPopravkeKvarova) {
        double price;

        if(iznajmljivanje.vozilo.getClass().equals(ElektricniAutomobil.class)) {
            if(jeZaIznosPopravkeKvarova)
                price = iznajmljivanje.vozilo.getCijenaNabavke() * KOEFCIJENT_ZA_POPRAVKU_AUTA;
            else
                price = carUnitPrice;
        }
        else if(iznajmljivanje.vozilo.getClass().equals(ElektricniBicikl.class)) {
            if (jeZaIznosPopravkeKvarova)
                price = iznajmljivanje.vozilo.getCijenaNabavke() * KOEFCIJENT_ZA_POPRAVKU_BICIKLA;
            else
                price = bikeUnitPrice;
        }
        else {
            if (jeZaIznosPopravkeKvarova)
                price = iznajmljivanje.vozilo.getCijenaNabavke() * KOEFCIJENT_ZA_POPRAVKU_TROTINETA;
            else
                price = scooterUnitPrice;
        }

        return price;
    }

    /**
     * Upoređuje trenutni objekat sa drugim objekatom tipa 'Iznajmljivanje' na osnovu datuma i vremena iznajmljivanja.
     *
     * Metoda vraća negativan broj ako je datum i vrijeme trenutnog objekta ranije od onog u drugom objektu,
     * nulti broj ako su datum i vrijeme jednaki, ili pozitivan broj ako je datum i vrijeme trenutnog objekta kasnije.
     *
     * @param other objekat tipa 'Iznajmljivanje' sa kojim se upoređuje trenutni objekat
     * @return negativan broj, nulti broj ili pozitivan broj zavisno od toga da li je datum i vrijeme trenutnog objekta
     * ranije, jednaki ili kasniji u odnosu na datum i vrijeme objekta 'other'
     */
    @Override
    public int compareTo(Iznajmljivanje other) {
        return this.datumIVrijemeIznajmljivanja.compareTo(other.datumIVrijemeIznajmljivanja);
    }

    /**
     * Sortira listu iznajmljivanja koristeći poređenje definisano u metodi 'compareTo'.
     *
     * @param iznajmljivanja lista objekata tipa 'Iznajmljivanje' koji treba da se sortiraju
     */
    public static void sortirajIznajmljivanja(List<Iznajmljivanje> iznajmljivanja) {
        Collections.sort(iznajmljivanja);
    }

    /**
     * Vraća string reprezentaciju objekta 'Iznajmljivanje'.
     *
     * Metoda vraća tekstualni opis objekta koji uključuje sve relevantne informacije poput ID-a vozila, imena korisnika,
     * lokacije preuzimanja i vraćanja, trajanja, datuma i vremena iznajmljivanja, ukupne cijene i informacija o promociji.
     *
     * @return string koji predstavlja objekat 'Iznajmljivanje'
     */
    @Override
    public String toString() {
        return "Iznajmljivanje: " +
                "idVozila=" + vozilo.getId() +
                ", imeKorisnika=" + imeKorisnika +
                ", lokacijaPreuzimanja='" + lokacijaPreuzimanja + '\'' +
                ", lokacijaVracanja='" + lokacijaVracanja + '\'' +
                ", trajanje=" + trajanje +
                ", datumIVrijemeIznajmljivanja=" + datumIVrijemeIznajmljivanja +
                ", ukupnaCijena=" + ukupnaCijena +
                ", promocija=" + imaPromociju;
    }

    public String getTipVozila() {
        return tipVozila;
    }

    public double getUkupnaCijena() {
        return ukupnaCijena;
    }

    public Vozilo getVozilo() {
        return vozilo;
    }

    public String getImeKorisnika() {
        return imeKorisnika;
    }

    public String getLokacijaPreuzimanja() {
        return lokacijaPreuzimanja;
    }

    public String getLokacijaVracanja() {
        return lokacijaVracanja;
    }

    public String getDatumIVrijemeIznajmljivanja() {
        return datumIVrijemeIznajmljivanja;
    }

    public double getTrajanje() {
        return this.trajanje;
    }


}