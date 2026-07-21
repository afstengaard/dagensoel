import { useParams } from "react-router-dom";
import { useEffect, useState } from "react";
import api from "../api/apiFacade";
import ImageLightbox from "../components/ImageLightbox";
import "../styles/responsive-table.css";

export default function Vote() {
  const { code } = useParams();

  const [event, setEvent] = useState(null);
  const [message, setMessage] = useState(null);

  const [favoriteBeerId, setFavoriteBeerId] = useState(null);
  const [secondBeerId, setSecondBeerId] = useState(null);
  const [submitted, setSubmitted] = useState(false);
  const [lightboxImage, setLightboxImage] = useState(null);

  useEffect(() => {
    api.getEventByCode(code)
      .then(setEvent)
      .catch(() => setMessage("Event ikke fundet"));
  }, [code]);

  if (!event) return <p>Indlæser...</p>;

  if (event.status !== "VOTING") {
    return <p>Afstemningen er ikke åben.</p>;
  }

  async function submitVotes() {
    setMessage(null);

    if (!favoriteBeerId || !secondBeerId) {
      setMessage("Vælg både en favorit og en andenplads.");
      return;
    }

    if (favoriteBeerId === secondBeerId) {
      setMessage("Favorit og andenplads skal være to forskellige øl.");
      return;
    }

    try {
      await api.submitVotes(code, {
        favoriteBeerId,
        secondBeerId,
      });

      setSubmitted(true);
      setMessage("Tak for din stemme! 🍻");
    } catch (e) {
      setMessage(e.message || "Kunne ikke indsende stemmer");
    }
  }

  return (
    <main>
      <h1>Stem på {event.name}</h1>

      {message && <p>{message}</p>}

      <div className="table-scroll">
      <table className="results-table">
        <thead>
          <tr>
            <th>Billede</th>
            <th>Navn</th>
            <th>Bryggeri</th>
            <th>Favorit (2 point)</th>
            <th>Andenplads (1 point)</th>
          </tr>
        </thead>
        <tbody>
          {event.beers.map((beer) => (
            <tr
              key={beer.id}
              className={
                beer.id === favoriteBeerId
                  ? "vote-favorite"
                  : beer.id === secondBeerId
                  ? "vote-second"
                  : ""
              }
            >
              <td className="cell-image">
                {beer.imageUrl ? (
                  <img
                    src={beer.imageUrl}
                    alt={beer.name}
                    onClick={() =>
                      setLightboxImage({ src: beer.imageUrl, alt: beer.name })
                    }
                    onError={(e) => {
                      e.currentTarget.style.display = "none";
                    }}
                  />
                ) : null}
              </td>
              <td className="cell-title" data-label="Navn">{beer.name}</td>
              <td data-label="Bryggeri">{beer.brewery}</td>
              <td data-label="Favorit (2 point)">
                <button
                  disabled={submitted}
                  onClick={() => setFavoriteBeerId(beer.id)}
                >
                  {favoriteBeerId === beer.id ? "Valgt" : "Vælg"}
                </button>
              </td>
              <td data-label="Andenplads (1 point)">
                <button
                  disabled={submitted}
                  onClick={() => setSecondBeerId(beer.id)}
                >
                  {secondBeerId === beer.id ? "Valgt" : "Vælg"}
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
      </div>

      <br />

      {!submitted && (
        <button onClick={submitVotes}>
          Indsend stemmer
        </button>
      )}

      <ImageLightbox
        src={lightboxImage?.src}
        alt={lightboxImage?.alt}
        onClose={() => setLightboxImage(null)}
      />
    </main>
  );
}
