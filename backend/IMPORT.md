# Import af historisk data (Oversigt.csv)

Dette er en engangs-import af de gamle resultater fra Microsoft List'en ind i
databasen, så de dukker op under "historik" i appen ligesom fremtidige events.

## Hvad den gør

- Hver unik værdi i kolonnen **"År"** bliver til ét `Event` med status `CLOSED`
  (navngivet "Dagens Øl {år}").
- Hver række bliver til én `Beer` under det event, med:
  - `name` = Øl
  - `brewery` = Bryggeri
  - `abv` = Procent
  - `submittedBy` = Navn
  - `importedPoints` = Antal stemmer (tomt for 2014, da der ikke findes
    stemmedata for det år)
- Der oprettes **ingen** rigtige `Vote`-rækker (stemmerne var anonyme, så der
  er ingen enkeltstemmer at genskabe) — pointtallet gemmes direkte på øllen.
  De eksisterende resultat-endpoints (`/api/events/{id}/results`,
  `/api/events/history`) lægger automatisk `importedPoints` sammen med
  eventuelle rigtige stemmer, så alt virker uændret for både gamle og nye
  events.
- Værktøjet er sikkert at køre flere gange: et event, hvis navn allerede
  findes i databasen, springes over.

Kolonnerne `Billede`, `Link`, `Aften` og `Rækkefølge` importeres **ikke** —
skemaet har ikke felter til dem. Sig til hvis I ønsker dem med (f.eks.
Untappd-linket), så tilføjer jeg felterne.

## Sådan køres den

1. Sørg for at databasen findes og pege på den rigtige database via
   miljøvariabler (samme som appen selv bruger):

   ```bash
   export POSTGRES_HOST=din-host
   export POSTGRES_DATABASE=dagensoel
   export POSTGRES_USER=dit-brugernavn
   export POSTGRES_PASSWORD=dit-password
   ```

   (Eller kør mod din lokale Postgres uden at sætte noget - så bruges
   `localhost:5432/dagensoel` / postgres/postgres, ligesom appen selv falder
   tilbage til.)

   Da `hibernate.hbm2ddl.auto=update`, opretter/opdaterer Hibernate automatisk
   `beer`-tabellen med den nye `importedpoints`-kolonne, første gang noget
   kører mod databasen (f.eks. når du starter appen, eller når du kører
   dette værktøj).

2. Kør værktøjet fra `backend`-mappen:

   ```bash
   mvn compile exec:java \
     -Dexec.mainClass=dk.dagensoel.tools.ImportHistoricalData \
     -Dexec.args="/sti/til/Oversigt.csv"
   ```

3. Tjek output - den skriver hvor mange events/øl der blev oprettet.

## Bagefter

Alt historisk data ligger nu som lukkede events med resultater. Fremtidige
smagninger opretter I som normalt via admin-flowet i appen (opret event →
tilføj øl → åbn for stemmer → luk → se resultater) - der er ingen ændring i
den arbejdsgang.
