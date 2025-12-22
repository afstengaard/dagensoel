import { useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../api/apiFacade";

export default function CreateEvent() {
  const [name, setName] = useState("");
  const [startDate, setStartDate] = useState("");
  const [message, setMessage] = useState(null);
  const navigate = useNavigate();

  async function createEvent() {
    try {
      await api.createEvent({ name, startDate });
      navigate("/admin");
    } catch (e) {
      setMessage("Failed to create event");
    }
  }

  return (
    <main>
      <h1>Create Event</h1>

      <input
        placeholder="Event name"
        value={name}
        onChange={e => setName(e.target.value)}
      />

      <input
        type="date"
        value={startDate}
        onChange={e => setStartDate(e.target.value)}
      />

      <button onClick={createEvent}>Create event</button>

      {message && <p>{message}</p>}
    </main>
  );
}
