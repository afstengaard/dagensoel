import { useParams } from "react-router-dom";
import { useEffect, useState } from "react";
import api from "../api/apiFacade";

export default function Vote() {
  const { code } = useParams();
  const [event, setEvent] = useState(null);
  const [message, setMessage] = useState(null);

  const [favoriteBeerId, setFavoriteBeerId] = useState(null);
  const [secondBeerId, setSecondBeerId] = useState(null);
  const [submitted, setSubmitted] = useState(false);

  useEffect(() => {
    api.getEventByCode(code)
      .then(setEvent)
      .catch(() => setMessage("Event not found"));
  }, [code]);

  if (!event) return <p>Loading...</p>;

  if (event.status !== "VOTING") {
    return <p>Voting is not open.</p>;
  }

  async function submitVotes() {
    setMessage(null);

    if (!favoriteBeerId || !secondBeerId) {
      setMessage("Please select both a favorite and a second choice.");
      return;
    }

    if (favoriteBeerId === secondBeerId) {
      setMessage("Favorite and second choice must be different beers.");
      return;
    }

    try {
      await api.submitVotes(code, {
        favoriteBeerId,
        secondBeerId,
      });

      setSubmitted(true);
      setMessage("Thanks for voting! 🍻");
    } catch (e) {
      setMessage(e.message || "Failed to submit votes");
    }
  }

  return (
    <main>
      <h1>Vote for {event.name}</h1>

      {message && <p>{message}</p>}

      <table border="1" cellPadding="8">
        <thead>
          <tr>
            <th>Name</th>
            <th>Brewery</th>
            <th>Favorite (2 pts)</th>
            <th>Second (1 pt)</th>
          </tr>
        </thead>
        <tbody>
          {event.beers.map(beer => (
            <tr
              key={beer.id}
              style={{
                background:
                  beer.id === favoriteBeerId
                    ? "#2e7d32"
                    : beer.id === secondBeerId
                    ? "#1565c0"
                    : "transparent",
                color:
                  beer.id === favoriteBeerId || beer.id === secondBeerId
                    ? "white"
                    : "inherit",
              }}
            >
              <td>{beer.name}</td>
              <td>{beer.brewery}</td>
              <td>
                <button
                  disabled={submitted}
                  onClick={() => setFavoriteBeerId(beer.id)}
                >
                  {favoriteBeerId === beer.id ? "Selected" : "Select"}
                </button>
              </td>
              <td>
                <button
                  disabled={submitted}
                  onClick={() => setSecondBeerId(beer.id)}
                >
                  {secondBeerId === beer.id ? "Selected" : "Select"}
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>

      <br />

      {!submitted && (
        <button onClick={submitVotes}>
          Submit votes
        </button>
      )}
    </main>
  );
}
