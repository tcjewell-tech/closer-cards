// Local-only dev server for the web version: serves the game and reads/writes the app's deck.
// Run: node web/server.mjs
import { createServer } from 'node:http';
import { readFile, writeFile, rename } from 'node:fs/promises';
import { fileURLToPath, pathToFileURL } from 'node:url';

// URL path -> deck file. The main deck is the Android app's; After Dark is web-only for now.
const DECKS = {
  '/cards.json': fileURLToPath(new URL('../app/src/main/assets/cards.json', import.meta.url)),
  '/cards-after-dark.json': fileURLToPath(new URL('./cards-after-dark.json', import.meta.url)),
};
const PAGE = fileURLToPath(new URL('./index.html', import.meta.url));
const PORT = Number(process.env.PORT) || 8787;
const MAX_BODY = 64 * 1024;
// Rejecting other Host headers blocks DNS-rebinding pages from writing the deck.
const ALLOWED_HOSTS = new Set([`127.0.0.1:${PORT}`, `localhost:${PORT}`]);

/** Same shape rule as loadDeck() in Card.kt: sets 1-3 x orders 1-12, each exactly once. Returns an error or null. */
export function validateDeck(cards) {
  if (!Array.isArray(cards)) return 'Deck must be an array of cards';
  const positions = new Set();
  const ids = new Set();
  for (const c of cards) {
    if (typeof c?.id !== 'string' || !c.id) return 'Every card needs a string id';
    if (ids.has(c.id)) return `Two cards share the id ${c.id}`;
    ids.add(c.id);
    if (!Number.isInteger(c.set) || c.set < 1 || c.set > 3 || !Number.isInteger(c.order) || c.order < 1 || c.order > 12) {
      return `Card ${c.id}: set must be 1-3 and order 1-12`;
    }
    if (typeof c.text !== 'string' || !c.text.trim()) return `Card ${c.id}: the question is empty`;
    const pos = `${c.set}-${c.order}`;
    if (positions.has(pos)) return `Set ${c.set} has two cards at position ${c.order}`;
    positions.add(pos);
  }
  if (positions.size !== 36) return `The deck needs 36 cards (3 sets of 12), got ${positions.size}`;
  return null;
}

/** One card per line, matching the hand-written file, so git diffs show only edited questions. */
export function serializeDeck(cards) {
  const sorted = [...cards].sort((a, b) => a.set - b.set || a.order - b.order);
  const lines = sorted.map((c) =>
    `  { "id": ${JSON.stringify(c.id)}, "set": ${c.set}, "order": ${c.order}, "text": ${JSON.stringify(c.text.trim())} }`);
  return `[\n${lines.join(',\n')}\n]\n`;
}

async function handle(req, res) {
  const send = (status, body, type = 'text/plain; charset=utf-8') => {
    res.writeHead(status, { 'Content-Type': type, 'Cache-Control': 'no-store' });
    res.end(body);
  };
  if (!ALLOWED_HOSTS.has(req.headers.host)) return send(403, 'Forbidden');

  if (req.method === 'GET' && req.url === '/') return send(200, await readFile(PAGE), 'text/html; charset=utf-8');
  const deckFile = Object.hasOwn(DECKS, req.url) ? DECKS[req.url] : null;
  if (!deckFile) return send(404, 'Not found');
  if (req.method === 'GET') return send(200, await readFile(deckFile), 'application/json; charset=utf-8');
  if (req.method !== 'PUT') return send(405, 'Method not allowed');

  let body = '';
  for await (const chunk of req) {
    body += chunk;
    if (body.length > MAX_BODY) return send(413, 'Deck is too large');
  }
  let cards;
  try {
    cards = JSON.parse(body);
  } catch {
    return send(400, 'Not valid JSON');
  }
  const error = validateDeck(cards);
  if (error) return send(400, error);

  // Write then rename, so a crash mid-write can't leave a truncated deck.
  const tmp = `${deckFile}.tmp`;
  await writeFile(tmp, serializeDeck(cards));
  await rename(tmp, deckFile);
  send(200, 'Saved');
}

if (process.argv[1] && import.meta.url === pathToFileURL(process.argv[1]).href) {
  createServer((req, res) =>
    handle(req, res).catch((e) => {
      if (!res.headersSent) res.writeHead(500, { 'Content-Type': 'text/plain; charset=utf-8' });
      res.end(`Server error: ${e.message}`);
    }),
  ).listen(PORT, '127.0.0.1', () => console.log(`Closer Cards web: http://127.0.0.1:${PORT}`));
}
