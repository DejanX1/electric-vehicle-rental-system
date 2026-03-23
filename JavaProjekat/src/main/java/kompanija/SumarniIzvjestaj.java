package kompanija;

/**
 * Predstavlja sumarni izvještaj iznajmljivanja i implementira interfejs Izvjestaj.
 * Koristi se za dobijanje određenih sumiranih podataka svih dnevnih izvještaja.
 */

public class SumarniIzvjestaj implements Izvjestaj {

    public static Double prihod = 0.0;
    public static Double porez = 0.0;
    public static Double popust = 0.0;
    public static Double promocije = 0.0;
    public static Double iznosOdrzavanja = 0.0;
    public static Double troskoviKompanije = 0.0;
    public static Double iznosPopravkiKvarova = 0.0;
    public static Double voznjaUSiremDijeluGrada = 0.0;
    public static Double voznjaUUzemDijeluGrada = 0.0;

    /**
     * Vraća iznos održavanja zaokružen na tri decimalna mjesta.
     *
     * @return Zaokružen iznos održavanja.
     */
    public Double getIznosOdrzavanja() {
        return DnevniIzvjestaj.zaokruzi(iznosOdrzavanja);
    }

    /**
     * Vraća prihod zaokružen na tri decimalna mjesta.
     *
     * @return Zaokružen prihod.
     */
    public Double getPrihod() {
        return DnevniIzvjestaj.zaokruzi(prihod);
    }

    /**
     * Vraća porez zaokružen na tri decimalna mjesta.
     *
     * @return Zaokružen porez.
     */
    public Double getPorez() {
        return DnevniIzvjestaj.zaokruzi(porez);
    }

    /**
     * Vraća popust zaokružen na tri decimalna mjesta.
     *
     * @return Zaokružen popust.
     */
    public Double getPopust() {
        return DnevniIzvjestaj.zaokruzi(popust);
    }

    /**
     * Vraća iznos promocija zaokružen na tri decimalna mjesta.
     *
     * @return Zaokružen iznos promocija.
     */
    public Double getPromocije() {
        return DnevniIzvjestaj.zaokruzi(promocije);
    }

    /**
     * Vraća troškove kompanije zaokružene na tri decimalna mjesta.
     *
     * @return Zaokruženi troškovi kompanije.
     */
    public Double getTroskoviKompanije() {
        return DnevniIzvjestaj.zaokruzi(troskoviKompanije);
    }

    /**
     * Vraća iznos popravki kvarova zaokružen na tri decimalna mjesta.
     *
     * @return Zaokružen iznos popravki kvarova.
     */
    public Double getIznosPopravkiKvarova() {
        return DnevniIzvjestaj.zaokruzi(iznosPopravkiKvarova);
    }

    /**
     * Vraća iznos vožnji u širem dijelu grada zaokružen na tri decimalna mjesta.
     *
     * @return Zaokružen iznos vožnji u širem dijelu grada.
     */
    public Double getVoznjaUSiremDijeluGrada() {
        return DnevniIzvjestaj.zaokruzi(voznjaUSiremDijeluGrada);
    }

    /**
     * Vraća iznos vožnji u užem dijelu grada zaokružen na tri decimalna mjesta.
     *
     * @return Zaokružen iznos vožnji u užem dijelu grada.
     */
    public Double getVoznjaUUzemDijeluGrada() {
        return DnevniIzvjestaj.zaokruzi(voznjaUUzemDijeluGrada);
    }
}
