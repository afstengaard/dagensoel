import { useEffect, useState } from "react";
import "../styles/retro-banner.css";

const ADS = [
  {
    style: "blink",
    bg: "linear-gradient(90deg, #ff0000, #ffff00)",
    text: "🎉 TILLYKKE!! DU ER DEN 1.000.000. BESØGENDE! KLIK FOR AT HENTE DIN PRÆMIE! 🎉",
    alert: "Præmien var faktisk bare en øl. Skål! 🍺",
  },
  {
    style: "marquee",
    bg: "linear-gradient(90deg, #ff00ff, #00ffff)",
    text: "🔥 HOT SINGLES I DIT OMRÅDE VIL SMAGE DIN IPA — KLIK NU 🔥",
    alert: "Der er ingen singler. Der er kun øl. Det er nok.",
  },
  {
    style: "blink",
    bg: "#00ff00",
    text: "💰 TJEN PENGE HJEMMEFRA MENS DU DRIKKER ØL — SPØRG MIG HVORDAN 💰",
    alert: "Trin 1: Drik øl. Trin 2: Der er intet trin 2.",
  },
  {
    style: "marquee",
    bg: "linear-gradient(90deg, #ffcc00, #ff6600)",
    text: "🐒 SLÅ ABEN OG VIND EN GRATIS ØL TIL NÆSTE SMAGNING 🐒",
    alert: "Aben stak af. Du vandt ingenting. Klassisk 2003.",
  },
  {
    style: "blink",
    bg: "#0000ff",
    text: "⚠️ DIN COMPUTER KØRER LANGSOMT — SCAN NU FOR AT FIKSE (VIRKER IKKE) ⚠️",
    alert: "Din computer har det fint. Det er bare Internet Explorer der er langsom.",
  },
  {
    style: "marquee",
    bg: "linear-gradient(90deg, #9900ff, #ff00cc)",
    text: "📠 GRATIS RINGETONER — KRÆVER 56K MODEM — DOWNLOAD NU 📠",
    alert: "Downloaden tager 45 minutter og blokerer telefonlinjen. Værd det.",
  },
];

export default function RetroBannerAd() {
  const [index, setIndex] = useState(() => Math.floor(Math.random() * ADS.length));

  useEffect(() => {
    const interval = setInterval(() => {
      setIndex((i) => (i + 1) % ADS.length);
    }, 7000);
    return () => clearInterval(interval);
  }, []);

  const ad = ADS[index];

  return (
    <div
      className="retro-banner"
      style={{ background: ad.bg }}
      onClick={() => alert(ad.alert)}
    >
      <span className={ad.style === "blink" ? "retro-banner-blink" : ""}>
        {ad.style === "marquee" ? (
          <span className="retro-banner-marquee-track">
            <span>{ad.text}</span>
            <span>{ad.text}</span>
          </span>
        ) : (
          ad.text
        )}
      </span>
    </div>
  );
}
