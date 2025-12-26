import { NavLink } from "react-router-dom";

export default function HeaderMenu() {
  return (
    <header style={styles.header}>
      <nav style={styles.nav}>
        <NavLink to="/" style={styles.link}>
          Home
        </NavLink>

        <NavLink to="/history" style={styles.link}>
          History
        </NavLink>

        <NavLink to="/admin/login" style={styles.link}>
          Admin
        </NavLink>
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
    gap: "16px",
  },
  link: {
    textDecoration: "none",
    fontWeight: "bold",
    color: "#333",
  },
};
