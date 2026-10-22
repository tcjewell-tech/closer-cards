// Run: node --test web/export.test.mjs
import { test } from 'node:test';
import assert from 'node:assert/strict';
import { readData, buildPage } from './export.mjs';

test('exports every deck by default', async () => {
  const data = await readData();
  assert.deepEqual(data['decks.json'].map((d) => d.id), ['main', 'after-dark', 'after-darker']);
  for (const d of data['decks.json']) assert.equal(data[d.file].length, 36, d.file);
});

test('--main-only drops the decks that have a consent screen', async () => {
  const data = await readData({ mainOnly: true });
  assert.deepEqual(data['decks.json'].map((d) => d.id), ['main']);
  assert.deepEqual(Object.keys(data).sort(), ['cards.json', 'decks.json']);
});

test('buildPage inlines data before the first script and survives awkward text', () => {
  const html = '<body><script>run()</script></body>';
  const out = buildPage(html, { x: ['</script><b>$& $\' end'] });
  assert.ok(out.indexOf('CLOSER_STATIC') < out.indexOf('run()'));
  assert.equal(out.match(/<\/script>/g).length, 2, 'a question must not close the script tag early');
  // Run the injected assignment and check the text came through unchanged.
  const injected = out.match(/window\.CLOSER_STATIC = (.*);<\/script>/)[1];
  assert.deepEqual(JSON.parse(injected), { x: ['</script><b>$& $\' end'] });
});

test('buildPage refuses a page with no script', () => {
  assert.throws(() => buildPage('<body></body>', {}), /no <script>/);
});
