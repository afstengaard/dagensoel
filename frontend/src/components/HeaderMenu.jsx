import { NavLink } from "react-router-dom";
import Countdown from "./Countdown";

export default function HeaderMenu() {
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

          <NavLink to="/admin/login" style={styles.link}>
            Admin
          </NavLink>

          <NavLink to="/beers" style={styles.link}>
            Beers
          </NavLink>
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
