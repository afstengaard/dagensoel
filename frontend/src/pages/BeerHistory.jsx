import { useEffect, useState, useMemo } from "react";
import { Link } from "react-router-dom";
import api from "../api/apiFacade";
import ImageLightbox from "../components/ImageLightbox";
import { BEER_STYLE_CATEGORIES, styleLabel } from "../data/beerStyles";
import "../styles/responsive-table.css";

export default function BeerHistory() {
  const [query, setQuery] = useState("");
  const [categoryFilter, setCategoryFilter] = useState("");
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

      const matchesSearch =
        beer.beerName.toLowerCase().includes(search) ||
        beer.brewery?.toLowerCase().includes(search) ||
        beer.submittedBy?.toLowerCase().includes(search) ||
        year.includes(search);

      // A style code like "5A" belongs to category "5" - match on the
      // leading digits so "5" matches "5A", "5B", "5G" etc, but not "12A".
      const beerCategoryNumber = beer.style?.match(/^\d+/)?.[0];
      const categoryMatches = !categoryFilter || beerCategoryNumber === categoryFilter;

      return matchesSearch && categoryMatches;
    });
  }, [query, categoryFilter, allBeers]);

  return (
    <div style={{ maxWidth: "1000px", margin: "0 auto", padding: "1rem" }}>
      <h1>Ølhistorik</h1>

      <div style={{ display: "flex", gap: "0.5rem", marginBottom: "1rem", flexWrap: "wrap" }}>
        <input
          type="text"
          placeholder="Søg efter øl, bryggeri, person eller år..."
          value={query}
          onChange={(e) => setQuery(e.target.value)}
          style={{
            flex: 1,
            minWidth: "200px",
            padding: "0.75rem",
          }}
        />

        <select
          value={categoryFilter}
          onChange={(e) => setCategoryFilter(e.target.value)}
          style={{ padding: "0.75rem" }}
        >
          <option value="">Alle ølstile</option>
          {BEER_STYLE_CATEGORIES.map((category) => (
            <option key={category.number} value={String(category.number)}>
              {category.number}. {category.name}
            </option>
          ))}
        </select>
      </div>

      {loading && <p>Indlæser øl...</p>}

      {!loading && (
        <div className="table-scroll">
        <table className="results-table">
          <thead>
            <tr>
              <th>Placering</th>
              <th>Billede</th>
              <th>Øl</th>
              <th>Bryggeri</th>
              <th>ABV</th>
              <th>Stil</th>
              <th>Aften</th>
              <th>Untappd</th>
              <th>Indsendt af</th>
              <th>Event</th>
            </tr>
          </thead>
          <tbody>
            {filteredBeers.map((beer) => (
              <tr key={`${beer.beerId}-${beer.eventId}`}>
                <td data-label="Placering">
                  {beer.placement} ({beer.totalPoints} p)
                </td>
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
                <td className="cell-title" data-label="Øl">
                  {beer.beerName}
                </td>
                <td data-label="Bryggeri">{beer.brewery}</td>
                <td data-label="ABV">{beer.abv ? `${beer.abv}%` : ""}</td>
                <td data-label="Stil">{styleLabel(beer.style)}</td>
                <td data-label="Aften">{beer.evening}</td>
                <td data-label="Untappd">
                  {beer.untappdLink ? (
                    <a href={beer.untappdLink} target="_blank" rel="noreferrer">
                      Untappd
                    </a>
                  ) : null}
                </td>
                <td data-label="Indsendt af">{beer.submittedBy}</td>
                <td data-label="Event">
                  <Link to={`/results/${beer.eventId}`}>{beer.eventName}</Link>
                </td>
              </tr>
            ))}

            {filteredBeers.length === 0 && (
              <tr>
                <td
                  colSpan="10"
                  style={{
                    padding: "1rem",
                    textAlign: "center",
                    opacity: 0.6,
                  }}
                >
                  Ingen øl matcher din søgning
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
