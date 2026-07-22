import { useEffect, useState } from "react";
import { useParams, useNavigate, Link } from "react-router-dom";
import api from "../api/apiFacade";
import { BEER_STYLE_CATEGORIES } from "../data/beerStyles";
import "../styles/responsive-table.css";

const emptyNewBeer = {
  name: "",
  brewery: "",
  country: "",
  abv: "",
  submittedBy: "",
  evening: "",
  untappdLink: "",
  imageUrl: "",
  style: "",
  totalPoints: "",
};

export default function EditEvent() {
  const { eventId } = useParams();
  const navigate = useNavigate();

  const [event, setEvent] = useState(null);
  const [beers, setBeers] = useState([]);
  const [eventName, setEventName] = useState("");
  const [eventDate, setEventDate] = useState("");
  const [newBeer, setNewBeer] = useState(emptyNewBeer);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (!api.loggedIn()) {
      navigate("/admin/login");
      return;
    }
    loadEvent();
  }, [eventId]);

  async function loadEvent() {
    try {
      const data = await api.getEventForEdit(eventId);
      setEvent(data);
      setEventName(data.name);
      setEventDate(data.startDate || "");
      setBeers(data.beers.map((b) => ({ ...b, abv: String(b.abv), totalPoints: String(b.totalPoints) })));
    } catch (err) {
      alert(`Kunne ikke hente event: ${err.message}`);
    } finally {
      setLoading(false);
    }
  }

  async function saveEventDetails() {
    try {
      await api.updateEvent(eventId, { name: eventName, startDate: eventDate });
      alert("Event gemt");
    } catch (err) {
      alert(`Kunne ikke gemme event: ${err.message}`);
    }
  }

  function updateBeerField(index, field, value) {
    setBeers((prev) =>
      prev.map((b, i) => (i === index ? { ...b, [field]: value } : b))
    );
  }

  async function saveBeer(index) {
    const beer = beers[index];

    const abv = Number(beer.abv);
    const totalPoints = Number(beer.totalPoints);

    if (!beer.name?.trim() || !beer.brewery?.trim() || !beer.submittedBy?.trim()) {
      alert("Navn, bryggeri og indsendt af skal udfyldes");
      return;
    }
    if (isNaN(abv) || abv < 0) {
      alert("ABV skal være et gyldigt tal");
      return;
    }
    if (isNaN(totalPoints)) {
      alert("Point skal være et tal");
      return;
    }

    try {
      const updated = await api.updateBeer(beer.id, {
        name: beer.name.trim(),
        brewery: beer.brewery.trim(),
        country: beer.country?.trim() || "",
        abv,
        submittedBy: beer.submittedBy.trim(),
        evening: beer.evening || null,
        untappdLink: beer.untappdLink?.trim() || null,
        imageUrl: beer.imageUrl?.trim() || null,
        style: beer.style || null,
        totalPoints,
      });

      updateBeerField(index, "totalPoints", String(updated.totalPoints));
      alert(`"${beer.name}" gemt`);
    } catch (err) {
      alert(`Kunne ikke gemme øl: ${err.message}`);
    }
  }

  async function removeBeer(index) {
    const beer = beers[index];
    if (!confirm(`Slet "${beer.name}"? Dette kan ikke fortrydes.`)) return;

    try {
      await api.deleteBeer(beer.id);
      setBeers((prev) => prev.filter((_, i) => i !== index));
    } catch (err) {
      alert(`Kunne ikke slette øl: ${err.message}`);
    }
  }

  async function addBeer() {
    const abv = Number(newBeer.abv);
    const totalPoints = newBeer.totalPoints === "" ? 0 : Number(newBeer.totalPoints);

    if (!newBeer.name.trim() || !newBeer.brewery.trim() || !newBeer.submittedBy.trim()) {
      alert("Navn, bryggeri og indsendt af skal udfyldes");
      return;
    }
    if (isNaN(abv) || abv < 0) {
      alert("ABV skal være et gyldigt tal");
      return;
    }
    if (isNaN(totalPoints)) {
      alert("Point skal være et tal");
      return;
    }

    try {
      const created = await api.addBeerToEvent(eventId, {
        name: newBeer.name.trim(),
        brewery: newBeer.brewery.trim(),
        country: newBeer.country.trim(),
        abv,
        submittedBy: newBeer.submittedBy.trim(),
        evening: newBeer.evening || null,
        untappdLink: newBeer.untappdLink.trim() || null,
        imageUrl: newBeer.imageUrl.trim() || null,
        style: newBeer.style || null,
      });

      // The create endpoint doesn't take points directly (new beers start
      // at 0), so if a starting point value was given, patch it in via update.
      if (totalPoints !== 0) {
        await api.updateBeer(created.id, {
          name: created.name,
          brewery: created.brewery,
          country: created.country,
          abv: created.abv,
          submittedBy: created.submittedBy,
          evening: created.evening,
          untappdLink: created.untappdLink,
          imageUrl: created.imageUrl,
          style: created.style,
          totalPoints,
        });
      }

      setNewBeer(emptyNewBeer);
      await loadEvent();
    } catch (err) {
      alert(`Kunne ikke tilføje øl: ${err.message}`);
    }
  }

  if (loading) return <p>Indlæser…</p>;
  if (!event) return <p>Event ikke fundet.</p>;

  return (
    <main>
      <Link to="/admin">← Tilbage til admin-dashboard</Link>

      <h1>Rediger event</h1>

      <div style={{ marginBottom: "1.5rem" }}>
        <input
          value={eventName}
          onChange={(e) => setEventName(e.target.value)}
          placeholder="Eventnavn"
        />
        <input
          type="date"
          value={eventDate}
          onChange={(e) => setEventDate(e.target.value)}
        />
        <button onClick={saveEventDetails}>Gem event</button>
      </div>

      <h2>Øl</h2>

      <div className="table-scroll table-fullwidth">
        <table className="results-table">
          <thead>
            <tr>
              <th>Navn</th>
              <th>Bryggeri</th>
              <th>Land</th>
              <th>ABV</th>
              <th>Indsendt af</th>
              <th>Aften</th>
              <th>Stil</th>
              <th>Untappd</th>
              <th>Billed-URL</th>
              <th>Point</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            {beers.map((beer, index) => (
              <tr key={beer.id}>
                <td data-label="Navn">
                  <input
                    value={beer.name}
                    onChange={(e) => updateBeerField(index, "name", e.target.value)}
                  />
                </td>
                <td data-label="Bryggeri">
                  <input
                    value={beer.brewery}
                    onChange={(e) => updateBeerField(index, "brewery", e.target.value)}
                  />
                </td>
                <td data-label="Land">
                  <input
                    value={beer.country || ""}
                    onChange={(e) => updateBeerField(index, "country", e.target.value)}
                  />
                </td>
                <td data-label="ABV">
                  <input
                    type="number"
                    step="0.1"
                    value={beer.abv}
                    onChange={(e) => updateBeerField(index, "abv", e.target.value)}
                    style={{ width: 70 }}
                  />
                </td>
                <td data-label="Indsendt af">
                  <input
                    value={beer.submittedBy}
                    onChange={(e) => updateBeerField(index, "submittedBy", e.target.value)}
                  />
                </td>
                <td data-label="Aften">
                  <select
                    value={beer.evening || ""}
                    onChange={(e) => updateBeerField(index, "evening", e.target.value)}
                  >
                    <option value="">–</option>
                    <option value="Første aften">Første aften</option>
                    <option value="Anden aften">Anden aften</option>
                  </select>
                </td>
                <td data-label="Stil">
                  <select
                    value={beer.style || ""}
                    onChange={(e) => updateBeerField(index, "style", e.target.value)}
                  >
                    <option value="">Vælg ølstil...</option>
                    {BEER_STYLE_CATEGORIES.map((category) => (
                      <optgroup key={category.number} label={`${category.number}. ${category.name}`}>
                        {category.styles.map((style) => (
                          <option key={style.code} value={style.code}>
                            {style.code} – {style.name}
                          </option>
                        ))}
                      </optgroup>
                    ))}
                  </select>
                </td>
                <td data-label="Untappd">
                  <input
                    value={beer.untappdLink || ""}
                    onChange={(e) => updateBeerField(index, "untappdLink", e.target.value)}
                    placeholder="https://..."
                    style={{ width: 140 }}
                  />
                </td>
                <td data-label="Billed-URL">
                  <input
                    value={beer.imageUrl || ""}
                    onChange={(e) => updateBeerField(index, "imageUrl", e.target.value)}
                    placeholder="https://..."
                    style={{ width: 140 }}
                  />
                </td>
                <td data-label="Point">
                  <input
                    type="number"
                    value={beer.totalPoints}
                    onChange={(e) => updateBeerField(index, "totalPoints", e.target.value)}
                    style={{ width: 60 }}
                  />
                </td>
                <td data-label="Handlinger">
                  <button onClick={() => saveBeer(index)}>Gem</button>
                  <button onClick={() => removeBeer(index)}>Slet</button>
                </td>
              </tr>
            ))}

            <tr>
              <td data-label="Navn">
                <input
                  value={newBeer.name}
                  onChange={(e) => setNewBeer({ ...newBeer, name: e.target.value })}
                  placeholder="Ny øl - navn"
                />
              </td>
              <td data-label="Bryggeri">
                <input
                  value={newBeer.brewery}
                  onChange={(e) => setNewBeer({ ...newBeer, brewery: e.target.value })}
                  placeholder="Bryggeri"
                />
              </td>
              <td data-label="Land">
                <input
                  value={newBeer.country}
                  onChange={(e) => setNewBeer({ ...newBeer, country: e.target.value })}
                  placeholder="Land"
                />
              </td>
              <td data-label="ABV">
                <input
                  type="number"
                  step="0.1"
                  value={newBeer.abv}
                  onChange={(e) => setNewBeer({ ...newBeer, abv: e.target.value })}
                  style={{ width: 70 }}
                />
              </td>
              <td data-label="Indsendt af">
                <input
                  value={newBeer.submittedBy}
                  onChange={(e) => setNewBeer({ ...newBeer, submittedBy: e.target.value })}
                  placeholder="Indsendt af"
                />
              </td>
              <td data-label="Aften">
                <select
                  value={newBeer.evening}
                  onChange={(e) => setNewBeer({ ...newBeer, evening: e.target.value })}
                >
                  <option value="">–</option>
                  <option value="Første aften">Første aften</option>
                  <option value="Anden aften">Anden aften</option>
                </select>
              </td>
              <td data-label="Stil">
                <select
                  value={newBeer.style}
                  onChange={(e) => setNewBeer({ ...newBeer, style: e.target.value })}
                >
                  <option value="">Vælg ølstil...</option>
                  {BEER_STYLE_CATEGORIES.map((category) => (
                    <optgroup key={category.number} label={`${category.number}. ${category.name}`}>
                      {category.styles.map((style) => (
                        <option key={style.code} value={style.code}>
                          {style.code} – {style.name}
                        </option>
                      ))}
                    </optgroup>
                  ))}
                </select>
              </td>
              <td data-label="Untappd">
                <input
                  value={newBeer.untappdLink}
                  onChange={(e) => setNewBeer({ ...newBeer, untappdLink: e.target.value })}
                  placeholder="https://..."
                  style={{ width: 140 }}
                />
              </td>
              <td data-label="Billed-URL">
                <input
                  value={newBeer.imageUrl}
                  onChange={(e) => setNewBeer({ ...newBeer, imageUrl: e.target.value })}
                  placeholder="https://..."
                  style={{ width: 140 }}
                />
              </td>
              <td data-label="Point">
                <input
                  type="number"
                  value={newBeer.totalPoints}
                  onChange={(e) => setNewBeer({ ...newBeer, totalPoints: e.target.value })}
                  style={{ width: 60 }}
                />
              </td>
              <td data-label="Handlinger">
                <button onClick={addBeer}>Tilføj</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </main>
  );
}
