package kompanija;

/**
 * Interfejs koji definiše zajedničke metode za pristup podacima iz različitih tipova izveštaja.
 * Implementira se u klasama koje predstavljaju specifične tipove izvještaja kao što su DnevniIzvjestaj i SumarniIzvjestaj.
 */
public interface Izvjestaj {
    Double getPrihod();
    Double getPopust();
    Double getPromocije();
    Double getIznosOdrzavanja();
    Double getIznosPopravkiKvarova();
    Double getVoznjaUSiremDijeluGrada();
    Double getVoznjaUUzemDijeluGrada();
}
