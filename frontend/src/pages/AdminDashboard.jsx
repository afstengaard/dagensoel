import { useEffect, useState } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import api from "../api/apiFacade";

const STATUS_LABELS = {
  OPEN: "Åben",
  VOTING: "Afstemning",
  CLOSED: "Lukket",
};

export default function AdminDashboard() {
  const [event, setEvent] = useState(null);
  const [pastEvents, setPastEvents] = useState([]);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();
  const location = useLocation();
  const [beer, setBeer] = useState({
    name: "",
    brewery: "",
    country: "",
    abv: "",
    submittedBy: "",
  });

  useEffect(() => {
    async function load() {
      try {
        const active = await api.getActiveEvent();
        setEvent(active);
      } catch {
        // Intet aktivt event → helt fint
        setEvent(null);
      } finally {
        setLoading(false);
      }

      try {
        const history = await api.getEventHistory();
        setPastEvents(history);
      } catch {
        setPastEvents([]);
      }
    }

    load();
  }, [location.pathname]);

  async function reloadPastEvents() {
    try {
      const history = await api.getEventHistory();
      setPastEvents(history);
    } catch {
      setPastEvents([]);
    }
  }

  async function removeEvent(id, name) {
    if (!confirm(`Slet "${name}" og alle dens øl/stemmer? Dette kan ikke fortrydes.`)) {
      return;
    }

    try {
      await api.deleteEvent(id);
      await reloadPastEvents();
    } catch {
      alert("Kunne ikke slette event");
    }
  }

  const [importFile, setImportFile] = useState(null);
  const [importing, setImporting] = useState(false);
  const [importStatus, setImportStatus] = useState(null);

  async function runImport() {
    if (!importFile) {
      alert("Vælg en CSV-fil først");
      return;
    }

    setImporting(true);
    setImportStatus(null);
    try {
      const summary = await api.importHistoricalData(importFile);
      setImportStatus(
        `Færdig: ${summary.eventsCreated} event(s) oprettet, ` +
          `${summary.eventsSkipped} sprunget over (fandtes allerede), ` +
          `${summary.beersCreated} øl oprettet.`
      );
      setImportFile(null);
      await reloadPastEvents();
    } catch (err) {
      setImportStatus(`Import mislykkedes: ${err.message}`);
    } finally {
      setImporting(false);
    }
  }

  async function reloadEvent() {
    try {
      const active = await api.getActiveEvent();
      setEvent(active);
    } catch {
      setEvent(null);
    }
  }

  async function changeStatus(nextStatus) {
    try {
      const updated = await api.updateEventStatus(event.id, nextStatus);
      setEvent(updated);
    } catch {
      alert("Kunne ikke opdatere event-status");
    }
  }

  async function addBeer() {
    const name = beer.name.trim();
    const brewery = beer.brewery.trim();
    const country = beer.country.trim();
    const submittedBy = beer.submittedBy.trim();
    const abv = Number(beer.abv);

    // Tekstvalidering
    if (!name || !brewery || !country || !submittedBy) {
      alert("Alle tekstfelter skal udfyldes");
      return;
    }

    // ABV-validering
    if (isNaN(abv)) {
      alert("ABV skal være et tal");
      return;
    }

    if (abv <= 0) {
      alert("ABV skal være større end 0");
      return;
    }

    try {
      await api.addBeerToEvent(event.id, {
        name,
        brewery,
        country,
        abv,
        submittedBy,
      });

      setBeer({
        name: "",
        brewery: "",
        country: "",
        abv: "",
        submittedBy: "",
      });

      await reloadEvent();
    } catch {
      alert("Kunne ikke tilføje øl");
    }
  }

  if (loading) return <p>Indlæser…</p>;

  return (
    <main>
      <h1>Admin-dashboard</h1>

      {!event && (
        <>
          <p>Intet aktivt event</p>
          <button onClick={() => navigate("/admin/events/create")}>
            Opret nyt event
          </button>
        </>
      )}

      {event && (
        <>
          <h2>{event.name}</h2>
          <p>Status: {STATUS_LABELS[event.status] || event.status}</p>

          {event.status === "OPEN" && (
            <button onClick={() => changeStatus("VOTING")}>Åbn for afstemning</button>
          )}

          {event.status === "VOTING" && (
            <button onClick={() => changeStatus("CLOSED")}>Luk event</button>
          )}

          <p>
            Kode: <strong>{event.code}</strong>
          </p>
          <p>
            Startdato: {new Date(event.startDate).toLocaleDateString("da-DK")}
          </p>

          {event.status === "OPEN" && (
            <>
              <hr />
              <h3>Tilføj øl</h3>

              <input
                placeholder="Navn"
                value={beer.name}
                onChange={(e) => setBeer({ ...beer, name: e.target.value })}
              />

              <input
                placeholder="Bryggeri"
                value={beer.brewery}
                onChange={(e) => setBeer({ ...beer, brewery: e.target.value })}
              />

              <input
                placeholder="Land"
                value={beer.country}
                onChange={(e) => setBeer({ ...beer, country: e.target.value })}
              />

              <input
                type="number"
                step="0.1"
                placeholder="ABV"
                value={beer.abv}
                onChange={(e) => setBeer({ ...beer, abv: e.target.value })}
              />

              <input
                placeholder="Indsendt af"
                value={beer.submittedBy}
                onChange={(e) =>
                  setBeer({ ...beer, submittedBy: e.target.value })
                }
              />

              <button onClick={addBeer}>Tilføj øl</button>
            </>
          )}
          <h3>Registrerede øl</h3>

          {event.beers.length === 0 ? (
            <p>Ingen øl tilføjet endnu.</p>
          ) : (
            <table border="1" cellPadding="8">
              <thead>
                <tr>
                  <th>Navn</th>
                  <th>Bryggeri</th>
                  <th>Land</th>
                  <th>ABV</th>
                  <th>Indsendt af</th>
                </tr>
              </thead>
              <tbody>
                {event.beers.map((beer) => (
                  <tr key={beer.id}>
                    <td>{beer.name}</td>
                    <td>{beer.brewery}</td>
                    <td>{beer.country}</td>
                    <td>{beer.abv}</td>
                    <td>{beer.submittedBy}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </>
      )}

      <hr />
      <h3>Importér historiske data</h3>
      <p>
        Upload den gamle "Oversigt"-CSV-eksport for at masseoprette lukkede
        events med importerede resultater. Sikkert at køre flere gange —
        events der allerede findes (efter navn) springes over.
      </p>
      <input
        type="file"
        accept=".csv"
        onChange={(e) => setImportFile(e.target.files[0] || null)}
      />
      <button onClick={runImport} disabled={importing}>
        {importing ? "Importerer…" : "Importér CSV"}
      </button>
      {importStatus && <p>{importStatus}</p>}

      <hr />
      <h3>Tidligere events</h3>

      {pastEvents.length === 0 ? (
        <p>Ingen tidligere events.</p>
      ) : (
        <table border="1" cellPadding="8">
          <thead>
            <tr>
              <th>Event</th>
              <th>Dato</th>
              <th>Vinderøl</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            {pastEvents.map((row) => (
              <tr key={row.eventId}>
                <td>{row.eventName}</td>
                <td>{new Date(row.eventDate).toLocaleDateString("da-DK")}</td>
                <td>{row.beerName}</td>
                <td>
                  <button onClick={() => removeEvent(row.eventId, row.eventName)}>
                    Slet
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </main>
  );
}
