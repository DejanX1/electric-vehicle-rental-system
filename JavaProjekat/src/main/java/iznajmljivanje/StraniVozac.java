package iznajmljivanje;

/**
 * Predstavlja vozače koji su stranog porijekla.
 * Nasljeđuje osnovne karakteristike vozača i dodaje podatke koji su specifični
 * za strane vozače, kao što je identifikacioni dokument.
 */
public class StraniVozac extends Vozac {

    private int identifikacioniDokument;

    /**
     * Konstruktor klase StraniVozac.
     *
     * Inicijalizuje instancu klase StraniVozac sa imenom korisnika i dodjeljuje jedinstveni identifikacioni dokument
     * na osnovu broja registrovanih vozača.
     *
     * @param imeKorisnika Ime korisnika koje se dodjeljuje ovom vozaču.
     */
    public StraniVozac(String imeKorisnika) {
        super(imeKorisnika);
        this.identifikacioniDokument = Vozac.getBrojVozaca();
    }
}
