package dk.dagensoel.tools;

import dk.dagensoel.service.HistoricalImportService;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Command-line wrapper around HistoricalImportService, for running the
 * historical CSV import from a terminal/IDE instead of the admin dashboard
 * button.
 *
 * Usage:
 *   mvn compile exec:java \
 *     -Dexec.mainClass=dk.dagensoel.tools.ImportHistoricalData \
 *     -Dexec.args="/path/to/Oversigt.csv"
 *
 * Set POSTGRES_HOST / POSTGRES_DATABASE / POSTGRES_USER / POSTGRES_PASSWORD
 * the same way the app itself expects, to point at the right database.
 */
public class ImportHistoricalData {

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("Usage: ImportHistoricalData <path-to-csv>");
            System.exit(1);
        }

        try (InputStream in = Files.newInputStream(Path.of(args[0]))) {
            HistoricalImportService.ImportSummary summary =
                    new HistoricalImportService().importFromCsv(in);

            summary.log.forEach(System.out::println);
            System.out.println();
            System.out.println("Done. Events created: " + summary.eventsCreated
                    + ", events skipped (already existed): " + summary.eventsSkipped
                    + ", beers created: " + summary.beersCreated);
        }
    }
}
