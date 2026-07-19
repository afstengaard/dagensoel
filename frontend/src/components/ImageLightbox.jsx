import { useEffect } from "react";

/**
 * Simple full-screen image viewer. Renders nothing when `src` is falsy.
 * Closes on backdrop click, the close button, or Escape.
 */
export default function ImageLightbox({ src, alt, onClose }) {
  useEffect(() => {
    if (!src) return;

    function handleKeyDown(e) {
      if (e.key === "Escape") onClose();
    }

    document.addEventListener("keydown", handleKeyDown);
    return () => document.removeEventListener("keydown", handleKeyDown);
  }, [src, onClose]);

  if (!src) return null;

  return (
    <div
      onClick={onClose}
      style={{
        position: "fixed",
        inset: 0,
        backgroundColor: "rgba(0, 0, 0, 0.85)",
        display: "flex",
        alignItems: "center",
        justifyContent: "center",
        zIndex: 1000,
        cursor: "zoom-out",
        padding: "2rem",
      }}
    >
      <button
        onClick={onClose}
        aria-label="Close"
        style={{
          position: "absolute",
          top: "1rem",
          right: "1.5rem",
          background: "none",
          border: "none",
          color: "white",
          fontSize: "2rem",
          lineHeight: 1,
          cursor: "pointer",
        }}
      >
        ×
      </button>
      <img
        src={src}
        alt={alt}
        onClick={(e) => e.stopPropagation()}
        style={{
          maxWidth: "100%",
          maxHeight: "100%",
          objectFit: "contain",
          cursor: "default",
          boxShadow: "0 0 24px rgba(0,0,0,0.6)",
        }}
      />
    </div>
  );
}
