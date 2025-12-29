import { useEffect, useState } from "react";
import api from "../api/apiFacade";

export default function Countdown() {
  const [text, setText] = useState("Loading…");

  useEffect(() => {
    api.getActiveEvent()
      .then(event => {
        if (!event?.startDate) {
          setText("No event planned");
          return;
        }

        const [year, month, day] = event.startDate;
        const startDate = new Date(year, month - 1, day);
        const today = new Date();

        // Normalize to midnight to avoid timezone issues
        startDate.setHours(0, 0, 0, 0);
        today.setHours(0, 0, 0, 0);

        const diffMs = startDate - today;
        const diffDays = Math.ceil(diffMs / (1000 * 60 * 60 * 24));

        if (diffDays > 1) {
          setText(`${diffDays} days until next tasting`);
        } else if (diffDays === 1) {
          setText("1 day until next tasting");
        } else if (diffDays === 0) {
          setText("Today! 🍻");
        } else {
          setText("Event has started");
        }
      })
      .catch(() => {
        setText("No event planned");
      });
  }, []);

  return <span>{text}</span>;
}
