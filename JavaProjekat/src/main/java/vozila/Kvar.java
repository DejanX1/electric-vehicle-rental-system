package vozila;
import java.io.Serializable;
import java.util.*;

/**
 * Klasa koja predstavlja kvar na prevoznom sredstvu.
 */
public class Kvar implements Serializable {

    private String opis;
    private String datumIVrijeme;
    private String idVozila;
    private String vrstaPrevoznogSredstva;

    /**
     * Lista definisanih opisa kvarova.
     * Ovi opisi predstavljaju najčešće vrste kvarova koji se mogu pojaviti kod prevoznih sredstava.
     *
     * Lista se koristi za nasumično generisanje opisa kvara u metodi getOpisiKvarova.
     */
    public static final List<String> opisiKvarova = Arrays.asList (
            "Pukla guma", "Nestalo ulja za kocnice", "Pregorila sijalica"
    );

    /**
     * Konstruktor klase Kvar.
     *
     * @param opis                   Opis kvara.
     * @param datumIVrijeme          Datum i vrijeme kada je kvar prijavljen.
     * @param idVozila               ID vozila na kojem je kvar nastao.
     * @param vrstaPrevoznogSredstva Vrsta prevoznog sredstva na kojem je kvar nastao.
     */
    public Kvar(String opis, String datumIVrijeme, String idVozila, String vrstaPrevoznogSredstva) {
        this.opis = opis;
        this.datumIVrijeme = datumIVrijeme;
        this.idVozila = idVozila;
        this.vrstaPrevoznogSredstva = vrstaPrevoznogSredstva;
    }

    public String getIdVozila() {
        return idVozila;
    }

    public String getVrstaPrevoznogSredstva() {
        return vrstaPrevoznogSredstva;
    }

    public String getOpis() {
        return this.opis;
    }

    public String getDatumIVrijeme() {
        return this.datumIVrijeme;
    }

    /**
     * Vraća nasumičan opis kvara iz definisanih opisa kvara.
     *
     * @return Nasumičan opis kvara.
     */
    public static String getOpisiKvarova() {
        Random rand = new Random();
        int randInt = rand.nextInt(opisiKvarova.size());

        return opisiKvarova.get(randInt);
    }

    /**
     * Vraća string reprezentaciju objekta klase Kvar.
     *
     * @return String koji predstavlja sve informacije o kvaru.
     */
    @Override
    public String toString() {
        return "Kvar { " +
                "opis = " + opis + ", datum i vrijeme = " + datumIVrijeme + " " + idVozila + " }";
    }
}
