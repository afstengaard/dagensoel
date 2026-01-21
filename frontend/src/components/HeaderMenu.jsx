import { NavLink } from "react-router-dom";
import { useEffect, useState } from "react";
import Countdown from "./Countdown";
import api from "../api/apiFacade";

export default function HeaderMenu() {
  const [loggedIn, setLoggedIn] = useState(api.loggedIn());

  useEffect(() => {
    const updateLoginState = () => setLoggedIn(api.loggedIn());

    window.addEventListener("loginChanged", updateLoginState);
    return () => window.removeEventListener("loginChanged", updateLoginState);
  }, []);

  return (
    <header style={styles.header}>
      <nav style={styles.nav}>
        <div style={styles.left}>
          <NavLink to="/" style={styles.link}>
            Home
          </NavLink>

          <NavLink to="/history" style={styles.link}>
            History
          </NavLink>

          <NavLink to="/beers" style={styles.link}>
            Beers
          </NavLink>

          {loggedIn ? (
            <NavLink to="/admin" style={styles.link}>
              Admin dashboard
            </NavLink>
          ) : (
            <NavLink to="/admin/login" style={styles.link}>
              Admin login
            </NavLink>
          )}

          {loggedIn && (
            <button onClick={api.logout} style={styles.link}>
              Logout
            </button>
          )}
        </div>

        <div style={styles.right}>
          <Countdown />
        </div>
      </nav>
    </header>
  );
}

const styles = {
  header: {
    padding: "12px 24px",
    borderBottom: "1px solid #ddd",
    marginBottom: "24px",
  },
  nav: {
    display: "flex",
    justifyContent: "space-between",
    alignItems: "center",
  },
  left: {
    display: "flex",
    gap: "16px",
  },
  right: {
    fontWeight: "bold",
    whiteSpace: "nowrap",
  },
  link: {
    textDecoration: "none",
    fontWeight: "bold",
    color: "#333",
  },
};
