package vozila;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Apstraktna klasa koja predstavlja osnovu za sve vrste vozila.
 * Implementira interfejs Comparable za poređenje vozila na osnovu ID-a.
 * Takođe, implementira Serializable za omogućavanje serijalizacije objekta.
 */
public abstract class Vozilo implements Comparable<Vozilo>, Serializable {

    private String id;
    private double cijenaNabavke;
    private String proizvodjac;
    private String model;
    private double trenutniNivoBaterije;

    private Kvar kvar;
    private double cijenaPopravkeKvara;
    private boolean imaPromociju = false;

    /**
     * Konstruktor za inicijalizaciju osnovnih atributa vozila.
     *
     * @param id                   Jedinstveni identifikator vozila.
     * @param cijenaNabavke        Cijena nabavke vozila.
     * @param proizvodjac          Proizvođač vozila.
     * @param model                Model vozila.
     * @param trenutniNivoBaterije Trenutni nivo baterije vozila.
     */
    public Vozilo(String id, double cijenaNabavke,String proizvodjac, String model, double trenutniNivoBaterije) {
        this.id = id;
        this.cijenaNabavke = cijenaNabavke;
        this.proizvodjac = proizvodjac;
        this.model = model;
        this.trenutniNivoBaterije = trenutniNivoBaterije;
        this.cijenaPopravkeKvara = 0;
    }

    /**
     * Apstraktna metoda koja mora biti implementirana u potklasama za dobijanje tipa vozila.
     *
     * @return Tip vozila kao String.
     */
    public abstract String getTipVozila();

    /**
     * Metoda za punjenje baterije vozila pri čemu se ne može premašiti vrijednost 100 niti može pasti ispod 0.
     * Ako nivo baterije prilokom punjenja premaši 100, postavlja se na 100.
     * Ako nivo baterije prilikom pražnjenja padne ispod 0, postavlja se na 0.
     *
     * @param kolicina Količina energije za dodavanje u bateriju.
     */
    public void puniBateriju(double kolicina) {
        trenutniNivoBaterije += kolicina;

        if(trenutniNivoBaterije > 100) {
            trenutniNivoBaterije = 100;
        }

        else if(trenutniNivoBaterije < 0) {
            trenutniNivoBaterije = 0;
        }
    }

    /**
     * Metoda za dodavanje kvara vozilu.
     *
     * @param opis                Opis kvara.
     * @param datumIVrijeme       Datum i vrijeme nastanka kvara.
     * @param idVozila            ID vozila na kojem se kvar dogodio.
     * @param vrstaPrevoznogSredstva Tip prevoznog sredstva.
     */
    public void dodajKvar(String opis, String datumIVrijeme, String idVozila, String vrstaPrevoznogSredstva) {
        this.kvar = new Kvar(opis, datumIVrijeme, idVozila, vrstaPrevoznogSredstva);
    }

    /**
     * Metoda za poređenje vozila na osnovu njihovog ID-a.
     *
     * @param vozilo Vozilo s kojim se poredi.
     * @return Rezultat poređenja ID-ova vozila.
     */
    @Override
    public int compareTo(Vozilo vozilo) {
        return this.id.compareTo(vozilo.id);
    }

    /**
     * Metod za dobijanje string reprezentacije vozila.
     *
     * @return String koji predstavlja vozilo.
     */
    @Override
    public String toString() {
        return "{ " +
                "id='" + id + '\'' +
                ", cijenaNabavke=" + cijenaNabavke +
                ", proizvodjac='" + proizvodjac + '\'' +
                ", model='" + model + '\'' +
                ", trenutniNivoBaterije=" + trenutniNivoBaterije +
                ", kvarovi=" + kvar +
                ", promocija=" + imaPromociju +
                '}';
    }

    public String getId() {
        return id;
    }

    public Kvar getKvar() {
        return this.kvar;
    }

    public double getCijenaNabavke() {
        return cijenaNabavke;
    }

    public String getProizvodjac() {
        return proizvodjac;
    }

    public String getModel() {
        return model;
    }

    public double getTrenutniNivoBaterije() {
        return trenutniNivoBaterije;
    }

    public double getCijenaPopravkeKvara() {
        return cijenaPopravkeKvara;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setCijenaNabavke(double cijenaNabavke) {
        this.cijenaNabavke = cijenaNabavke;
    }

    public void setProizvodjac(String proizvodjac) {
        this.proizvodjac = proizvodjac;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setTrenutniNivoBaterije(double trenutniNivoBaterije) {
        this.trenutniNivoBaterije = trenutniNivoBaterije;
    }

    public void setKvar(Kvar kvar) {
        this.kvar = kvar;
    }

    public void setCijenaPopravkeKvara(double cijenaPopravkeKvara) {
        this.cijenaPopravkeKvara = cijenaPopravkeKvara;
    }

    public void setImaPromociju(boolean imaPromociju) {
        this.imaPromociju = imaPromociju;
    }
}
