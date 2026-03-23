package simulacija;
import vozila.Vozilo;

/**
 * Predstavlja mapu grada koja je sačinjena od užeg i šireg dijela grada, a
 * na kojoj se prikazuje kretanje vozila prilikom iznajmljivanja.
 *
 * Ova klasa definiše mapu kao dvodimenzionalni niz vozila gde svaka pozicija može sadržati vozilo
 * ili biti prazna. Mapa ima fiksnu širinu i visinu, koje su definisane kao konstante.
 *
 * Klasa omogućava postavljanje i uklanjanje vozila na određenim koordinatama unutar mape.
 */
public class Mapa {

    /**
     * Konstantna vrednost koja predstavlja širinu mape u koordinatama X.
     * Ova dimenzija definiše broj kolona u matrici mape.
     */
    private static final int X_DIMENZIJA = 20;

    /**
     * Konstantna vrednost koja predstavlja visinu mape u koordinatama Y.
     * Ova dimenzija definiše broj redova u matrici mape.
     */
    private static final int Y_DIMENZIJA = 20;

    /**
     * Staticki atribut koji predstavlja mapu vozila u prostoru.
     * Mapa je dvodimenzionalni niz koji se koristi za smještanje vozila na određene koordinate.
     */
    private static Vozilo[][] mapa = new Vozilo[X_DIMENZIJA][Y_DIMENZIJA];

    /**
     * Konstruktor klase Mapa.
     *
     * Inicijalizuje mapu vozila tako što postavlja sve pozicije u matrici na null, što znači da nema vozila
     * na tim pozicijama pri kreiranju mape.
     */
    public Mapa() {
        for (int i = 0; i < X_DIMENZIJA; i++) {
            for (int j = 0; j < Y_DIMENZIJA; j++) {
                mapa[i][j] = null;
            }
        }
    }

    /**
     * Metoda za postavljanje vozila na određene koordinate u mapi.
     *
     * @param vozilo Vozilo koje se postavlja na mapu.
     * @param x X-koordinata na kojoj se vozilo postavlja.
     * @param y Y-koordinata na kojoj se vozilo postavlja.
     */
    public void postaviVozilo(Vozilo vozilo, int x, int y) {
        mapa[x][y] = vozilo;
    }

    /**
     * Metoda za uklanjanje vozila sa određenih koordinata u mapi.
     *
     * @param x X-koordinata sa koje se vozilo uklanja.
     * @param y Y-koordinata sa koje se vozilo uklanja.
     */
    public void ukloniVozilo(int x, int y) {
        mapa[x][y] = null;
    }

}


