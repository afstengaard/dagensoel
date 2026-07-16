import { useEffect, useState } from "react";
import { useParams, Link } from "react-router-dom";
import api from "../api/apiFacade";

export default function EventResults() {
  const { eventId } = useParams();
  const [results, setResults] = useState([]);
  const [editingId, setEditingId] = useState(null);
  const [urlDraft, setUrlDraft] = useState("");
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
        <table border="1" cellPadding="8">
          <thead>
            <tr>
              <th>Image</th>
              <th>Beer</th>
              <th>Brewery</th>
              <th>ABV</th>
              <th>Submitted By</th>
              <th>Points</th>
              {isAdmin && <th></th>}
            </tr>
          </thead>
          <tbody>
            {results.map((r) => (
              <tr key={r.beerId}>
                <td>
                  {r.imageUrl ? (
                    <img
                      src={r.imageUrl}
                      alt={r.beerName}
                      style={{ width: 60, height: 60, objectFit: "cover" }}
                      onError={(e) => {
                        e.currentTarget.style.display = "none";
                      }}
                    />
                  ) : null}
                </td>
                <td>{r.beerName}</td>
                <td>{r.brewery}</td>
                <td>{r.abv ? `${r.abv}%` : ""}</td>
                <td>{r.submittedBy}</td>
                <td>{r.totalPoints}</td>
                {isAdmin && (
                  <td>
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
    </main>
  );
}
