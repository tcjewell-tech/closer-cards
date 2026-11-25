// Builds a standalone, offline copy of the game that runs from a double-click: dist/closer-cards-web.zip
// Run: node web/export.mjs            (all decks)
//      node web/export.mjs --main-only (leaves out decks that have a consent screen, i.e. the adult ones)
import { readFile, writeFile, mkdir, rm } from 'node:fs/promises';
import { execFileSync } from 'node:child_process';
import { fileURLToPath, pathToFileURL } from 'node:url';
import { listDecks, validateDeck } from './server.mjs';

const ROOT = new URL('../', import.meta.url);
const asPath = (url) => fileURLToPath(url);

/** Loads the deck list plus each deck's cards, keyed by file name, and refuses to ship an invalid deck. */
export async function readData({ mainOnly = false } = {}) {
  let decks = await listDecks();
  if (mainOnly) decks = decks.filter((d) => !d.deck.consent);
  const data = { 'decks.json': decks.map((d) => d.deck) };
  for (const { deck, url } of decks) {
    data[deck.file] = JSON.parse(await readFile(url, 'utf8'));
    const error = validateDeck(data[deck.file]);
    if (error) throw new Error(`${deck.file}: ${error}`);
  }
  return data;
}

/** Inlines the deck data into the page so it needs no server and no network. */
export function buildPage(html, data) {
  if (!html.includes('<script>')) throw new Error('index.html has no <script> to put the data before');
  // < stops a "</script>" inside a question from ending the tag early.
  const json = JSON.stringify(data).replace(/</g, '\\u003c');
  // A replacer function, because a plain string would treat "$&" or "$'" in a question as patterns.
  return html.replace('<script>', () => `<script>window.CLOSER_STATIC = ${json};</script>\n<script>`);
}

const readme = (adult) => `Closer Cards (test build)

Open index.html (double-click it). It runs in your browser, works offline, and
doesn't send or save anything.

Share one screen, tap the card to flip it, and take turns answering. Either of
you can skip any question.
${adult ? '\nThe After Dark and After Darker decks are explicit and for adults only. Each one\nasks you both to agree before it starts.\n' : ''}
This is a test version, so any feedback is welcome.
`;

if (process.argv[1] && import.meta.url === pathToFileURL(process.argv[1]).href) {
  const mainOnly = process.argv.includes('--main-only');
  const data = await readData({ mainOnly });
  const dist = new URL('dist/', ROOT);
  const dir = new URL('closer-cards/', dist);
  await rm(dir, { recursive: true, force: true });
  await rm(new URL('closer-cards-web.zip', dist), { force: true });
  await mkdir(dir, { recursive: true });
  await writeFile(new URL('index.html', dir), buildPage(await readFile(new URL('web/index.html', ROOT), 'utf8'), data));
  await writeFile(new URL('README.txt', dir), readme(!mainOnly));
  // Full path on purpose: Git for Windows puts GNU tar first on PATH, and it silently writes a tar, not a zip.
  // shortcut: Windows-only (bsdtar ships with Windows 10+); use a zip library to build on other systems.
  const bsdtar = `${process.env.SystemRoot ?? 'C:\\Windows'}\\System32\\tar.exe`;
  execFileSync(bsdtar, ['-a', '-cf', 'closer-cards-web.zip', 'closer-cards'], { cwd: asPath(dist) });
  const zip = await readFile(new URL('closer-cards-web.zip', dist));
  if (zip.subarray(0, 4).toString('latin1') !== 'PK\x03\x04') throw new Error('Output is not a zip file; nothing to send');
  console.log(`Wrote ${asPath(new URL('closer-cards-web.zip', dist))} (${data['decks.json'].length} deck${data['decks.json'].length > 1 ? 's' : ''})`);
}
