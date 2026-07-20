import { useEffect, useState } from "react";
import { useParams, Link } from "react-router-dom";
import api from "../api/apiFacade";
import ImageLightbox from "../components/ImageLightbox";
import "../styles/responsive-table.css";

export default function EventResults() {
  const { eventId } = useParams();
  const [results, setResults] = useState([]);
  const [editingId, setEditingId] = useState(null);
  const [urlDraft, setUrlDraft] = useState("");
  const [lightboxImage, setLightboxImage] = useState(null);
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
              <th>Placering</th>
              <th>Billede</th>
              <th>Øl</th>
              <th>Bryggeri</th>
              <th>ABV</th>
              <th>Aften</th>
              <th>Untappd</th>
              <th>Indsendt af</th>
              <th>Point</th>
              {isAdmin && <th></th>}
            </tr>
          </thead>
          <tbody>
            {results.map((r) => (
              <tr key={r.beerId}>
                <td data-label="Placering">
                  {r.placement}.{" "}
                  {r.placement === 1 ? "🥇" : r.placement === 2 ? "🥈" : r.placement === 3 ? "🥉" : ""}
                </td>
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
                <td data-label="Aften">{r.evening}</td>
                <td data-label="Untappd">
                  {r.untappdLink ? (
                    <a href={r.untappdLink} target="_blank" rel="noreferrer">
                      Untappd
                    </a>
                  ) : null}
                </td>
                <td data-label="Indsendt af">{r.submittedBy}</td>
                <td data-label="Point">{r.totalPoints}</td>
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
