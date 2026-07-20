import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import api from "../api/apiFacade";

export default function Event() {
  const { code } = useParams();
  const navigate = useNavigate();
  const [event, setEvent] = useState(null);

  useEffect(() => {
    api.getEventByCode(code).then(setEvent);
  }, [code]);

  if (!event) return <p>Indlæser...</p>;

  const votingOpen = event.status === "VOTING";

  return (
    <main>
      <h1>{event.name}</h1>

      {event.startDate && <p>Startdato: {event.startDate}</p>}

      <h2>Smagte øl</h2>

      {event.beers.length === 0 ? (
        <p>Ingen øl tilføjet endnu.</p>
      ) : (
        <table border="1" cellPadding="8">
          <thead>
            <tr>
              <th>Navn</th>
              <th>Bryggeri</th>
              <th>Land</th>
              <th>ABV %</th>
              <th>Indsendt af</th>
            </tr>
          </thead>
          <tbody>
            {event.beers.map((beer) => (
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
        disabled={!votingOpen}
        onClick={() => navigate(`/event/${code}/vote`)}
      >
        Gå til afstemning
      </button>

      {!votingOpen && <p>Afstemningen er ikke åbnet endnu.</p>}

      {event.status === "CLOSED" && (
        <button onClick={() => navigate(`/results/${event.id}`)}>
          Se resultater
        </button>
      )}
    </main>
  );
}
