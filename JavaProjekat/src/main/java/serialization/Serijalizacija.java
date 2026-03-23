package serialization;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import vozila.Vozilo;

/**
 * Klasa za serijalizaciju vozila.
 */
public class Serijalizacija {

    /**
     * Serijalizuje objekat Vozilo zajedno sa iznosom popravke u datoteku.
     *
     * @param vozilo Objekat Vozilo koji treba da se serijalizuje.
     * @param iznosPopravke Iznos popravke koji treba da se serijalizuje.
     * @param folderPath Putanja do foldera gdje će datoteka biti sačuvana.
     */
    public static void serijalizuj(Vozilo vozilo, double iznosPopravke, String folderPath) {
        try {
            FileOutputStream fileOut = new FileOutputStream(folderPath + "/" + vozilo.getId() + ".ser");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(vozilo);
            out.writeDouble(iznosPopravke);
            out.close();
            fileOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
