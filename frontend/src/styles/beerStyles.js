// Danish beer style guide, transcribed from the club's printed style chart.
// Codes (e.g. "5A") are what gets stored on a beer; names are for display.
// If you spot a transcription mistake against the physical poster, this is
// the one place to fix it.

export const BEER_STYLE_CATEGORIES = [
  {
    number: 1,
    name: "Lys lager",
    styles: [
      { code: "1A", name: "Moderne lys lager" },
      { code: "1B", name: "International lager" },
      { code: "1C", name: "Tjekkisk pilsner" },
      { code: "1D", name: "Tysk pilsner" },
      { code: "1E", name: "München helles" },
    ],
  },
  {
    number: 2,
    name: "Mørk lager",
    styles: [
      { code: "2A", name: "Moderne mørk lager" },
      { code: "2B", name: "Wiener" },
      { code: "2C", name: "Märzen" },
      { code: "2D", name: "Münchener dunkel" },
      { code: "2E", name: "Mørk tjekkisk lager" },
      { code: "2F", name: "Schwarzbier" },
      { code: "2G", name: "Klassisk røgøl" },
    ],
  },
  {
    number: 3,
    name: "Bock",
    styles: [
      { code: "3A", name: "Helles bock / maibock" },
      { code: "3B", name: "Bock" },
      { code: "3C", name: "Doppelbock" },
      { code: "3D", name: "Eisbock" },
      { code: "3E", name: "Weizenbock" },
    ],
  },
  {
    number: 4,
    name: "Lys ale",
    styles: [
      { code: "4A", name: "Engelsk ordinary bitter" },
      { code: "4B", name: "Engelsk best bitter" },
      { code: "4C", name: "Engelsk strong bitter / pale ale" },
      { code: "4D", name: "Engelsk golden ale" },
      { code: "4E", name: "Amerikansk pale ale" },
      { code: "4F", name: "California common / steam beer" },
      { code: "4G", name: "Kölsch" },
    ],
  },
  {
    number: 5,
    name: "Mørk ale",
    styles: [
      { code: "5A", name: "Engelsk brown ale" },
      { code: "5B", name: "Amerikansk brown ale" },
      { code: "5C", name: "Engelsk dark mild" },
      { code: "5D", name: "Altbier" },
      { code: "5E", name: "Skotsk ale" },
      { code: "5F", name: "Skotsk stærk ale / Wee heavy" },
      { code: "5G", name: "Old ale" },
    ],
  },
  {
    number: 6,
    name: "India Pale Ale (IPA)",
    styles: [
      { code: "6A", name: "Engelsk IPA" },
      { code: "6B", name: "Amerikansk IPA" },
      { code: "6C", name: "Imperial IPA" },
    ],
  },
  {
    number: 7,
    name: "Hvedeøl",
    styles: [
      { code: "7A", name: "Weizen / Weissbier" },
      { code: "7B", name: "Dunkelweizen" },
      { code: "7C", name: "Belgisk wit" },
    ],
  },
  {
    number: 8,
    name: "Porter / Stout",
    styles: [
      { code: "8A", name: "Dry stout" },
      { code: "8B", name: "Sweet stout" },
      { code: "8C", name: "Oatmeal stout" },
      { code: "8D", name: "Porter" },
      { code: "8E", name: "Moderne porter og stout" },
    ],
  },
  {
    number: 9,
    name: "Stærk øl",
    styles: [
      { code: "9A", name: "Engelsk barley wine" },
      { code: "9B", name: "Amerikansk barley wine" },
      { code: "9C", name: "Foreign extra stout" },
      { code: "9D", name: "Baltisk porter" },
      { code: "9E", name: "Imperial stout" },
    ],
  },
  {
    number: 10,
    name: "Lys belgisk ale",
    styles: [
      { code: "10A", name: "Belgisk blond" },
      { code: "10B", name: "Lys stærk belgisk ale / tripel" },
      { code: "10C", name: "Bière de garde" },
      { code: "10D", name: "Saison" },
    ],
  },
  {
    number: 11,
    name: "Mørk belgisk ale",
    styles: [
      { code: "11A", name: "Belgisk dubbel / bruin" },
      { code: "11B", name: "Mørk stærk belgisk ale" },
    ],
  },
  {
    number: 12,
    name: "Syrlig øl",
    styles: [
      { code: "12A", name: "Gueuze lambic" },
      { code: "12B", name: "Belgisk frugt lambic" },
      { code: "12C", name: "Flamsk rød" },
      { code: "12D", name: "Flamsk brun / oud bruin" },
      { code: "12E", name: "Berliner weisse" },
    ],
  },
  {
    number: 13,
    name: "Special-øl",
    styles: [
      { code: "13A", name: "Frugt- / grøntsagsøl" },
      { code: "13B", name: "Krydret øl" },
      { code: "13C", name: "Trælagret øl" },
      { code: "13D", name: "Andre røgøl" },
      { code: "13E", name: "Andre tilsætninger" },
    ],
  },
];

export function findStyle(code) {
  if (!code) return null;
  for (const category of BEER_STYLE_CATEGORIES) {
    const style = category.styles.find((s) => s.code === code);
    if (style) {
      return { ...style, category: category.name, categoryNumber: category.number };
    }
  }
  return null;
}

export function styleLabel(code) {
  const style = findStyle(code);
  return style ? `${style.code} – ${style.name}` : "";
}
