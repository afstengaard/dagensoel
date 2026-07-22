import { useEffect, useMemo, useState } from "react";
import { Link } from "react-router-dom";
import api from "../api/apiFacade";
import { BEER_STYLE_CATEGORIES } from "../data/beerStyles";

export default function Stats() {
  const [beers, setBeers] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    api.getBeerHistory()
      .then(setBeers)
      .finally(() => setLoading(false));
  }, []);

  const categoryStats = useMemo(() => {
    // Only count beers with a known style and a real (non-"Ukendt") point
    // total - unknown-result years would otherwise drag averages toward 0.
    const sums = {};
    const counts = {};

    for (const beer of beers) {
      if (beer.pointsUnknown || !beer.style) continue;

      const categoryNumber = beer.style.match(/^\d+/)?.[0];
      if (!categoryNumber) continue;

      sums[categoryNumber] = (sums[categoryNumber] || 0) + beer.totalPoints;
      counts[categoryNumber] = (counts[categoryNumber] || 0) + 1;
    }

    return BEER_STYLE_CATEGORIES.map((category) => {
      const key = String(category.number);
      const count = counts[key] || 0;
      const average = count > 0 ? sums[key] / count : null;

      return {
        number: category.number,
        name: category.name,
        average,
        count,
      };
    });
  }, [beers]);

  const withData = categoryStats.filter((c) => c.average !== null);
  const best = withData.reduce(
    (a, b) => (b.average > (a?.average ?? -Infinity) ? b : a),
    null
  );
  const worst = withData.reduce(
    (a, b) => (b.average < (a?.average ?? Infinity) ? b : a),
    null
  );

  const sortedForTable = [...categoryStats].sort((a, b) => a.number - b.number);

  return (
    <div style={{ maxWidth: "900px", margin: "0 auto", padding: "1rem" }}>
      <h1>Statistik</h1>
      <p>Gennemsnitligt point pr. ølstil-kategori, på tværs af alle Dagens Øl-konkurrencer.</p>

      {loading && <p>Indlæser...</p>}

      {!loading && withData.length === 0 && (
        <p>Ingen øl har både en registreret stil og et kendt pointtal endnu.</p>
      )}

      {!loading && withData.length > 0 && (
        <>
          <div style={{ display: "flex", gap: "1rem", flexWrap: "wrap", margin: "1.5rem 0" }}>
            <HighlightCard
              label="Bedst klarende stil"
              category={best}
              background="#e6f7e6"
              border="#3a9d3a"
            />
            <HighlightCard
              label="Dårligst klarende stil"
              category={worst}
              background="#fbe9e9"
              border="#c0392b"
            />
          </div>

          <table border="1" cellPadding="8" style={{ width: "100%", borderCollapse: "collapse" }}>
            <thead>
              <tr>
                <th style={{ textAlign: "left" }}>Kategori</th>
                <th style={{ textAlign: "left" }}>Gennemsnit</th>
                <th style={{ textAlign: "left" }}>Antal øl</th>
              </tr>
            </thead>
            <tbody>
              {sortedForTable.map((c) => (
                <tr key={c.number}>
                  <td>{c.number}. {c.name}</td>
                  <td>{c.average !== null ? c.average.toFixed(1) + " p" : "–"}</td>
                  <td>{c.count}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </>
      )}

      <p style={{ marginTop: "1.5rem" }}>
        <Link to="/beers">← Se alle øl</Link>
      </p>
    </div>
  );
}

function HighlightCard({ label, category, background, border }) {
  if (!category) return null;

  return (
    <div
      style={{
        flex: "1 1 250px",
        background,
        border: `2px solid ${border}`,
        borderRadius: "8px",
        padding: "1rem",
      }}
    >
      <div style={{ fontWeight: "bold", marginBottom: "0.5rem" }}>{label}</div>
      <div style={{ fontSize: "1.2rem" }}>
        {category.number}. {category.name}
      </div>
      <div>
        {category.average.toFixed(1)} point i gennemsnit ({category.count} øl)
      </div>
    </div>
  );
}
