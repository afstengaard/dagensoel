import { useEffect, useState } from "react";
import { useParams, Link } from "react-router-dom";
import api from "../api/apiFacade";

export default function EventResults() {
  const { eventId } = useParams();
  const [results, setResults] = useState([]);

  useEffect(() => {
    api.getEventResults(eventId).then(setResults);
  }, [eventId]);

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
              <th>Beer</th>
              <th>Submitted By</th>
              <th>Points</th>
            </tr>
          </thead>
          <tbody>
            {results.map((r) => (
              <tr key={r.beerId}>
                <td>{r.beerName}</td>
                <td>{r.submittedBy}</td>
                <td>{r.totalPoints}</td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </main>
  );
}
