package dat;

import dat.config.ApplicationConfig;
import dat.config.Populate; // Importer din Populate klasse

public class Main {
    public static void main(String[] args) {
        Populate.populateDatabase();

        ApplicationConfig.startServer(7170);
    }
}