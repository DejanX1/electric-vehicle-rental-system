package kontroleri;

import main.Main;
import simulacija.Simulacija;
import vozila.ElektricniAutomobil;
import vozila.ElektricniBicikl;
import vozila.ElektricniTrotinet;
import vozila.Vozilo;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.util.HashMap;
import java.util.Map;

/**
 * Kontroler za upravljanje prikazom mape u aplikaciji.
 * Ova klasa upravlja kreiranjem i ažuriranjem prikaza mape i vozila u GridPane.
 */
public class MapaController {

    private static final int BROJ_REDOVA = 20;
    private static final int BROJ_KOLONA = 20;
    public static Simulacija simulacija;

    @FXML
    private AnchorPane pane;
    @FXML
    private Button dugmeKvarovi;
    @FXML
    private Button dugmePokvarenaVozila;
    @FXML
    private Button dugmePrevoznaSredstva;
    @FXML
    private Button dugmeStart;
    @FXML
    public Button dugmePoslovanja;
    @FXML
    private GridPane gridPane;

    public static Boolean seZavrsilaSimulacija = false;

    @FXML
    public void initialize() {
        generisiMapu();
    }

    /**
     * Generiše mapu ćelija u GridPane i postavlja stil za svaku ćeliju.
     */
    public void generisiMapu() {
        for(int i = 0; i < BROJ_REDOVA; i++){
            for(int j = 0; j < BROJ_KOLONA; j++){
                Region cell = new Region();
                cell.setPrefSize(CELL_WIDTH, CELL_HEIGHT);  // Set size of each cell

                // Set the background color based on the condition
                if (i >= 5 && i <= 14 && j >= 5 && j <= 14) {
                    cell.setStyle("-fx-background-color: #00BFFF; -fx-border-color: black;");
                } else {
                    cell.setStyle("-fx-background-color: white;-fx-border-color: black;");
                }

                //Text text = new Text(getIdentifierForCell(i, j));
                Text text = new Text("");

                // Create a StackPane to hold the cell and text
                StackPane stack = new StackPane();
                stack.getChildren().addAll(cell, text);
                gridPane.add(stack, j, i);  // Add StackPane to the gridPane at (j, i)
            }
        }

        //gridPane.setHgap(2); // Postavlja horizontalni razmak između ćelija na 1 piksel
        //gridPane.setVgap(2); // Postavlja vertikalni razmak između ćelija na 1 piksel
    }

    /**
     * Prikazuje prozor za prikaz prevoznih sredstava.
     *
     * @param event Akcija koja je pokrenula prikaz.
     * @throws Exception Ako dođe do greške prilikom učitavanja FXML fajla.
     */
    @FXML
    void prikaziPrevoznaSredstva(ActionEvent event) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/fxml/PrikazSvihPrevoznihSredstava.fxml"));
        Stage stage = new Stage();
        stage.setTitle("Prikaz svih prevoznih sredstava");
        stage.setScene(new Scene(fxmlLoader.load()));
        stage.show();
    }

    private Map<Vozilo, int[]> vehiclePositions = new HashMap<>();
    private static final double CELL_WIDTH = 46.4;
    private static final double CELL_HEIGHT = 28.0;

    /**
     * Ažurira poziciju vozila na mapi.
     *
     * @param x Pozicija x u mreži.
     * @param y Pozicija y u mreži.
     * @param vozilo Vozilo koje treba prikazati.
     */
    public void updateVehiclePosition(int x, int y, Vozilo vozilo) {
        if (vozilo != null) {
            Platform.runLater(() -> {
                try {
                    // Ukloni vozilo sa stare pozicije ako postoji
                    if (vehiclePositions.containsKey(vozilo)) {
                        int[] oldPos = vehiclePositions.get(vozilo);
                        removeVehicleFromPosition(oldPos[0], oldPos[1]);
                    }

                    // Postavi vozilo na novu poziciju
                    Label label = new Label(vozilo.getId() + "," + vozilo.getTrenutniNivoBaterije());
                    label.setAlignment(Pos.CENTER);

                    // Postavi veličinu labela tako da odgovara veličini ćelije
                    label.setPrefSize(CELL_WIDTH, CELL_HEIGHT);
                    label.setMaxSize(CELL_WIDTH, CELL_HEIGHT);
                    label.setMinSize(CELL_WIDTH, CELL_HEIGHT);

                    // Dodaj boju na osnovu tipa vozila
                    if (vozilo instanceof ElektricniAutomobil) {
                        label.setStyle("-fx-background-color: green; -fx-text-fill: black;");
                    } else if (vozilo instanceof ElektricniTrotinet) {
                        label.setStyle("-fx-background-color: yellow; -fx-text-fill: black;");
                    } else if (vozilo instanceof ElektricniBicikl) {
                        label.setStyle("-fx-background-color: red; -fx-text-fill: black;");
                    }

                    // Dodaj StackPane ili nađi postojeći i dodaj labelu
                    Node node = findNodeByRowColumnIndex(y, x, gridPane);
                    if (node != null && node instanceof StackPane) {
                        StackPane stack = (StackPane) node;
                        stack.getChildren().add(label);
                    }

                    // Ažuriraj mapu sa pozicijama vozila
                    vehiclePositions.put(vozilo, new int[]{x, y});
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    /**
     * Uklanja vozilo sa date pozicije.
     *
     * @param x Pozicija x u mreži.
     * @param y Pozicija y u mreži.
     */
    private void removeVehicleFromPosition(int x, int y) {
        Platform.runLater(() -> {
            try {
                // Pronađi StackPane na poziciji (x, y)
                Node node = findNodeByRowColumnIndex(y, x, gridPane);
                if (node != null && node instanceof StackPane) {
                    StackPane stack = (StackPane) node;
                    // Ukloni sve Label čvorove iz StackPane
                    stack.getChildren().removeIf(child -> child instanceof Label);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Pronalazi Node u GridPane na osnovu rednog i kolonskog indeksa.
     *
     * @param row Redni indeks.
     * @param column Kolonski indeks.
     * @param gridPane GridPane u kojem se traži Node.
     * @return Node na datoj poziciji ili null ako nije pronađen.
     */
    private Node findNodeByRowColumnIndex(final int row, final int column, GridPane gridPane) {
        for (Node node : gridPane.getChildren()) {
            Integer rowIndex = GridPane.getRowIndex(node);
            Integer columnIndex = GridPane.getColumnIndex(node);

            if (rowIndex != null && rowIndex == row && columnIndex != null && columnIndex == column) {
                return node;
            }
        }
        return null;
    }

    /**
     * Prikazuje prozor za prikaz kvarova.
     *
     * @param event Akcija koja je pokrenula prikaz.
     * @throws Exception Ako dođe do greške prilikom učitavanja FXML fajla.
     */
    @FXML
    void prikaziKvarove(ActionEvent event) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/fxml/PrikazKvarova.fxml"));
        Stage stage = new Stage();
        stage.setTitle("Prikaz kvarova");
        stage.setScene(new Scene(fxmlLoader.load()));
        stage.show();
    }

    /**
     * Prikazuje rezultate poslovanja.
     *
     * @param event Akcija koja je pokrenula prikaz.
     * @throws Exception Ako dođe do greške prilikom učitavanja FXML fajla.
     */
    @FXML
    void prikaziRezultatePoslovanja(ActionEvent event) throws Exception {
        if(seZavrsilaSimulacija) {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/fxml/IzvjestajController.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Rezultati poslovanja");
            stage.setScene(new Scene(fxmlLoader.load()));
            stage.show();
        }
        else {
            Stage stage = new Stage();
            stage.setTitle("Обавјештење");

            Label label = new Label("Није могуће отворити док се не заврши симулација!");
            StackPane root = new StackPane();
            root.getChildren().add(label);

            Scene scene = new Scene(root, 300, 100); // Dimenzije prozora

            stage.setScene(scene);
            stage.show();
        }
    }

    /**
     * Pokreće simulaciju i onemogućava dugme za start.
     *
     * @param event Akcija koja je pokrenula start simulacije.
     */
    @FXML
    void start(ActionEvent event) {

            dugmeStart.setDisable(true);

            Simulacija simulacija = new Simulacija();
            Main.controller.setSimulacija(simulacija);

            simulacija.start();
    }

    /**
     * Postavlja novu simulaciju.
     *
     * @param novaSimulacija Nova instanca simulacije.
     */
    public void setSimulacija(Simulacija novaSimulacija) {
        MapaController.simulacija = novaSimulacija;
    }
}

