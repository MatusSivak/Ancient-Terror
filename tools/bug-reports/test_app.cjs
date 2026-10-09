// Offline browser checks: npm install playwright, then node --test test_app.cjs.
const {test} = require('node:test');
const assert = require('node:assert/strict');
const fs = require('node:fs');
const path = require('node:path');
const {chromium} = require('playwright');

async function fixture(t, failedCollection, options = {}) {
  const browser = await chromium.launch(process.env.VIEWER_TEST_BROWSER
    ? {executablePath: process.env.VIEWER_TEST_BROWSER} : {});
  t.after(() => browser.close());
  const page = await browser.newPage({viewport: {width: 1280, height: 900}});
  const errors = [], requests = [];
  let viewerSession = 'current-session';
  if (options.staleStorage) await page.addInitScript(() => sessionStorage.setItem('viewerSession', 'old-session'));
  if (options.blockStorage) await page.addInitScript(() => {
    Object.defineProperty(window, 'sessionStorage', {get() { throw new Error('Storage is unavailable'); }});
  });
  page.on('pageerror', error => errors.push(error));
  const make = (collection, id, time) => ({collection, id, name: `${collection}/${id}`,
    createTime: time, updateTime: time, fields: {status: 'new',
      description: collection === 'crashReports' ? 'TEST crash\n at Game.render(Game.java:1)' : 'A reported bug',
      metadata: {platform: 'Desktop'}, saveFile: {type: 'bytes', size: 4},
      screenshotPng: {type: 'bytes', size: 68}}});
  let reports = [make('bugReports', 'same-id', '2026-09-28T10:00:00Z'),
    make('bugReports', 'second-page', '2026-09-27T10:00:00Z'),
    make('crashReports', 'same-id', '2026-09-29T10:00:00Z')];
  await page.route('http://viewer.test/**', async route => {
    const request = route.request(), url = new URL(request.url());
    const collection = url.searchParams.get('collection');
    requests.push({path: url.pathname, collection, attachment: url.searchParams.get('attachment'),
      token: url.searchParams.get('pageToken'), method: request.method(), session: request.headers()['x-viewer-session']});
    const json = body => route.fulfill({json: body});
    if (url.pathname.startsWith('/api/') && request.headers()['x-viewer-session'] !== viewerSession) {
      return route.fulfill({status: 403, json: {error: 'Viewer session expired or missing. Reload this page to reconnect.'}});
    }
    if (url.pathname === '/api/reports') {
      if (collection === failedCollection) return route.fulfill({status: 502, json: {error: 'Access denied'}});
      const items = reports.filter(r => r.collection === collection);
      const offset = url.searchParams.get('pageToken') ? 1 : 0;
      return json({reports: items.slice(offset, offset + 1), nextPageToken: items.length > offset + 1 ? 'next' : '',
        project: 'test', statuses: {new: 'New', fixed: 'Fixed'}});
    }
    if (url.pathname === '/api/report') {
      const report = reports.find(r => r.collection === collection && r.id === url.searchParams.get('id'));
      assert.ok(report, 'Request must select the correct collection and ID');
      if (request.method() === 'PATCH') {
        const body = request.postDataJSON();
        assert.equal(body.updateTime, report.updateTime);
        if (options.failMutation) return route.fulfill({status: 502, json: {error: 'This report changed. Refresh and try again.'}});
        for (const field of ['status', 'title']) if (Object.hasOwn(body, field)) report.fields[field] = body[field];
        report.updateTime = new Date(Date.parse(report.updateTime) + 1000).toISOString();
        return json(report);
      }
      if (request.method() === 'DELETE') {
        reports = reports.filter(r => r !== report);
        return json({deleted: report.id});
      }
      const attachment = url.searchParams.get('attachment');
      if (attachment === 'screenshotPng') return route.fulfill({contentType: 'image/png', body: Buffer.from(
        'iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mP8/x8AAwMCAO+a4WQAAAAASUVORK5CYII=', 'base64')});
      if (attachment === 'saveFile') return route.fulfill({body: 'save'});
      return json(report);
    }
    const files = {'/': ['index.html', 'text/html'], '/app.js': ['app.js', 'text/javascript'], '/style.css': ['style.css', 'text/css']};
    if (!files[url.pathname]) return route.fulfill({status: 404, body: ''});
    const [name, contentType] = files[url.pathname];
    let body = fs.readFileSync(path.join(__dirname, name), 'utf8');
    if (url.pathname === '/') body = body.replace('__VIEWER_SESSION__', viewerSession);
    return route.fulfill({contentType, body});
  });
  await page.goto('http://viewer.test/' + (options.hash || ''));
  await page.waitForFunction(() => !document.getElementById('refresh').disabled);
  return {page, requests, errors, restart: () => { viewerSession = 'restarted-session'; }};
}

test('plain bookmark loads both collections without browser storage', async t => {
  const {page, requests, errors} = await fixture(t, undefined, {blockStorage: true});
  assert.equal(await page.locator('#reports .report').count(), 3);
  assert.ok(requests.filter(r => r.path.startsWith('/api/')).every(r => r.session === 'current-session'));
  assert.deepEqual(errors, []);
});

test('stale launch fragment and tab storage cannot override current session', async t => {
  const {page, requests, errors} = await fixture(t, undefined, {staleStorage: true, hash: '#session=old-session'});
  assert.equal(await page.locator('#reports .report').count(), 3);
  assert.equal(page.url(), 'http://viewer.test/');
  assert.ok(requests.filter(r => r.path.startsWith('/api/')).every(r => r.session === 'current-session'));
  assert.deepEqual(errors, []);
});

test('page reload reconnects after viewer restarts', async t => {
  const {page, requests, errors, restart} = await fixture(t);
  restart();
  await page.getByRole('button', {name: 'Refresh reports'}).click();
  await page.waitForFunction(() => !document.getElementById('refresh').disabled);
  assert.match(await page.locator('#notice').textContent(), /Reload this page to reconnect/);
  await page.reload();
  await page.waitForFunction(() => !document.getElementById('refresh').disabled);
  assert.equal(await page.locator('#reports .report').count(), 3);
  assert.ok(requests.some(r => r.collection === 'bugReports' && r.session === 'restarted-session'));
  assert.ok(requests.some(r => r.collection === 'crashReports' && r.session === 'restarted-session'));
  assert.deepEqual(errors, []);
});

test('both collections, pagination, filters, attachments and same-ID mutations', async t => {
  const {page, requests, errors} = await fixture(t);
  assert.equal(await page.locator('#reports .report').count(), 3);
  assert.ok(requests.some(r => r.collection === 'bugReports' && r.token === 'next'));
  await page.selectOption('#type', 'crashReports');
  assert.equal(await page.locator('#reports .report').count(), 1);
  await page.locator('#reports .report').click();
  await page.getByRole('heading', {name: 'Exception and stack trace'}).waitFor();
  await page.locator('img.screenshot').waitFor();
  for (const name of ['Download save', 'Download screenshot', 'Download full report JSON']) {
    const download = page.waitForEvent('download');
    await page.getByRole('button', {name, exact: false}).click();
    await download;
  }
  for (const attachment of ['saveFile', 'screenshotPng', 'raw']) {
    assert.ok(requests.some(r => r.collection === 'crashReports' && r.attachment === attachment));
  }
  await page.locator('.management select').selectOption('fixed');
  await page.getByRole('button', {name: 'Save status'}).click();
  await page.waitForFunction(() => document.querySelector('#detail .badge')?.textContent === 'Fixed');
  await page.selectOption('#type', 'bugReports');
  await page.locator('#reports .report').filter({hasText: 'A reported bug'}).first().click();
  await page.getByRole('heading', {name: 'Problem description'}).waitFor();
  assert.equal(await page.locator('.management select').inputValue(), 'new');
  await page.selectOption('#type', 'crashReports');
  await page.locator('#reports .report').click();
  await page.getByRole('heading', {name: 'Exception and stack trace'}).waitFor();
  page.once('dialog', dialog => dialog.accept());
  await page.getByRole('button', {name: 'Delete report'}).click();
  await page.getByRole('heading', {name: 'Report deleted'}).waitFor();
  await page.selectOption('#type', '');
  assert.equal(await page.locator('#reports .report').count(), 2);
  assert.ok(requests.filter(r => ['PATCH', 'DELETE'].includes(r.method)).every(r => r.collection === 'crashReports'));
  assert.deepEqual(errors, []);
});

test('one failed collection keeps the other available with a warning', async t => {
  const {page, errors} = await fixture(t, 'bugReports');
  assert.equal(await page.locator('#reports .report').count(), 1);
  assert.match(await page.locator('#notice').textContent(), /Bug reports: Access denied.*incomplete/);
  await page.getByRole('heading', {name: 'Exception and stack trace'}).waitFor();
  assert.deepEqual(errors, []);
});

test('titles persist, are searchable, and preserve descriptions and statuses in both collections', async t => {
  const {page, errors} = await fixture(t);
  for (const collection of ['crashReports', 'bugReports']) {
    await page.selectOption('#type', collection);
    await page.locator('#reports .report').first().click();
    const title = page.getByLabel('Report title'), save = page.getByRole('button', {name: 'Save title'});
    await title.waitFor();
    const description = await page.locator('.description').textContent();
    assert.equal(await save.isDisabled(), true);
    const name = `${collection} <script>alert(1)</script>`;
    await title.fill('  ' + name + '  ');
    await title.press('Enter');
    await page.getByRole('heading', {name, exact: true}).waitFor();
    assert.equal(await page.locator('.description').textContent(), description);
    assert.equal(await page.getByLabel('Report status').inputValue(), 'new');
    await page.getByLabel('Report status').selectOption('fixed');
    await page.getByRole('button', {name: 'Save status'}).click();
    await page.waitForFunction(() => document.querySelector('#detail .badge')?.textContent === 'Fixed');
    assert.equal(await title.inputValue(), name);
    await page.getByRole('button', {name: 'Refresh reports'}).click();
    await page.waitForFunction(() => !document.getElementById('refresh').disabled);
    await page.locator('#search').fill(name);
    assert.equal(await page.locator('#reports .report').count(), 1);
    assert.equal(await page.locator('#reports .report strong').textContent(), name);
    await title.fill(''); await save.click();
    await page.waitForFunction(() => document.querySelector('#notice').textContent === 'Title saved.');
    await page.getByRole('heading', {name: /^(Bug|Crash) report same-id$/}).waitFor();
    await page.locator('#search').fill('');
    assert.equal(await page.locator('#reports .report strong').first().textContent(), description);
  }
  assert.deepEqual(errors, []);
});

test('failed title save keeps the draft and original list title', async t => {
  const {page, errors} = await fixture(t, undefined, {failMutation: true});
  const original = await page.locator('#reports .selected strong').textContent();
  await page.getByLabel('Report title').fill('Draft title');
  await page.getByRole('button', {name: 'Save title'}).click();
  await page.waitForFunction(() => document.querySelector('#notice').className === 'error');
  assert.match(await page.locator('#notice').textContent(), /Refresh/);
  assert.equal(await page.getByLabel('Report title').inputValue(), 'Draft title');
  assert.equal(await page.getByRole('button', {name: 'Save title'}).isEnabled(), true);
  assert.equal(await page.locator('#reports .selected strong').textContent(), original);
  assert.deepEqual(errors, []);
});
