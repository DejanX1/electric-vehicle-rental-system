package kontroleri;

import kompanija.DnevniIzvjestaj;
import kompanija.Izvjestaj;
import kompanija.SumarniIzvjestaj;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * Kontroler za prikaz izvještaja u tabeli.
 * Ova klasa upravlja podacima i interakcijama sa TableView u korisničkom interfejsu.
 */
public class IzvjestajController {
    @FXML
    private TableView<Izvjestaj> rezultatiPoslovanjaTable;
    @FXML
    private TableColumn<Izvjestaj, Double> iznosOdrzavanjaColumn;
    @FXML
    private TableColumn<Izvjestaj, Double> iznosPopravkiKvarovaColumn;
    @FXML
    private TableColumn<Izvjestaj, Double> popustColumn;
    @FXML
    private TableColumn<Izvjestaj, Double> porezColumn;
    @FXML
    private TableColumn<Izvjestaj, Double> prihodColumn;
    @FXML
    private TableColumn<Izvjestaj, Double> promocijeColumn;
    @FXML
    private TableColumn<Izvjestaj, Double> troskoviKompanijeColumn;
    @FXML
    private TableColumn<Izvjestaj, Double> voznjaUSiremDijeluGradaColumn;
    @FXML
    private TableColumn<Izvjestaj, Double> voznjaUUzemDijeluGradaColumn;
    @FXML
    private TableColumn<Izvjestaj, String> terminColumn;

    // Dnevni izvjestaji
    public static ObservableList<DnevniIzvjestaj> dnevniIzvjestaj = FXCollections.observableArrayList();

    // Dodavanje statičkih podataka u tabelu
    ObservableList<Izvjestaj> sumarniIzvjestaj = FXCollections.observableArrayList(
            new SumarniIzvjestaj() // Dodavanje jedne instance sa statičkim vrednostima
    );

    /**
     * Inicijalizuje kolone u tabeli i postavlja njihove vrijednosti.
     * Poziva se automatski nakon učitavanja FXML fajla.
     */
    @FXML
    public void initialize() {
        terminColumn.setCellValueFactory(new PropertyValueFactory<>("datumIVrijeme"));
        prihodColumn.setCellValueFactory(new PropertyValueFactory<>("prihod"));
        popustColumn.setCellValueFactory(new PropertyValueFactory<>("popust"));
        promocijeColumn.setCellValueFactory(new PropertyValueFactory<>("promocije"));
        voznjaUSiremDijeluGradaColumn.setCellValueFactory(new PropertyValueFactory<>("voznjaUSiremDijeluGrada"));
        voznjaUUzemDijeluGradaColumn.setCellValueFactory(new PropertyValueFactory<>("voznjaUUzemDijeluGrada"));
        iznosOdrzavanjaColumn.setCellValueFactory(new PropertyValueFactory<>("iznosOdrzavanja"));
        iznosPopravkiKvarovaColumn.setCellValueFactory(new PropertyValueFactory<>("iznosPopravkiKvarova"));

        porezColumn.setCellValueFactory(new PropertyValueFactory<>("porez"));
        troskoviKompanijeColumn.setCellValueFactory(new PropertyValueFactory<>("troskoviKompanije"));

        rezultatiPoslovanjaTable.getColumns().clear();
    }

    /**
     * Dodaje novi dnevni izvještaj u listu dnevnih izvještaja.
     *
     * @param noviDnevniIzvjestaj Novi dnevni izvještaj koji treba dodati.
     * @throws IllegalArgumentException Ako je dnevni izvještaj null.
     */
    @FXML
    public void dodajDnevniIzvjestaj(DnevniIzvjestaj noviDnevniIzvjestaj) {
        if (dnevniIzvjestaj == null) {
            throw new IllegalArgumentException("Dnevni izvjestaj ne može biti null!");
        }

        dnevniIzvjestaj.add(noviDnevniIzvjestaj);
    }

    /**
     * Prikazuje sumarni izvještaj u tabeli.
     *
     * @param event Akcija koja je pokrenula prikaz.
     */
    @FXML
    public void prikaziSumarniIzvjestaj(ActionEvent event) {
        rezultatiPoslovanjaTable.getColumns().clear();
        rezultatiPoslovanjaTable.getColumns().addAll(prihodColumn, popustColumn, promocijeColumn,
                voznjaUSiremDijeluGradaColumn, voznjaUUzemDijeluGradaColumn, iznosOdrzavanjaColumn,
                iznosPopravkiKvarovaColumn, porezColumn, troskoviKompanijeColumn);

        rezultatiPoslovanjaTable.setItems(sumarniIzvjestaj);
    }

    /**
     * Prikazuje dnevni izvještaj u tabeli.
     *
     * @param event Akcija koja je pokrenula prikaz.
     */
    @FXML
    public void prikaziDnevniIzvjestaj(ActionEvent event) {
        rezultatiPoslovanjaTable.getColumns().clear();
        rezultatiPoslovanjaTable.getColumns().addAll(terminColumn, prihodColumn, popustColumn, promocijeColumn,
        voznjaUSiremDijeluGradaColumn, voznjaUUzemDijeluGradaColumn, iznosOdrzavanjaColumn, iznosPopravkiKvarovaColumn);

        ObservableList<Izvjestaj> observableDnevniIzvjestaj = FXCollections.observableArrayList(dnevniIzvjestaj);
        rezultatiPoslovanjaTable.setItems(observableDnevniIzvjestaj);
    }
}
