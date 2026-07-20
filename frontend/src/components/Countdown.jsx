import { useEffect, useState } from "react";
import api from "../api/apiFacade";

export default function Countdown() {
  const [text, setText] = useState("Indlæser…");

  useEffect(() => {
    api.getActiveEvent()
      .then(event => {
        if (!event?.startDate) {
          setText("Ingen smagning planlagt");
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
          setText(`${diffDays} dage til næste smagning`);
        } else if (diffDays === 1) {
          setText("1 dag til næste smagning");
        } else if (diffDays === 0) {
          setText("I dag! 🍻");
        } else {
          setText("Smagningen er startet");
        }
      })
      .catch(() => {
        setText("Ingen smagning planlagt");
      });
  }, []);

  return <span>{text}</span>;
}
