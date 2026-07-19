import { useEffect, useState, useMemo } from "react";
import { Link } from "react-router-dom";
import api from "../api/apiFacade";
import ImageLightbox from "../components/ImageLightbox";

export default function BeerHistory() {
  const [query, setQuery] = useState("");
  const [allBeers, setAllBeers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [lightboxImage, setLightboxImage] = useState(null);

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
    const search = query.trim().toLowerCase();

    return allBeers.filter((beer) => {
      const year = beer.eventDate?.[0]?.toString() || "";

      return (
        beer.beerName.toLowerCase().includes(search) ||
        beer.brewery?.toLowerCase().includes(search) ||
        beer.submittedBy?.toLowerCase().includes(search) ||
        year.includes(search)
      );
    });
  }, [query, allBeers]);

  return (
    <div style={{ maxWidth: "1000px", margin: "0 auto", padding: "1rem" }}>
      <h1>Beer History</h1>

      <input
        type="text"
        placeholder="Search beer, brewery, person or year..."
        value={query}
        onChange={(e) => setQuery(e.target.value)}
        style={{
          width: "100%",
          padding: "0.75rem",
          marginBottom: "1rem",
        }}
      />

      {loading && <p>Loading beers...</p>}

      {!loading && (
        <table
          style={{
            width: "100%",
            borderCollapse: "collapse",
          }}
        >
          <thead>
            <tr style={{ backgroundColor: "#f5f5f5" }}>
              <th style={thStyle}>Image</th>
              <th style={thStyle}>Beer</th>
              <th style={thStyle}>Brewery</th>
              <th style={thStyle}>ABV</th>
              <th style={thStyle}>Evening</th>
              <th style={thStyle}>Untappd</th>
              <th style={thStyle}>Submitted By</th>
              <th style={thStyle}>Event</th>
              <th style={thStyle}>Year</th>
            </tr>
          </thead>
          <tbody>
            {filteredBeers.map((beer) => (
              <tr key={`${beer.beerId}-${beer.eventId}`}>
                <td style={tdStyle}>
                  {beer.imageUrl ? (
                    <img
                      src={beer.imageUrl}
                      alt={beer.beerName}
                      onClick={() =>
                        setLightboxImage({ src: beer.imageUrl, alt: beer.beerName })
                      }
                      style={{
                        width: 48,
                        height: 48,
                        objectFit: "cover",
                        cursor: "zoom-in",
                      }}
                      onError={(e) => {
                        e.currentTarget.style.display = "none";
                      }}
                    />
                  ) : null}
                </td>
                <td style={tdStyle}>{beer.beerName}</td>
                <td style={tdStyle}>{beer.brewery}</td>
                <td style={tdStyle}>{beer.abv ? `${beer.abv}%` : ""}</td>
                <td style={tdStyle}>{beer.evening}</td>
                <td style={tdStyle}>
                  {beer.untappdLink ? (
                    <a href={beer.untappdLink} target="_blank" rel="noreferrer">
                      Untappd
                    </a>
                  ) : null}
                </td>
                <td style={tdStyle}>{beer.submittedBy}</td>
                <td style={tdStyle}>
                  <Link to={`/results/${beer.eventId}`}>{beer.eventName}</Link>
                </td>
                <td style={tdStyle}>{beer.eventDate?.[0]}</td>
              </tr>
            ))}

            {filteredBeers.length === 0 && (
              <tr>
                <td
                  colSpan="9"
                  style={{
                    padding: "1rem",
                    textAlign: "center",
                    opacity: 0.6,
                  }}
                >
                  No beers match your search
                </td>
              </tr>
            )}
          </tbody>
        </table>
      )}

      <ImageLightbox
        src={lightboxImage?.src}
        alt={lightboxImage?.alt}
        onClose={() => setLightboxImage(null)}
      />
    </div>
  );
}

const thStyle = {
  textAlign: "left",
  padding: "0.75rem",
  borderBottom: "2px solid #ddd",
};

const tdStyle = {
  padding: "0.75rem",
  borderBottom: "1px solid #eee",
};
