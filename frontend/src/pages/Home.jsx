import { useState } from "react";
import { useNavigate } from "react-router-dom";

export default function Home() {
  const [code, setCode] = useState("");
  const navigate = useNavigate();

  function joinEvent() {
    if (!code.trim()) return;

    navigate(`/event/${code.trim().toUpperCase()}`);
  }

  return (
    <main>
      <h1>Dagens Øl 🍺</h1>

      <input
        placeholder="Eventkode"
        value={code}
        onChange={e => setCode(e.target.value)}
      />

      <button onClick={joinEvent}>Deltag i event</button>

      <hr />

      <button onClick={() => navigate("/admin/login")}>
        Admin login
      </button>
    </main>
  );
}
