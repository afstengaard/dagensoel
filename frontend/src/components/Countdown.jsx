import { useEffect, useState } from "react";

export default function Countdown() {
  const eventDate = new Date("2026-07-23T18:00:00");
  const [timeLeft, setTimeLeft] = useState(eventDate - new Date());

  useEffect(() => {
    const timer = setInterval(() => {
      setTimeLeft(eventDate - new Date());
    }, 1000);
    return () => clearInterval(timer);
  }, []);

  if (timeLeft <= 0) return <p>It’s beer time 🍻</p>;

  const days = Math.floor(timeLeft / (1000 * 60 * 60 * 24));

  return <p>{days} days until next tasting</p>;
}
