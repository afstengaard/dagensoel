import { useEffect, useState } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import api from "../api/apiFacade";

export default function AdminDashboard() {
  const [event, setEvent] = useState(null);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();
  const location = useLocation();

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

  async function changeStatus(nextStatus) {
    try {
      const updated = await api.updateEventStatus(event.id, nextStatus);
      setEvent(updated);
    } catch {
      alert("Failed to update event status");
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

          {/* Later */}
          {/* <button>Open voting</button> */}
          {/* <button>Close event</button> */}
        </>
      )}
    </main>
  );
}
