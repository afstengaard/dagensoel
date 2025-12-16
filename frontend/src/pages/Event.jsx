import { useParams, useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../api/apiFacade";

export default function Event() {
  const { code } = useParams();
  const navigate = useNavigate();
  const [event, setEvent] = useState(null);

  useEffect(() => {
  api.getEventByCode(code).then(setEvent);
}, [code]);


  if (!event) return <p>Loading...</p>;

  return (
    <main>
      <h1>{event.name} ({event.year})</h1>
      <p>
        {event.startDate} → {event.endDate}
      </p>

      <h2>Beers tasted</h2>

      {event.beers.length === 0 ? (
        <p>No beers registered yet.</p>
      ) : (
        <table border="1" cellPadding="8">
          <thead>
            <tr>
              <th>Name</th>
              <th>Brewery</th>
              <th>Country</th>
              <th>ABV %</th>
              <th>Submitted by</th>
            </tr>
          </thead>
          <tbody>
            {event.beers.map(beer => (
              <tr key={beer.id}>
                <td>{beer.name}</td>
                <td>{beer.brewery}</td>
                <td>{beer.country}</td>
                <td>{beer.abv}</td>
                <td>{beer.submittedBy}</td>
              </tr>
            ))}
          </tbody>
        </table>
      )}

      <br />

      <button
        disabled={!event.votingOpen}
        onClick={() => navigate(`/event/${code}/vote`)}
      >
        Go to vote
      </button>

      {!event.votingOpen && (
        <p>Voting is not open yet.</p>
      )}
    </main>
  );
}
