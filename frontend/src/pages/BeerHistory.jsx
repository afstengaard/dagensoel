import { useEffect, useState, useMemo } from "react";
import { Link } from "react-router-dom";
import api from "../api/apiFacade";
import ImageLightbox from "../components/ImageLightbox";
import "../styles/responsive-table.css";

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
        <div className="table-scroll">
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
              <th>Event</th>
            </tr>
          </thead>
          <tbody>
            {filteredBeers.map((beer) => (
              <tr key={`${beer.beerId}-${beer.eventId}`}>
                <td className="cell-image">
                  {beer.imageUrl ? (
                    <img
                      src={beer.imageUrl}
                      alt={beer.beerName}
                      onClick={() =>
                        setLightboxImage({ src: beer.imageUrl, alt: beer.beerName })
                      }
                      onError={(e) => {
                        e.currentTarget.style.display = "none";
                      }}
                    />
                  ) : null}
                </td>
                <td className="cell-title" data-label="Beer">
                  {beer.beerName}
                </td>
                <td data-label="Brewery">{beer.brewery}</td>
                <td data-label="ABV">{beer.abv ? `${beer.abv}%` : ""}</td>
                <td data-label="Evening">{beer.evening}</td>
                <td data-label="Untappd">
                  {beer.untappdLink ? (
                    <a href={beer.untappdLink} target="_blank" rel="noreferrer">
                      Untappd
                    </a>
                  ) : null}
                </td>
                <td data-label="Submitted By">{beer.submittedBy}</td>
                <td data-label="Event">
                  <Link to={`/results/${beer.eventId}`}>{beer.eventName}</Link>
                </td>
              </tr>
            ))}

            {filteredBeers.length === 0 && (
              <tr>
                <td
                  colSpan="8"
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
        </div>
      )}

      <ImageLightbox
        src={lightboxImage?.src}
        alt={lightboxImage?.alt}
        onClose={() => setLightboxImage(null)}
      />
    </div>
  );
}
