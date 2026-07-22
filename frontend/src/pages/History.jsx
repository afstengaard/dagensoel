import { useEffect, useState, useMemo } from "react";
import { Link } from "react-router-dom";
import api from "../api/apiFacade";

function compareRows(a, b, sortKey) {
  if (sortKey === "year") {
    return (a.eventDate?.[0] ?? 0) - (b.eventDate?.[0] ?? 0);
  }

  return String(a[sortKey] ?? "").localeCompare(String(b[sortKey] ?? ""), "da");
}

export default function History() {
  const [history, setHistory] = useState([]);
  const [sortKey, setSortKey] = useState("year");
  const [sortDirection, setSortDirection] = useState("desc");

  useEffect(() => {
    api.getEventHistory().then(setHistory);
  }, []);

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

  const sortedHistory = useMemo(() => {
    const sorted = [...history].sort((a, b) => compareRows(a, b, sortKey));
    return sortDirection === "desc" ? sorted.reverse() : sorted;
  }, [history, sortKey, sortDirection]);

  return (
    <main>
      <h1>Tidligere år</h1>

      {history.length === 0 ? (
        <p>Ingen tidligere konkurrencer endnu.</p>
      ) : (
        <table border="1" cellPadding="8">
          <thead>
            <tr>
              <th onClick={() => toggleSort("year")} style={{ cursor: "pointer" }}>
                År{sortIndicator("year")}
              </th>
              <th onClick={() => toggleSort("beerName")} style={{ cursor: "pointer" }}>
                Vinderøl{sortIndicator("beerName")}
              </th>
              <th onClick={() => toggleSort("submittedBy")} style={{ cursor: "pointer" }}>
                Indsendt af{sortIndicator("submittedBy")}
              </th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            {sortedHistory.map(row => (
              <tr key={row.eventId}>
                <td>{row.eventDate[0]}</td>
                <td>{row.beerName}</td>
                <td>{row.submittedBy}</td>
                <td>
                  <Link to={`/results/${row.eventId}`}>
                    <button>Se resultater</button>
                  </Link>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </main>
  );
}
