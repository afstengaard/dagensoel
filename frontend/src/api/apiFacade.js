const API_URL = import.meta.env.VITE_API_BASE_URL || "http://localhost:7070";

//TOKEN

const setToken = (token) => {
  if (token && token !== "undefined") {
    localStorage.setItem("token", token);
  } else {
    localStorage.removeItem("token");
  }
};

const getToken = () => localStorage.getItem("token");

const loggedIn = () => !!getToken();

const logout = () => {
  localStorage.removeItem("token");
  window.dispatchEvent(new Event("loginChanged"));
};

//OPTIONS

const makeOptions = (method, addToken, body) => {
  const opts = {
    method,
    headers: {
      "Content-Type": "application/json",
      Accept: "application/json",
    },
  };

  if (addToken && loggedIn()) {
    opts.headers["Authorization"] = `Bearer ${getToken()}`;
  }

  if (body) {
    opts.body = JSON.stringify(body);
  }

  opts.headers["X-Device-Id"] = getDeviceId();

  return opts;
};

//FETCH HELPERS

const fetchJson = async (endpoint, options) => {
  const res = await fetch(`${API_URL}${endpoint}`, options);

  if (!res.ok) {
    let error;
    try {
      error = await res.json();
    } catch {
      throw new Error("Server error");
    }
    throw new Error(error.message || "Request failed");
  }

  return res.json();
};

const fetchNoContent = async (endpoint, options) => {
  const res = await fetch(`${API_URL}${endpoint}`, options);

  if (!res.ok) {
    const text = await res.text();
    throw new Error(text || "Request failed");
  }

  return; // nothing to parse
};

const fetchWithAuth = (endpoint, method = "GET", body = null) =>
  fetchJson(endpoint, makeOptions(method, true, body));

//AUTH

const login = async (username, password) => {
  const data = await fetchJson(
    "/api/auth/login",
    makeOptions("POST", false, { username, password })
  );

  setToken(data.token);
  window.dispatchEvent(new Event("loginChanged"));
  return data;
};

const getDeviceId = () => {
  let id = localStorage.getItem("deviceId");
  if (!id) {
    id = crypto.randomUUID();
    localStorage.setItem("deviceId", id);
  }
  return id;
};

//EVENTS

const getEventByCode = (code) =>
  fetchJson(`/api/events/${code}`, makeOptions("GET", false));

const createEvent = (event) =>
  fetchWithAuth("/api/admin/events", "POST", event);

const getActiveEvent = () =>
  fetchJson("/api/events/active", makeOptions("GET", false));

const updateEventStatus = (id, status) =>
  fetchWithAuth(`/api/admin/events/${id}/status`, "POST", { status });

const deleteEvent = (id) =>
  fetchNoContent(`/api/admin/events/${id}`, makeOptions("DELETE", true));

const importHistoricalData = (file) => {
  const formData = new FormData();
  formData.append("file", file);

  const opts = { method: "POST", headers: {}, body: formData };
  // Note: no Content-Type header here on purpose - the browser sets the
  // multipart boundary itself when the body is a FormData object.
  if (loggedIn()) {
    opts.headers["Authorization"] = `Bearer ${getToken()}`;
  }
  opts.headers["X-Device-Id"] = getDeviceId();

  return fetchJson("/api/admin/import/historical", opts);
};

const getEventResults = (eventId) =>
  fetchJson(`/api/events/${eventId}/results`, makeOptions("GET", false));


//VOTES

const submitVotes = (code, votes) =>
  fetchNoContent(`/api/events/${code}/votes`, makeOptions("POST", false, votes));

//BEERS

const addBeerToEvent = (eventId, beer) =>
  fetchWithAuth(`/api/admin/events/${eventId}/beers`, "POST", beer);

const setBeerImageUrl = (beerId, imageUrl) =>
  fetchWithAuth(`/api/admin/beers/${beerId}/image`, "POST", { imageUrl });

const searchBeers = (query) =>
  fetchJson(
    `/api/beers/search?q=${encodeURIComponent(query)}`,
    makeOptions("GET", false)
  );

const getBeerHistory = () =>
  fetchJson("/api/beers/history", makeOptions("GET", false));


//HISTORY

const getEventHistory = () =>
  fetchJson("/api/events/history", makeOptions("GET", false));

//EXPORT

const apiFacade = {
  login,
  logout,
  loggedIn,
  getDeviceId,
  getEventByCode,
  getActiveEvent,
  createEvent,
  updateEventStatus,
  deleteEvent,
  importHistoricalData,
  getEventResults,
  submitVotes,
  addBeerToEvent,
  setBeerImageUrl,
  getBeerHistory,
  searchBeers,
  getEventHistory,
};

export default apiFacade;
