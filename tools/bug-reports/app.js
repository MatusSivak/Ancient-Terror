'use strict';
const $ = id => document.getElementById(id);
const session = new URLSearchParams(location.hash.slice(1)).get('session') || sessionStorage.getItem('viewerSession') || '';
sessionStorage.setItem('viewerSession', session);
history.replaceState(null, '', location.pathname);
let reports = [], selected = null, selectionVersion = 0, imageUrl = null;
let statuses = {}, mutating = false;
function statusLabel(value) { return statuses[value] || value || 'Unknown status'; }
function node(tag, text, cls) { const el = document.createElement(tag); if (text !== undefined) el.textContent = text; if (cls) el.className = cls; return el; }
function notice(message, error = false) { $('notice').textContent = message; $('notice').className = error ? 'error' : ''; }
async function api(path, blob = false, method = 'GET', body) {
  const response = await fetch(path, {method, headers: {'X-Viewer-Session': session, ...(body ? {'Content-Type': 'application/json'} : {})}, ...(body ? {body: JSON.stringify(body)} : {})});
  if (!response.ok) { const body = await response.json(); throw new Error(body.error || `HTTP ${response.status}`); }
  return blob ? response.blob() : response.json();
}
function reportPath(id, attachment) { return '/api/report?' + new URLSearchParams({id, ...(attachment ? {attachment} : {})}); }
function date(value) { if (!value) return 'Unknown date'; const d = new Date(/^\d+$/.test(String(value)) ? Number(value) : value); return Number.isNaN(d.getTime()) ? String(value) : d.toLocaleString(); }
function timestamp(r) { return Date.parse(r.createTime) || Number(r.fields.capturedAt) || 0; }
function filters() {
  for (const [id, value] of [['status', r => r.fields.status], ['platform', r => r.fields.metadata?.platform]]) {
    const select = $(id), previous = select.value;
    select.replaceChildren(new Option(id === 'status' ? 'All statuses' : 'All platforms', ''));
    [...new Set([...(id === 'status' ? Object.keys(statuses) : []), ...reports.map(value).filter(Boolean)])].sort().forEach(v => select.add(new Option(id === 'status' ? statusLabel(v) : v, v)));
    select.value = [...select.options].some(o => o.value === previous) ? previous : '';
  }
}
function renderList() {
  const search = $('search').value.toLowerCase();
  const visible = reports.filter(r => JSON.stringify(r).toLowerCase().includes(search) && (!$('status').value || r.fields.status === $('status').value) && (!$('platform').value || r.fields.metadata?.platform === $('platform').value));
  visible.sort((a,b) => ($('sort').value === 'newest' ? -1 : 1) * (timestamp(a)-timestamp(b)));
  $('count').textContent = `${visible.length} shown / ${reports.length} loaded`;
  $('reports').replaceChildren();
  for (const r of visible) {
    const button = node('button', undefined, 'report' + (r.id === selected ? ' selected' : ''));
    button.setAttribute('aria-pressed', String(r.id === selected));
    button.append(node('span', statusLabel(r.fields.status), 'badge'), node('strong', r.fields.description || '(No description)'), node('small', `${r.fields.metadata?.platform || 'Unknown platform'} · ${date(r.createTime || r.fields.capturedAt)}`));
    button.onclick = () => show(r.id); $('reports').append(button);
  }
  if (!visible.length) $('reports').append(node('p', reports.length ? 'No reports match these filters.' : 'No reports loaded.'));
}
async function refresh() {
  if (mutating) return;
  $('refresh').disabled = true; reports = []; filters(); renderList();
  let pageToken = ''; const tokens = new Set();
  try {
    do {
      notice(`Fetching reports… ${reports.length} loaded`);
      const page = await api('/api/reports?' + new URLSearchParams({pageToken}));
      statuses = page.statuses || {};
      $('project').textContent = `${page.project} / bugReports`;
      reports.push(...page.reports); filters(); renderList();
      pageToken = page.nextPageToken;
      if (pageToken && tokens.has(pageToken)) throw new Error('Firestore returned a repeated page token. Refresh to retry.');
      tokens.add(pageToken);
    } while (pageToken);
    notice(`${reports.length} reports loaded · Refreshed ${new Date().toLocaleTimeString()}`);
    if (selected && reports.some(r => r.id === selected)) await show(selected);
    else if (reports.length) await show([...reports].sort((a,b) => timestamp(b)-timestamp(a))[0].id);
    else { selected = null; selectionVersion++; clearImage(); $('detail').replaceChildren(node('h2', 'No bug reports yet')); }
  } catch (error) { notice(`${error.message}${reports.length ? ` (${reports.length} reports loaded; results are incomplete.)` : ''}`, true); }
  finally { $('refresh').disabled = false; }
}
function clearImage() { if (imageUrl) URL.revokeObjectURL(imageUrl); imageUrl = null; }
function table(values) {
  const dl = node('dl');
  for (const [key, value] of Object.entries(values)) dl.append(node('dt', key), node('dd', typeof value === 'object' ? JSON.stringify(value, null, 2) : String(value ?? '—')));
  return dl;
}
function downloadButton(id, type, label, filename) {
  const button = node('button', label);
  button.onclick = async () => {
    button.disabled = true;
    try { const blob = await api(reportPath(id, type), true); const url = URL.createObjectURL(blob); const a = node('a'); a.href = url; a.download = filename; a.click(); setTimeout(() => URL.revokeObjectURL(url), 1000); }
    catch (error) { notice(error.message, true); } finally { button.disabled = false; }
  };
  return button;
}
async function show(id) {
  if (mutating) return;
  selected = id; const version = ++selectionVersion; clearImage(); renderList(); $('detail').replaceChildren(node('p', 'Loading report…'));
  try {
    const r = await api(reportPath(id)); if (version !== selectionVersion) return;
    const f = r.fields, detail = $('detail'); detail.replaceChildren(node('span', statusLabel(f.status), 'badge'), node('h2', 'Report ' + id), node('p', date(r.createTime || f.capturedAt), 'hint'), management(r), node('h3', 'Problem description'), node('p', f.description || '(No description)', 'description'));
    const actions = node('div', undefined, 'actions');
    if (f.saveFile?.type === 'bytes') actions.append(downloadButton(id, 'saveFile', `Download save (${f.saveFile.size.toLocaleString()} bytes)`, id + '-save.json'));
    if (f.screenshotPng?.type === 'bytes') actions.append(downloadButton(id, 'screenshotPng', 'Download screenshot', id + '.png'));
    actions.append(downloadButton(id, 'raw', 'Download full report JSON', id + '-report.json')); detail.append(actions, node('h3', 'Screenshot'));
    const screenshot = node('div'); detail.append(screenshot);
    screenshot.append(node('p', f.screenshotPng?.type === 'bytes' ? 'Loading screenshot…' : `No screenshot attached (${f.metadata?.screenshotStatus || 'unavailable'}).`));
    detail.append(node('h3', 'Report details'), table({id:r.id, document:r.name, created:date(r.createTime), updated:date(r.updateTime), captured:date(f.capturedAt), reporterUid:f.reporterUid, status:f.status, schemaVersion:f.schemaVersion}), node('h3', 'Metadata'), table(f.metadata || {}), node('h3', 'Attachments'), table({saveFile:f.saveFile || 'Not attached', screenshotPng:f.screenshotPng || 'Not attached'}));
    const raw = node('details'); raw.append(node('summary', 'All fields (attachment sizes shown; download JSON for original bytes)'), node('pre', JSON.stringify(r, null, 2))); detail.append(raw);
    if (f.screenshotPng?.type === 'bytes') {
      try {
        const blob = await api(reportPath(id, 'screenshotPng'), true); if (version !== selectionVersion) return;
        imageUrl = URL.createObjectURL(blob); const a = node('a', undefined, 'screenshot-link'); a.href = imageUrl; a.target = '_blank'; a.rel = 'noopener';
        const img = node('img', undefined, 'screenshot'); img.src = imageUrl; img.alt = 'Game screenshot attached to report ' + id;
        img.onerror = () => screenshot.replaceChildren(node('p', 'The attached screenshot could not be displayed. Use Download screenshot to inspect the file.'));
        a.append(img); screenshot.replaceChildren(a, node('p', 'Click the screenshot to open it at full size.', 'hint'));
      } catch (error) { if (version === selectionVersion) screenshot.replaceChildren(node('p', error.message)); }
    }
  } catch (error) { if (version === selectionVersion) { const retry = node('button', 'Retry'); retry.onclick = () => show(id); $('detail').replaceChildren(node('p', error.message), retry); } }
}
function management(report) {
  const box = node('div', undefined, 'management'), label = node('label', 'Report status');
  const select = node('select');
  for (const [value, title] of Object.entries(statuses)) select.add(new Option(title, value));
  if (!Object.hasOwn(statuses, report.fields.status)) {
    const unknown = new Option(statusLabel(report.fields.status), '', true, true); unknown.disabled = true; select.add(unknown);
  } else select.value = report.fields.status;
  label.append(select);
  const save = node('button', 'Save status'), remove = node('button', 'Delete report', 'danger');
  const sync = () => { save.disabled = !select.value || select.value === report.fields.status; };
  select.onchange = sync; sync();
  async function change(method, body) {
    if (mutating || $('refresh').disabled) return;
    mutating = true; save.disabled = remove.disabled = select.disabled = $('refresh').disabled = true;
    let succeeded = false;
    try {
      const result = await api(reportPath(report.id), false, method, {...body, updateTime: report.updateTime});
      if (method === 'DELETE') {
        reports = reports.filter(r => r.id !== report.id); selected = null; selectionVersion++; clearImage();
        $('detail').replaceChildren(node('h2', 'Report deleted'), node('p', 'Select another report from the list.'));
      } else reports = reports.map(r => r.id === report.id ? result : r);
      filters(); renderList(); succeeded = true;
      notice(method === 'DELETE' ? 'Report permanently deleted.' : `Status saved: ${statusLabel(body.status)}.`);
    } catch (error) { notice(error.message, true); }
    finally { mutating = false; remove.disabled = select.disabled = $('refresh').disabled = false; sync(); }
    if (succeeded && method === 'PATCH') await show(report.id);
  }
  save.onclick = () => change('PATCH', {status: select.value});
  remove.onclick = () => {
    if (confirm(`Permanently delete report ${report.id}?\n\nIts description, screenshot, save file, and metadata will be removed. This cannot be undone.`)) {
      change('DELETE', {confirmId: report.id});
    }
  };
  box.append(label, save, remove); return box;
}
$('refresh').onclick = refresh;
['search','status','platform','sort'].forEach(id => $(id).addEventListener(id === 'search' ? 'input' : 'change', renderList));
refresh();
