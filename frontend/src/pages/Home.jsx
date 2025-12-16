import { useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../api/apiFacade";

export default function Home() {
  const [code, setCode] = useState("");
  const [error, setError] = useState(null);
  const navigate = useNavigate();

  const joinEvent = async () => {
  try {
    await api.getEventByCode(code);
    navigate(`/event/${code}`);
  } catch {
    setError("Event not found");
  }
};

  return (
    <main>
      <h1>Dagens Øl 🍺</h1>

      <input
        type="number"
        placeholder="Event code"
        value={code}
        onChange={e => setCode(e.target.value)}
      />

      <button onClick={joinEvent}>Join event</button>

      {error && <p style={{ color: "red" }}>{error}</p>}

      <hr />

      <button onClick={() => navigate("/admin/login")}>
        Admin login
      </button>
    </main>
  );
}
