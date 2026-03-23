package kontroleri;
import vozila.Kvar;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * Kontroler za prikaz kvarova u tabeli.
 * Ova klasa upravlja prikazom kvarova u TableView komponenti.
 */
public class PrikazKvarovaController {

    @FXML
    private TableView<Kvar> tabelaKvarova;

    @FXML
    private TableColumn<Kvar, String> datumIVrijemeColumn;

    @FXML
    private TableColumn<Kvar, String> opisColumn;

    @FXML
    private TableColumn<Kvar, String> idColumn;

    @FXML
    private TableColumn<Kvar, String> vrstaPrevoznogSredstvaColumn;

    private static ObservableList<Kvar> kvarovi = FXCollections.observableArrayList();

    /**
     * Inicijalizuje TableView i postavlja kolone na odgovarajuće vrednosti iz objekata Kvar.
     */
    @FXML
    public void initialize() {
        datumIVrijemeColumn.setCellValueFactory(new PropertyValueFactory<>("datumIVrijeme"));
        opisColumn.setCellValueFactory(new PropertyValueFactory<>("opis"));
        idColumn.setCellValueFactory(new PropertyValueFactory<>("idVozila"));
        vrstaPrevoznogSredstvaColumn.setCellValueFactory(new PropertyValueFactory<>("vrstaPrevoznogSredstva"));

        tabelaKvarova.setItems(kvarovi);
    }

    /**
     * Dodaje novi kvar u tabelu.
     *
     * @param kvar Novi kvar koji treba dodati.
     * @throws IllegalArgumentException Ako je prosleđeni kvar null.
     */
    @FXML
    public void dodajKvar(Kvar kvar) {
        if (kvar == null) {
            throw new IllegalArgumentException("Tip vozila ne može biti null!");
        }

        PrikazKvarovaController.kvarovi.add(kvar);
    }
}