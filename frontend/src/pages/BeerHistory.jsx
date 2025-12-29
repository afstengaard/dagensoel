import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../api/apiFacade";

export default function BeerHistory() {
  const [query, setQuery] = useState("");
  const [results, setResults] = useState([]);
  const [loading, setLoading] = useState(false);

  const navigate = useNavigate();

  useEffect(() => {
    if (query.trim().length < 2) {
      setResults([]);
      return;
    }

    const timeout = setTimeout(() => {
      searchBeers(query);
    }, 300); // debounce

    return () => clearTimeout(timeout);
  }, [query]);

  const searchBeers = async (q) => {
  setLoading(true);
  try {
    const data = await api.searchBeers(q);
    setResults(data);
  } catch (err) {
    console.error(err);
    setResults([]);
  } finally {
    setLoading(false);
  }
};

  const handleSelect = (eventId) => {
  navigate(`/results/${eventId}`);
};

  return (
    <div style={{ maxWidth: "500px", margin: "0 auto" }}>
      <h1>Beer history</h1>
      <p>Check if a beer has competed in previous events</p>

      <input
        type="text"
        value={query}
        onChange={(e) => setQuery(e.target.value)}
        placeholder="Type a beer name..."
        style={{
          width: "100%",
          padding: "0.75rem",
          fontSize: "1rem"
        }}
      />

      {loading && <p>Searching…</p>}

      {results.length > 0 && (
        <ul
          style={{
            listStyle: "none",
            padding: 0,
            marginTop: "0.5rem",
            border: "1px solid #ddd",
            borderRadius: "4px"
          }}
        >
          {results.map((beer) => (
            <li
              key={`${beer.beerId}-${beer.eventId}`}
              onClick={() => handleSelect(beer.eventId)}
              style={{
                padding: "0.75rem",
                cursor: "pointer",
                borderBottom: "1px solid #eee"
              }}
            >
              <strong>{beer.beerName}</strong>
              <div style={{ fontSize: "0.85rem", opacity: 0.7 }}>
                {beer.eventName}
              </div>
            </li>
          ))}
        </ul>
      )}

      {!loading && query.length >= 2 && results.length === 0 && (
        <p>No beers found</p>
      )}
    </div>
  );
}