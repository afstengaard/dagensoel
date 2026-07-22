import { NavLink } from "react-router-dom";
import { useEffect, useState } from "react";
import Countdown from "./Countdown";
import RetroBannerAd from "./RetroBannerAd";
import api from "../api/apiFacade";

export default function HeaderMenu() {
  const [loggedIn, setLoggedIn] = useState(api.loggedIn());
  const [adsEnabled, setAdsEnabled] = useState(() => {
    const stored = localStorage.getItem("retroAdsEnabled");
    return stored === null ? true : stored === "true";
  });

  useEffect(() => {
    const updateLoginState = () => setLoggedIn(api.loggedIn());

    window.addEventListener("loginChanged", updateLoginState);
    return () => window.removeEventListener("loginChanged", updateLoginState);
  }, []);

  function toggleAds() {
    setAdsEnabled((prev) => {
      const next = !prev;
      localStorage.setItem("retroAdsEnabled", String(next));
      return next;
    });
  }

  return (
    <>
      <header style={styles.header}>
        <nav style={styles.nav}>
          <div style={styles.left}>
            <NavLink to="/" style={styles.link}>
              Hjem
            </NavLink>

            <NavLink to="/history" style={styles.link}>
              Historik
            </NavLink>

            <NavLink to="/beers" style={styles.link}>
              Øl
            </NavLink>

            <NavLink to="/stats" style={styles.link}>
              Statistik
            </NavLink>

            {loggedIn ? (
              <NavLink to="/admin" style={styles.link}>
                Admin-dashboard
              </NavLink>
            ) : (
              <NavLink to="/admin/login" style={styles.link}>
                Admin login
              </NavLink>
            )}

            {loggedIn && (
              <button onClick={api.logout} style={styles.link}>
                Log ud
              </button>
            )}
          </div>

          <div style={styles.right}>
            <button onClick={toggleAds} style={styles.adsToggle}>
              {adsEnabled ? "🚫 Skjul reklamer" : "📢 Vis reklamer"}
            </button>
            <Countdown />
          </div>
        </nav>
      </header>

      {adsEnabled && <RetroBannerAd />}
    </>
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
    display: "flex",
    alignItems: "center",
    gap: "12px",
    fontWeight: "bold",
    whiteSpace: "nowrap",
  },
  adsToggle: {
    fontSize: "0.8rem",
    padding: "4px 8px",
    cursor: "pointer",
    border: "1px solid #ccc",
    borderRadius: "4px",
    background: "white",
  },
  link: {
    textDecoration: "none",
    fontWeight: "bold",
    color: "#333",
  },
};
