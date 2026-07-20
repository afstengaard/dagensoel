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
      alert(`Failed to save image URL: ${err.message}`);
    }
  }

  return (
    <main>
      <Link to="/history">← Back to history</Link>

      <h1>Event Results</h1>

      {results.length === 0 ? (
        <p>No results available.</p>
      ) : (
        <table className="results-table">
          <thead>
            <tr>
              <th>Image</th>
              <th>Beer</th>
              <th>Brewery</th>
              <th>ABV</th>
              <th>Evening</th>
              <th>Untappd</th>
              <th>Submitted By</th>
              <th>Points</th>
              {isAdmin && <th></th>}
            </tr>
          </thead>
          <tbody>
            {results.map((r) => (
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
                <td className="cell-title" data-label="Beer">
                  {r.beerName}
                </td>
                <td data-label="Brewery">{r.brewery}</td>
                <td data-label="ABV">{r.abv ? `${r.abv}%` : ""}</td>
                <td data-label="Evening">{r.evening}</td>
                <td data-label="Untappd">
                  {r.untappdLink ? (
                    <a href={r.untappdLink} target="_blank" rel="noreferrer">
                      Untappd
                    </a>
                  ) : null}
                </td>
                <td data-label="Submitted By">{r.submittedBy}</td>
                <td data-label="Points">{r.totalPoints}</td>
                {isAdmin && (
                  <td data-label="Image URL">
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
                          Save
                        </button>
                        <button onClick={() => setEditingId(null)}>
                          Cancel
                        </button>
                      </>
                    ) : (
                      <button onClick={() => startEditing(r)}>
                        {r.imageUrl ? "Edit image" : "Add image"}
                      </button>
                    )}
                  </td>
                )}
              </tr>
            ))}
          </tbody>
        </table>
      )}

      <ImageLightbox
        src={lightboxImage?.src}
        alt={lightboxImage?.alt}
        onClose={() => setLightboxImage(null)}
      />
    </main>
  );
}
