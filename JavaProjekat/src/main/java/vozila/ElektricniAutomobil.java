package vozila;

/**
 * Klasa koja predstavlja električni automobil i nasljeđuje klasu Vozilo.
 */
public class ElektricniAutomobil extends Vozilo {

    private boolean prevoziViseLjudi;
    private String opis;
    private String datumNabavke;

    private final String tipVozila = "Automobil";

    /**
     * Konstruktor za inicijalizaciju objekta ElektricniAutomobil.
     *
     * @param id                   Jedinstveni identifikator automobila.
     * @param datumNabavke         Datum kada je automobil nabavljen.
     * @param cijenaNabavke        Cijena nabavke automobila.
     * @param proizvodjac          Proizvođač automobila.
     * @param model                Model automobila.
     * @param opis                 Opis automobila.
     * @param trenutniNivoBaterije Trenutni nivo baterije automobila.
     * @param prevoziViseLjudi     Da li automobil prevoziti više ljudi.
     */
    public ElektricniAutomobil(String id, String datumNabavke, double cijenaNabavke, String proizvodjac, String model, String opis, double trenutniNivoBaterije,
                               boolean prevoziViseLjudi) {

        super(id, cijenaNabavke, proizvodjac, model, trenutniNivoBaterije);

        this.opis = opis;
        this.prevoziViseLjudi = prevoziViseLjudi;
        this.datumNabavke = datumNabavke;
    }

    /**
     * Metoda za dobijanje string reprezentacije električnog automobila.
     *
     * @return String koji predstavlja električni automobil.
     */
    @Override
    public String toString() {
        return "Elektricni Automobil " +
                super.toString() +
                ", opis = " + opis +
                ", datumNabavke = " + datumNabavke +
                ", prevoziViseLjudi = " + prevoziViseLjudi +
                " }";
    }

    public String getOpis() {
        return opis;
    }

    public String getDatumNabavke() {
        return datumNabavke;
    }

    public Boolean getPrevoziViseLjudi() {
        return prevoziViseLjudi;
    }

    /**
     * Vraća string reprezentaciju objekta klase ElektricniAutomobil.
     * Ova metoda poziva toString metodu iz nadklase Vozilo da uključi sve osnovne informacije
     * i dodaje specifične informacije za ElektricniAutomobil, uključujući opis, datum nabavke i
     * mogućnost prevoza više ljudi.
     *
     * @return string koji predstavlja sve informacije o elektricnom automobilu.
     */
    @Override
    public String getTipVozila() {
        return tipVozila;
    }
}
