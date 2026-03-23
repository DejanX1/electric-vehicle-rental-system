package kontroleri;
import vozila.Vozilo;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * Kontroler za prikaz svih prevoznih sredstava.
 * Ova klasa upravlja tabelom koja prikazuje različite tipove prevoznih sredstava
 * i omogućava filtriranje na osnovu tipa vozila.
 */
public class PrikazSvihPrevoznihSredstavaController {

    @FXML
    private AnchorPane anchorPane;
    @FXML
    public Button btnNazad;
    Stage stage;

    @FXML
    public TableView<Vozilo> rentalTableView;
    @FXML
    private TableColumn<Vozilo, String> cijenaNabavkeColumn;
    @FXML
    private TableColumn<Vozilo, String> datumNabavkeColumn;
    @FXML
    private TableColumn<Vozilo, Double> dometColumn;
    @FXML
    private TableColumn<Vozilo, String> idColumn;
    @FXML
    private TableColumn<Vozilo, Double> maxBrzinaColumn;
    @FXML
    private TableColumn<Vozilo, String> modelColumn;
    @FXML
    private TableColumn<Vozilo, String> prevoziViseLjudiColumn;
    @FXML
    private TableColumn<Vozilo, Double> trenutniNivoBaterijeColumn;
    @FXML
    private TableColumn<Vozilo, String> opisColumn;
    @FXML
    private TableColumn<Vozilo, Double> proizvodjacColumn;
    @FXML
    private TableColumn<Vozilo, Double> cijenaPopravkeColumn;

    public static ObservableList<Vozilo> automobili = FXCollections.observableArrayList();
    public static ObservableList<Vozilo> trotineti = FXCollections.observableArrayList();
    public static ObservableList<Vozilo> bicikli = FXCollections.observableArrayList();
    public static ObservableList<Vozilo> pokvarenaVozila = FXCollections.observableArrayList();

    @FXML
    private ComboBox<String> tipVozilaComboBox;

    /**
     * Inicijalizuje TableView i ComboBox.
     * Postavlja kolone za TableView i dodaje opcije u ComboBox za izbor tipa vozila.
     */
    public void initialize() {
        cijenaNabavkeColumn.setCellValueFactory(new PropertyValueFactory<>("cijenaNabavke"));//
        datumNabavkeColumn.setCellValueFactory(new PropertyValueFactory<>("datumNabavke"));
        dometColumn.setCellValueFactory(new PropertyValueFactory<>("domet"));
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));//
        maxBrzinaColumn.setCellValueFactory(new PropertyValueFactory<>("maksimalnaBrzina"));
        modelColumn.setCellValueFactory(new PropertyValueFactory<>("model"));//
        prevoziViseLjudiColumn.setCellValueFactory(new PropertyValueFactory<>("prevoziViseLjudi"));
        trenutniNivoBaterijeColumn.setCellValueFactory(new PropertyValueFactory<>("trenutniNivoBaterije"));//
        opisColumn.setCellValueFactory(new PropertyValueFactory<>("opis"));
        proizvodjacColumn.setCellValueFactory(new PropertyValueFactory<>("proizvodjac"));//
        cijenaPopravkeColumn.setCellValueFactory(new PropertyValueFactory<>("cijenaPopravkeKvara"));

        tipVozilaComboBox.setItems(FXCollections.observableArrayList("Automobil", "Trotinet", "Bicikl", "Pokvarena"));
        tipVozilaComboBox.setValue("Одабери возило");
        rentalTableView.getColumns().clear();
    }

    /**
     * Dodaje vozilo u odgovarajuću listu na osnovu tipa vozila.
     *
     * @param vozilo Vozilo koje treba dodati.
     * @throws IllegalArgumentException Ako je tip vozila null.
     */
    @FXML
    public void dodajVozilo(Vozilo vozilo) {
        if (vozilo.getTipVozila() == null) {
            throw new IllegalArgumentException("Tip vozila ne može biti null!");
        }

        switch(vozilo.getTipVozila()) {
            case "Automobil" :
                automobili.add(vozilo);
                break;
            case "Trotinet" :
                trotineti.add(vozilo);
                break;
            case "Bicikl" :
                bicikli.add(vozilo);
                break;
        }
    }

    /**
     * Dodaje pokvareno vozilo u listu pokvarenih vozila.
     *
     * @param vozilo Pokvareno vozilo koje treba dodati.
     */
    @FXML
    public void dodajPokvarenoVozilo(Vozilo vozilo) {
        pokvarenaVozila.add(vozilo);
    }

    /**
     * Zatvara trenutnu scenu kada se klikne dugme za povratak.
     *
     * @param event Akcija na klik dugmeta.
     */
    @FXML
    public void exit(ActionEvent event) {
        stage = (Stage) btnNazad.getScene().getWindow();
        stage.close();
    }

    /**
     * Filtrira prikaz u tabeli na osnovu izabranog tipa vozila u ComboBox-u.
     *
     * @param event Akcija na izbor tipa vozila.
     */
    @FXML
    @SuppressWarnings("unchecked")
    public void filterByTipVozila(ActionEvent event) {
        String selectedTip = tipVozilaComboBox.getValue();
        rentalTableView.getColumns().clear();

        rentalTableView.getColumns().addAll(idColumn, cijenaNabavkeColumn, proizvodjacColumn, modelColumn, trenutniNivoBaterijeColumn);

        switch (selectedTip) {
            case "Automobil" :
                rentalTableView.setItems(automobili);
                rentalTableView.getColumns().add(prevoziViseLjudiColumn);
                rentalTableView.getColumns().add(opisColumn);
                rentalTableView.getColumns().add(datumNabavkeColumn);
                break;
            case "Trotinet" :
                rentalTableView.setItems(trotineti);
                rentalTableView.getColumns().add(maxBrzinaColumn);
                break;
            case "Bicikl" :
                rentalTableView.setItems(bicikli);
                rentalTableView.getColumns().add(dometColumn);
                break;
            case "Pokvarena" :
                rentalTableView.setItems(pokvarenaVozila);

                rentalTableView.getColumns().add(cijenaPopravkeColumn);
                break;
        }
    }
}