package iznajmljivanje;

/**
 * Apstraktna klasa koja predstavlja vozača.
 */
public abstract class Vozac {

    private String imeVozaca;

    private static int brojVozaca = 0;

    /**
     * Konstruktor klase Vozac.
     *
     * Inicijalizuje instancu klase Vozac sa imenom vozača i povećava broj registrovanih vozača.
     *
     * @param imeKorisnika Ime vozača koje se dodeljuje ovom vozaču.
     */
    public Vozac(String imeKorisnika) {
        this.imeVozaca = imeKorisnika;
        brojVozaca++;
    }

    public static int getBrojVozaca() {
        return brojVozaca;
    }
}
