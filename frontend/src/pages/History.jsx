import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import api from "../api/apiFacade";

export default function History() {
  const [history, setHistory] = useState([]);

  useEffect(() => {
    api.getEventHistory().then(setHistory);
  }, []);

  return (
    <main>
      <h1>Previous Years</h1>

      {history.length === 0 ? (
        <p>No previous contests yet.</p>
      ) : (
        <table border="1" cellPadding="8">
          <thead>
            <tr>
              <th>Year</th>
              <th>Winning Beer</th>
              <th>Submitted By</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            {history.map(row => (
              <tr key={row.eventId}>
                <td>{row.eventDate[0]}</td>
                <td>{row.beerName}</td>
                <td>{row.submittedBy}</td>
                <td>
                  <Link to={`/results/${row.eventId}`}>
                    <button>View results</button>
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
