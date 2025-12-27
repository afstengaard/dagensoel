import { useEffect, useState } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import api from "../api/apiFacade";

export default function AdminDashboard() {
  const [event, setEvent] = useState(null);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();
  const location = useLocation();
  const [beer, setBeer] = useState({
    name: "",
    brewery: "",
    country: "",
    abv: "",
    submittedBy: "",
  });

  useEffect(() => {
    async function load() {
      try {
        const active = await api.getActiveEvent();
        setEvent(active);
      } catch {
        // No active event → this is OK
        setEvent(null);
      } finally {
        setLoading(false);
      }
    }

    load();
  }, [location.pathname]);

  async function reloadEvent() {
    try {
      const active = await api.getActiveEvent();
      setEvent(active);
    } catch {
      setEvent(null);
    }
  }

  async function changeStatus(nextStatus) {
    try {
      const updated = await api.updateEventStatus(event.id, nextStatus);
      setEvent(updated);
    } catch {
      alert("Failed to update event status");
    }
  }

  async function addBeer() {
    const name = beer.name.trim();
    const brewery = beer.brewery.trim();
    const country = beer.country.trim();
    const submittedBy = beer.submittedBy.trim();
    const abv = Number(beer.abv);

    // String validation
    if (!name || !brewery || !country || !submittedBy) {
      alert("All text fields must be filled out");
      return;
    }

    // ABV validation
    if (isNaN(abv)) {
      alert("ABV must be a number");
      return;
    }

    if (abv <= 0) {
      alert("ABV must be greater than 0");
      return;
    }

    try {
      await api.addBeerToEvent(event.id, {
        name,
        brewery,
        country,
        abv,
        submittedBy,
      });

      setBeer({
        name: "",
        brewery: "",
        country: "",
        abv: "",
        submittedBy: "",
      });

      await reloadEvent();
    } catch {
      alert("Failed to add beer");
    }
  }

  if (loading) return <p>Loading…</p>;

  return (
    <main>
      <h1>Admin Dashboard</h1>

      {!event && (
        <>
          <p>No active event</p>
          <button onClick={() => navigate("/admin/events/create")}>
            Create new event
          </button>
        </>
      )}

      {event && (
        <>
          <h2>{event.name}</h2>
          <p>Status: {event.status}</p>

          {event.status === "OPEN" && (
            <button onClick={() => changeStatus("VOTING")}>Open voting</button>
          )}

          {event.status === "VOTING" && (
            <button onClick={() => changeStatus("CLOSED")}>Close event</button>
          )}

          <p>
            Code: <strong>{event.code}</strong>
          </p>
          <p>
            Start date: {new Date(event.startDate).toLocaleDateString("da-DK")}
          </p>

          {event.status === "OPEN" && (
            <>
              <hr />
              <h3>Add beer</h3>

              <input
                placeholder="Name"
                value={beer.name}
                onChange={(e) => setBeer({ ...beer, name: e.target.value })}
              />

              <input
                placeholder="Brewery"
                value={beer.brewery}
                onChange={(e) => setBeer({ ...beer, brewery: e.target.value })}
              />

              <input
                placeholder="Country"
                value={beer.country}
                onChange={(e) => setBeer({ ...beer, country: e.target.value })}
              />

              <input
                type="number"
                step="0.1"
                placeholder="ABV"
                value={beer.abv}
                onChange={(e) => setBeer({ ...beer, abv: e.target.value })}
              />

              <input
                placeholder="Submitted by"
                value={beer.submittedBy}
                onChange={(e) =>
                  setBeer({ ...beer, submittedBy: e.target.value })
                }
              />

              <button onClick={addBeer}>Add beer</button>
            </>
          )}
          <h3>Registered beers</h3>

          {event.beers.length === 0 ? (
            <p>No beers added yet.</p>
          ) : (
            <table border="1" cellPadding="8">
              <thead>
                <tr>
                  <th>Name</th>
                  <th>Brewery</th>
                  <th>Country</th>
                  <th>ABV</th>
                  <th>Submitted by</th>
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
        </>
      )}
    </main>
  );
}
