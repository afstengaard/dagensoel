package dk.dagensoel.controllers;

import dk.dagensoel.service.HistoricalImportService;
import io.javalin.http.Context;
import io.javalin.http.UploadedFile;

import java.io.IOException;

/**
 * Purpose: lets an admin trigger the historical CSV import from the
 * dashboard instead of running a command-line tool.
 */
public class ImportController {

    private final HistoricalImportService importService = new HistoricalImportService();

    public void importHistorical(Context ctx) {
        UploadedFile file = ctx.uploadedFile("file");

        if (file == null) {
            ctx.status(400).result("No file uploaded (expected form field 'file')");
            return;
        }

        try {
            HistoricalImportService.ImportSummary summary =
                    importService.importFromCsv(file.content());
            ctx.json(summary);
        } catch (IllegalArgumentException e) {
            ctx.status(400).result(e.getMessage());
        } catch (IOException e) {
            ctx.status(500).result("Failed to read CSV: " + e.getMessage());
        } catch (Exception e) {
            ctx.status(500).result("Import failed: " + e.getMessage());
        }
    }
}