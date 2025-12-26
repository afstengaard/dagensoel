import { BrowserRouter, Routes, Route } from "react-router-dom";
import Home from "./pages/Home";
import History from "./pages/History";
import Vote from "./pages/Vote";
import Event from "./pages/Event";
import EventResults from "./pages/EventResults";
import NotFound from "./pages/NotFound";
import AdminLogin from "./pages/AdminLogin";
import AdminDashboard from "./pages/AdminDashboard";
import CreateEvent from "./pages/CreateEvent";
import HeaderMenu from "./components/HeaderMenu";


export default function App() {
  return (
    <BrowserRouter>
      <HeaderMenu />
      
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/history" element={<History />} />
        <Route path="/vote" element={<Vote />} />
        <Route path="/results/:eventId" element={<EventResults />} />
        <Route path="/event/:code" element={<Event />} />
        <Route path="/event/:code/vote" element={<Vote />} />
        <Route path="/admin/login" element={<AdminLogin />} />
        <Route path="/admin" element={<AdminDashboard />} />
        <Route path="/admin/events/create" element={<CreateEvent />} />
        <Route path="*" element={<NotFound />} />
      </Routes>
    </BrowserRouter>
  );
}
