package dk.dagensoel.service;

import dk.dagensoel.config.HibernateConfig;
import dk.dagensoel.daos.BeerDAO;
import dk.dagensoel.daos.EventDAO;
import dk.dagensoel.entities.Beer;
import dk.dagensoel.entities.Event;
import dk.dagensoel.entities.EventStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;

/**
 * Imports the historical "Oversigt" CSV export (from the old Microsoft
 * List) into the database. Each distinct "År" (year) becomes one CLOSED
 * Event, and each row becomes a Beer with "Antal stemmer" imported as
 * Beer.importedPoints.
 *
 * Votes in this competition are anonymous, so no individual Vote rows are
 * created - the final point totals are stored directly on the Beer via
 * importedPoints, and VoteDAO.getResultsForEvent() adds this to any
 * (non-existent, for historical events) live votes.
 *
 * Safe to run more than once: an event whose name already exists in the
 * database is skipped, so repeated imports of the same file don't create
 * duplicates.
 */
public class HistoricalImportService {

    private static final List<String> EXPECTED_HEADER = List.of(
            "Øl", "Procent", "Bryggeri", "Navn", "Billede", "Link",
            "Antal stemmer", "Placering", "Aften", "Rækkefølge", "År"
    );

    private final EventDAO eventDAO = new EventDAO();
    private final BeerDAO beerDAO = new BeerDAO();

    public static class ImportSummary {
        public int eventsCreated;
        public int eventsSkipped;
        public int beersCreated;
        public List<String> log = new ArrayList<>();
    }

    public ImportSummary importFromCsv(InputStream csvStream) throws IOException {
        String content = readAll(csvStream);
        // strip UTF-8 BOM if present
        if (!content.isEmpty() && content.charAt(0) == '\uFEFF') {
            content = content.substring(1);
        }

        List<List<String>> records = parseCsv(content);
        if (records.isEmpty()) {
            throw new IllegalArgumentException("CSV file is empty");
        }

        List<String> header = records.get(0);
        if (!header.equals(EXPECTED_HEADER)) {
            throw new IllegalArgumentException(
                    "Unexpected CSV columns. Expected: " + EXPECTED_HEADER + " but got: " + header);
        }

        List<Map<String, String>> rows = toRowMaps(header, records);

        ImportSummary summary = new ImportSummary();

        // Group rows by year, preserving original row order within each year.
        LinkedHashMap<String, List<Map<String, String>>> byYear = new LinkedHashMap<>();
        for (Map<String, String> row : rows) {
            byYear.computeIfAbsent(row.get("År"), k -> new ArrayList<>()).add(row);
        }

        List<String> years = new ArrayList<>(byYear.keySet());
        years.sort(Comparator.naturalOrder()); // chronological insert order

        for (String year : years) {
            String eventName = "Dagens Øl " + year;

            if (eventExists(eventName)) {
                summary.eventsSkipped++;
                summary.log.add("Skipped " + eventName + " (already exists)");
                continue;
            }

            Event event = Event.builder()
                    .name(eventName)
                    .code(eventDAO.generateUniqueCode())
                    .status(EventStatus.CLOSED)
                    .startDate(LocalDate.of(Integer.parseInt(year), 1, 1))
                    .build();

            Event savedEvent = eventDAO.create(event);
            summary.eventsCreated++;

            int beersForYear = 0;
            for (Map<String, String> row : byYear.get(year)) {
                Beer beer = Beer.builder()
                        .name(emptyToNull(row.get("Øl")))
                        .brewery(emptyToNull(row.get("Bryggeri")))
                        .country(null) // not present in the CSV
                        .abv(parseDouble(row.get("Procent")))
                        .submittedBy(emptyToNull(row.get("Navn")))
                        .importedPoints(parseIntOrNull(row.get("Antal stemmer")))
                        .event(savedEvent)
                        .build();

                beerDAO.create(beer);
                summary.beersCreated++;
                beersForYear++;
            }

            summary.log.add("Imported " + eventName + " with " + beersForYear + " beers");
        }

        return summary;
    }

    private boolean eventExists(String name) {
        var emf = HibernateConfig.getEntityManagerFactory();
        try (EntityManager em = emf.createEntityManager()) {
            em.createQuery("SELECT e FROM Event e WHERE e.name = :name", Event.class)
                    .setParameter("name", name)
                    .getSingleResult();
            return true;
        } catch (NoResultException e) {
            return false;
        }
    }

    private static String readAll(InputStream in) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        in.transferTo(buffer);
        return buffer.toString(StandardCharsets.UTF_8);
    }

    private static List<Map<String, String>> toRowMaps(List<String> header, List<List<String>> records) {
        List<Map<String, String>> result = new ArrayList<>();
        for (int i = 1; i < records.size(); i++) {
            List<String> record = records.get(i);
            if (record.size() == 1 && record.get(0).isBlank()) {
                continue; // skip trailing blank lines
            }
            Map<String, String> row = new LinkedHashMap<>();
            for (int col = 0; col < header.size(); col++) {
                row.put(header.get(col), col < record.size() ? record.get(col) : "");
            }
            result.add(row);
        }
        return result;
    }

    private static String emptyToNull(String s) {
        if (s == null) return null;
        String trimmed = s.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private static double parseDouble(String s) {
        String v = emptyToNull(s);
        if (v == null) return 0.0;
        try {
            return Double.parseDouble(v.replace(",", "."));
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    private static Integer parseIntOrNull(String s) {
        String v = emptyToNull(s);
        if (v == null) return null;
        try {
            return Integer.parseInt(v);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    // ---- minimal RFC4180 CSV reader (handles quoted fields, embedded
    // commas and escaped "" quotes) ----

    private static List<List<String>> parseCsv(String content) {
        List<List<String>> records = new ArrayList<>();
        List<String> current = new ArrayList<>();
        StringBuilder field = new StringBuilder();
        boolean inQuotes = false;

        int i = 0;
        int len = content.length();
        while (i < len) {
            char c = content.charAt(i);

            if (inQuotes) {
                if (c == '"') {
                    if (i + 1 < len && content.charAt(i + 1) == '"') {
                        field.append('"');
                        i += 2;
                        continue;
                    } else {
                        inQuotes = false;
                        i++;
                        continue;
                    }
                } else {
                    field.append(c);
                    i++;
                    continue;
                }
            } else {
                if (c == '"') {
                    inQuotes = true;
                    i++;
                } else if (c == ',') {
                    current.add(field.toString());
                    field.setLength(0);
                    i++;
                } else if (c == '\r') {
                    i++; // ignore, \n handles the line break
                } else if (c == '\n') {
                    current.add(field.toString());
                    field.setLength(0);
                    records.add(current);
                    current = new ArrayList<>();
                    i++;
                } else {
                    field.append(c);
                    i++;
                }
            }
        }
        // last field/record if content doesn't end with newline
        if (field.length() > 0 || !current.isEmpty()) {
            current.add(field.toString());
            records.add(current);
        }
        return records;
    }
}