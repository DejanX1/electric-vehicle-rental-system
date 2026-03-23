package vozila;

/**
 * Klasa koja predstavlja električni trotinet i nasljeđuje klasu Vozilo.
 */
public class ElektricniTrotinet extends Vozilo {

    private double maksimalnaBrzina;
    private final String tipVozila = "Trotinet";

    /**
     * Konstruktor za inicijalizaciju objekta ElektricniTrotinet.
     *
     * @param id                   Jedinstveni identifikator trotineta.
     * @param cijenaNabavke        Cijena nabavke trotineta.
     * @param proizvodjac          Proizvođač trotineta.
     * @param model                Model trotineta.
     * @param trenutniNivoBaterije Trenutni nivo baterije trotineta.
     * @param maksimalnaBrzina     Maksimalna brzina trotineta.
     */
    public ElektricniTrotinet(String id, double cijenaNabavke,String proizvodjac, String model, double trenutniNivoBaterije,
                               double maksimalnaBrzina) {

        super(id, cijenaNabavke, proizvodjac, model, trenutniNivoBaterije);
        this.maksimalnaBrzina = maksimalnaBrzina;
    }

    /**
     * Metod koji vraća tip vozila.
     *
     * @return Tip vozila ("Trotinet").
     */
    @Override
    public String getTipVozila() {
        return tipVozila;
    }

    public Double getMaksimalnaBrzina() {
        return maksimalnaBrzina;
    }

    /**
     * Vraća string reprezentaciju objekta klase ElektricniTrotinet.
     * Ova metoda poziva toString metodu iz nadklase Vozilo da uključi sve osnovne informacije
     * i dodaje specifične informacije za ElektricniTrotinet, uključujući maksimalnu brzinu.
     *
     * @return string koji predstavlja sve informacije o elektricnom trotinetu.
     */
    @Override
    public String toString() {
        return "Električni Trotinet " +
                super.toString() +
                ", maksimalnaBrzina = " + maksimalnaBrzina +
                " }";
    }
}
