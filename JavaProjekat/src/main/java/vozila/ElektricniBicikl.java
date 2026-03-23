package vozila;

/**
 * Klasa koja predstavlja električni bicikl i nasljeđuje klasu Vozilo.
 */
public class ElektricniBicikl extends Vozilo {

    private double domet;
    private final String tipVozila = "Bicikl";

    /**
     * Konstruktor za inicijalizaciju objekta ElektricniBicikl.
     *
     * @param id                   Jedinstveni identifikator bicikla.
     * @param cijenaNabavke        Cijena nabavke bicikla.
     * @param proizvodjac          Proizvođač bicikla.
     * @param model                Model bicikla.
     * @param trenutniNivoBaterije Trenutni nivo baterije bicikla.
     * @param domet                Domet bicikla sa jednim punjenjem baterije.
     */
    public ElektricniBicikl(String id, double cijenaNabavke, String proizvodjac, String model, double trenutniNivoBaterije,
                             double domet) {

        super(id, cijenaNabavke, proizvodjac, model, trenutniNivoBaterije);
        this.domet = domet;
    }

    /**
     * Metod koji vraća tip vozila.
     *
     * @return Tip vozila ("Bicikl").
     */
    @Override
    public String getTipVozila() {
        return tipVozila;
    }

    public Double getDomet() {
        return domet;
    }

    /**
     * Vraća string reprezentaciju objekta klase ElektricniBicikl.
     * Ova metoda poziva toString metodu iz nadklase Vozilo da uključi sve osnovne informacije
     * i dodaje specifične informacije za ElektricniBicikl, uključujući domet sa jednim punjenjem.
     *
     * @return string koji predstavlja sve informacije o elektricnom biciklu.
     */
    @Override
    public String toString() {
        return "Električni Bicikl " +
                super.toString() +
                ", dometSaJednimPunjenjem = " + domet +
                " }";
    }
}