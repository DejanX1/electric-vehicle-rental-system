package kompanija;

import java.util.Collections;
import java.util.List;

/**
 * Predstavlja dnevni izvještaj iznajmljivanja i implementira interfejs Izvjestaj.
 * Ova klasa sadrži metode za generisanje izvještaja na dnevnom nivou.
 */
public class DnevniIzvjestaj implements Izvjestaj, Comparable<DnevniIzvjestaj> {

    private String datumIVrijeme;
    private Double prihod;
    private Double popust;
    private Double promocije;
    private Double iznosOdrzavanja;
    private Double iznosPopravkiKvarova;
    private Double voznjaUSiremDijeluGrada;
    private Double voznjaUUzemDijeluGrada;

    /**
     * Konstruktor za klasu DnevniIzvjestaj.
     *
     * Inicijalizuje objekat sa zadatim datumom i vremenom, dok se svi ostali
     * finansijski atributi postavljaju na nulu.
     *
     * @param datumIVrijeme Datum i vrijeme za koje se generiše dnevni izvještaj.
     */
    public DnevniIzvjestaj(String datumIVrijeme) {
        this.datumIVrijeme = datumIVrijeme;
        this.prihod = 0.0;
        this.popust = 0.0;
        this.promocije = 0.0;
        this.iznosOdrzavanja = 0.0;
        this.iznosPopravkiKvarova = 0.0;
        this.voznjaUSiremDijeluGrada = 0.0;
        this.voznjaUUzemDijeluGrada = 0.0;
    }

    /**
     * Sortira listu dnevnih izvještaja po datumu i vremenu.
     *
     * @param dnevniIzvjestaji Lista dnevnih izvještaja koja se sortira.
     */
    public static void sortirajDnevneIzvjestaje(List<DnevniIzvjestaj> dnevniIzvjestaji) {
        Collections.sort(dnevniIzvjestaji);
    }

    /**
     * Poredi ovaj dnevni izvještaj sa drugim na osnovu datuma i vremena.
     *
     * @param other Drugi dnevni izvještaj za poređenje.
     * @return Negativan broj ako je ovaj dnevni izvještaj prije drugog,
     *         nula ako su isti, pozitivan broj ako je ovaj dnevni izvještaj
     *         poslije drugog.
     */
    @Override
    public int compareTo(DnevniIzvjestaj other) {
        return this.datumIVrijeme.compareTo(other.datumIVrijeme);
    }

    /**
     * Vraća datum i vrijeme izvještaja.
     *
     * @return Datum i vrijeme kao string.
     */
    public String getDatumIVrijeme() {
        return datumIVrijeme;
    }

    /**
     * Vraća prihod zaokružen na tri decimalna mjesta.
     *
     * @return Zaokružen prihod.
     */
    public Double getPrihod() {
        return zaokruzi(prihod);
    }

    /**
     * Vraća popust zaokružen na tri decimalna mjesta.
     *
     * @return Zaokružen popust.
     */
    public Double getPopust() {
        return zaokruzi(popust);
    }

    /**
     * Vraća iznos promocija zaokružen na tri decimalna mjesta.
     *
     * @return Zaokružen iznos promocija.
     */
    public Double getPromocije() {
        return zaokruzi(promocije);
    }

    /**
     * Vraća iznos održavanja zaokružen na tri decimalna mjesta.
     *
     * @return Zaokružen iznos održavanja.
     */
    public Double getIznosOdrzavanja() {
        return zaokruzi(iznosOdrzavanja);
    }

    /**
     * Vraća iznos popravki kvarova zaokružen na tri decimalna mjesta.
     *
     * @return Zaokružen iznos popravki kvarova.
     */
    public Double getIznosPopravkiKvarova() {
        return zaokruzi(iznosPopravkiKvarova);
    }

    /**
     * Vraća iznos vožnji u širem dijelu grada zaokružen na tri decimalna mjesta.
     *
     * @return Zaokružen iznos vožnji u širem dijelu grada.
     */
    public Double getVoznjaUSiremDijeluGrada() {
        return zaokruzi(voznjaUSiremDijeluGrada);
    }

    /**
     * Vraća iznos vožnji u užem dijelu grada zaokružen na tri decimalna mjesta.
     *
     * @return Zaokružen iznos vožnji u užem dijelu grada.
     */
    public Double getVoznjaUUzemDijeluGrada() {
        return zaokruzi(voznjaUUzemDijeluGrada);
    }

    /**
     * Zaokružuje vrijednost na tri decimalna mjesta.
     *
     * Ova metoda uzima decimalni broj i zaokružuje ga na tri decimalna mjesta
     * koristeći matematičko zaokruživanje.
     *
     * @param vrijednost Vrijednost koja treba biti zaokružena.
     * @return Vrijednost zaokružena na tri decimalna mjesta.
     */
    public static double zaokruzi(double vrijednost) {
        return Math.round(vrijednost * 1000.0) / 1000.0;
    }

    public void setDatumIVrijeme(String datumIVrijeme) {
        this.datumIVrijeme = datumIVrijeme;
    }

    public void setPrihod(Double prihod) {
        this.prihod = prihod;
    }

    public void setPopust(Double popust) {
        this.popust = popust;
    }

    public void setPromocije(Double promocije) {
        this.promocije = promocije;
    }

    public void setIznosOdrzavanja(Double iznosOdrzavanja) {
        this.iznosOdrzavanja = iznosOdrzavanja;
    }

    public void setIznosPopravkiKvarova(Double iznosPopravkiKvarova) {
        this.iznosPopravkiKvarova = iznosPopravkiKvarova;
    }

    public void setVoznjaUSiremDijeluGrada(Double voznjaUSiremDijeluGrada) {
        this.voznjaUSiremDijeluGrada = voznjaUSiremDijeluGrada;
    }

    public void setVoznjaUUzemDijeluGrada(Double voznjaUUzemDijeluGrada) {
        this.voznjaUUzemDijeluGrada = voznjaUUzemDijeluGrada;
    }
}
