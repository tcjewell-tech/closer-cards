// Run: node --test web/server.test.mjs
import { test } from 'node:test';
import assert from 'node:assert/strict';
import { readFileSync } from 'node:fs';
import { listDecks, validateDeck, serializeDeck } from './server.mjs';

const deck = JSON.parse(readFileSync(new URL('../app/src/main/assets/cards.json', import.meta.url), 'utf8'));

test('accepts every deck in both deck folders', async () => {
  const decks = await listDecks();
  assert.deepEqual(decks.map((d) => d.deck.id), ['main', 'after-dark', 'after-darker']);
  for (const d of decks) assert.equal(validateDeck(JSON.parse(readFileSync(d.url, 'utf8'))), null, d.deck.file);
});

test('adult decks live outside the Android app and each has a consent screen', async () => {
  for (const { deck: d, url } of await listDecks()) {
    // --main-only and the Android build both rely on this split.
    assert.equal(Boolean(d.consent), url.pathname.includes('/web/decks/'), d.id);
  }
});

test('rejects a missing card', () => {
  assert.match(validateDeck(deck.slice(1)), /36 cards/);
});

test('rejects a duplicate position', () => {
  const dup = deck.map((c, i) => (i === 1 ? { ...c, id: 'x', order: 1 } : c));
  assert.match(validateDeck(dup), /two cards at position 1/);
});

test('rejects blank text', () => {
  assert.match(validateDeck(deck.map((c, i) => (i === 5 ? { ...c, text: '   ' } : c))), /empty/);
});

test('rejects non-array input', () => {
  assert.ok(validateDeck({}));
});

test('serializer round-trips and keeps one card per line', () => {
  const out = serializeDeck([...deck].reverse());
  assert.deepEqual(JSON.parse(out), deck);
  assert.equal(out.split('\n').length, 36 + 3); // [ + 36 cards + ] + trailing newline
});
