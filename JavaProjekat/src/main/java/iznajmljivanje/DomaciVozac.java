package iznajmljivanje;

/**
 * Predstavlja vozače koji su državljani domaće zemlje.
 * Nasljeđuje osnovne karakteristike vozača i dodatno uključuje specifične
 * podatke koji su relevantni za domaće vozace kao što je vozačka dozvola.
 */
public class DomaciVozac extends Vozac {

    private int vozackaDozvola;

    /**
     * Konstruktor klase DomaciVozac.
     *
     * Inicijalizuje instancu klase DomaciVozac sa imenom korisnika i dodeljuje jedinstveni broj vozačke dozvole
     * na osnovu broja registrovanih vozača.
     *
     * @param imeKorisnika Ime korisnika koje se dodeljuje ovom vozaču.
     */
    public DomaciVozac(String imeKorisnika) {
        super(imeKorisnika);
        this.vozackaDozvola = Vozac.getBrojVozaca();
    }
}
