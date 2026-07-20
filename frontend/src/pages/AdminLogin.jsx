import { useState } from "react";
import api from "../api/apiFacade";
import { useNavigate } from "react-router-dom";

export default function AdminLogin() {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState(null);
  const navigate = useNavigate();

  async function submit() {
    try {
      await api.login(username, password);
      navigate("/admin");
    } catch {
      setError("Login mislykkedes");
    }
  }

  return (
    <main>
      <h1>Admin login</h1>

      <input placeholder="Brugernavn" onChange={e => setUsername(e.target.value)} />
      <input type="password" placeholder="Adgangskode"
             onChange={e => setPassword(e.target.value)} />

      <button onClick={submit}>Log ind</button>

      {error && <p>{error}</p>}
    </main>
  );
}
