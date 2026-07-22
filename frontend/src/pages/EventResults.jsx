import { useEffect, useState, useMemo } from "react";
import { useParams, Link } from "react-router-dom";
import api from "../api/apiFacade";
import ImageLightbox from "../components/ImageLightbox";
import { styleLabel } from "../data/beerStyles";
import "../styles/responsive-table.css";

function compareResults(a, b, sortKey) {
  if (sortKey === "abv" || sortKey === "totalPoints") {
    return (a[sortKey] ?? 0) - (b[sortKey] ?? 0);
  }

  if (sortKey === "style") {
    return styleLabel(a.style).localeCompare(styleLabel(b.style), "da");
  }

  return String(a[sortKey] ?? "").localeCompare(String(b[sortKey] ?? ""), "da");
}

export default function EventResults() {
  const { eventId } = useParams();
  const [results, setResults] = useState([]);
  const [editingId, setEditingId] = useState(null);
  const [urlDraft, setUrlDraft] = useState("");
  const [lightboxImage, setLightboxImage] = useState(null);
  const [sortKey, setSortKey] = useState("totalPoints");
  const [sortDirection, setSortDirection] = useState("desc");
  const isAdmin = api.loggedIn();

  useEffect(() => {
    loadResults();
  }, [eventId]);

  function loadResults() {
    api.getEventResults(eventId).then(setResults);
  }

  function startEditing(beer) {
    setEditingId(beer.beerId);
    setUrlDraft(beer.imageUrl || "");
  }

  async function saveImageUrl(beerId) {
    try {
      await api.setBeerImageUrl(beerId, urlDraft.trim());
      setEditingId(null);
      loadResults();
    } catch (err) {
      alert(`Kunne ikke gemme billed-URL: ${err.message}`);
    }
  }

  function toggleSort(key) {
    if (sortKey === key) {
      setSortDirection((d) => (d === "asc" ? "desc" : "asc"));
    } else {
      setSortKey(key);
      setSortDirection("asc");
    }
  }

  function sortIndicator(key) {
    if (sortKey !== key) return "";
    return sortDirection === "asc" ? " ▲" : " ▼";
  }

  const sortedResults = useMemo(() => {
    const sorted = [...results].sort((a, b) => compareResults(a, b, sortKey));
    return sortDirection === "desc" ? sorted.reverse() : sorted;
  }, [results, sortKey, sortDirection]);

  return (
    <main>
      <Link to="/history">← Tilbage til historik</Link>

      <h1>Eventresultater</h1>

      {results.length === 0 ? (
        <p>Ingen resultater tilgængelige.</p>
      ) : (
        <div className="table-scroll">
        <table className="results-table">
          <thead>
            <tr>
              <th>Billede</th>
              <th onClick={() => toggleSort("beerName")} style={{ cursor: "pointer" }}>
                Øl{sortIndicator("beerName")}
              </th>
              <th onClick={() => toggleSort("brewery")} style={{ cursor: "pointer" }}>
                Bryggeri{sortIndicator("brewery")}
              </th>
              <th onClick={() => toggleSort("abv")} style={{ cursor: "pointer" }}>
                ABV{sortIndicator("abv")}
              </th>
              <th onClick={() => toggleSort("style")} style={{ cursor: "pointer" }}>
                Stil{sortIndicator("style")}
              </th>
              <th onClick={() => toggleSort("evening")} style={{ cursor: "pointer" }}>
                Aften{sortIndicator("evening")}
              </th>
              <th>Untappd</th>
              <th onClick={() => toggleSort("submittedBy")} style={{ cursor: "pointer" }}>
                Indsendt af{sortIndicator("submittedBy")}
              </th>
              <th onClick={() => toggleSort("totalPoints")} style={{ cursor: "pointer" }}>
                Point{sortIndicator("totalPoints")}
              </th>
              {isAdmin && <th></th>}
            </tr>
          </thead>
          <tbody>
            {sortedResults.map((r) => (
              <tr key={r.beerId}>
                <td className="cell-image">
                  {r.imageUrl ? (
                    <img
                      src={r.imageUrl}
                      alt={r.beerName}
                      onClick={() =>
                        setLightboxImage({ src: r.imageUrl, alt: r.beerName })
                      }
                      onError={(e) => {
                        e.currentTarget.style.display = "none";
                      }}
                    />
                  ) : null}
                </td>
                <td className="cell-title" data-label="Øl">
                  {r.beerName}
                </td>
                <td data-label="Bryggeri">{r.brewery}</td>
                <td data-label="ABV">{r.abv ? `${r.abv}%` : ""}</td>
                <td data-label="Stil">{styleLabel(r.style)}</td>
                <td data-label="Aften">{r.evening}</td>
                <td data-label="Untappd">
                  {r.untappdLink ? (
                    <a href={r.untappdLink} target="_blank" rel="noreferrer">
                      Untappd
                    </a>
                  ) : null}
                </td>
                <td data-label="Indsendt af">{r.submittedBy}</td>
                <td data-label="Point">{r.pointsUnknown ? "Ukendt" : r.totalPoints}</td>
                {isAdmin && (
                  <td data-label="Billed-URL">
                    {editingId === r.beerId ? (
                      <>
                        <input
                          type="text"
                          placeholder="https://..."
                          value={urlDraft}
                          onChange={(e) => setUrlDraft(e.target.value)}
                          style={{ width: 200 }}
                        />
                        <button onClick={() => saveImageUrl(r.beerId)}>
                          Gem
                        </button>
                        <button onClick={() => setEditingId(null)}>
                          Annullér
                        </button>
                      </>
                    ) : (
                      <button onClick={() => startEditing(r)}>
                        {r.imageUrl ? "Rediger billede" : "Tilføj billede"}
                      </button>
                    )}
                  </td>
                )}
              </tr>
            ))}
          </tbody>
        </table>
        </div>
      )}

      <ImageLightbox
        src={lightboxImage?.src}
        alt={lightboxImage?.alt}
        onClose={() => setLightboxImage(null)}
      />
    </main>
  );
}
