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
      setMessage("Kunne ikke oprette event");
    }
  }

  return (
    <main>
      <h1>Opret event</h1>

      <input
        placeholder="Eventnavn"
        value={name}
        onChange={e => setName(e.target.value)}
      />

      <input
        type="date"
        value={startDate}
        onChange={e => setStartDate(e.target.value)}
      />

      <button onClick={createEvent}>Opret event</button>

      {message && <p>{message}</p>}
    </main>
  );
}
