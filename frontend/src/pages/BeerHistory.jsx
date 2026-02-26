import { useEffect, useState, useMemo } from "react";
import { useNavigate } from "react-router-dom";
import api from "../api/apiFacade";

export default function BeerHistory() {
  const [query, setQuery] = useState("");
  const [allBeers, setAllBeers] = useState([]);
  const [loading, setLoading] = useState(true);

  const navigate = useNavigate();

  useEffect(() => {
    const fetchHistory = async () => {
      try {
        const data = await api.getBeerHistory();
        setAllBeers(data);
      } catch (err) {
        console.error("Failed to load beer history:", err);
      } finally {
        setLoading(false);
      }
    };

    fetchHistory();
  }, []);

  const filteredBeers = useMemo(() => {
    return allBeers.filter((beer) =>
      beer.beerName.toLowerCase().includes(query.toLowerCase())
    );
  }, [query, allBeers]);

  const handleSelect = (eventId) => {
    navigate(`/results/${eventId}`);
  };

  return (
    <div style={{ maxWidth: "600px", margin: "0 auto", padding: "1rem" }}>
      <h1>Beer History</h1>
      <p>Browse all beers that have competed in previous events</p>

      <input
        type="text"
        value={query}
        onChange={(e) => setQuery(e.target.value)}
        placeholder="Search beer..."
        style={{
          width: "100%",
          padding: "0.75rem",
          fontSize: "1rem",
          marginBottom: "1rem",
        }}
      />

      {loading && <p>Loading beers...</p>}

      {!loading && (
        <ul
          style={{
            listStyle: "none",
            padding: 0,
            border: "1px solid #ddd",
            borderRadius: "6px",
            maxHeight: "500px",
            overflowY: "auto",
          }}
        >
          {filteredBeers.map((beer) => (
            <li
              key={`${beer.beerId}-${beer.eventId}`}
              onClick={() => handleSelect(beer.eventId)}
              style={{
                padding: "0.75rem",
                cursor: "pointer",
                borderBottom: "1px solid #eee",
              }}
            >
              <strong>{beer.beerName}</strong>
              <div style={{ fontSize: "0.85rem", opacity: 0.7 }}>
                {beer.eventName}
              </div>
            </li>
          ))}

          {filteredBeers.length === 0 && (
            <li style={{ padding: "0.75rem", opacity: 0.6 }}>
              No beers match your search
            </li>
          )}
        </ul>
      )}
    </div>
  );
}