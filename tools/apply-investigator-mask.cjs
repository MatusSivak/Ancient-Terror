// Apply the exact established portrait alpha without resampling or changing RGB.
// Requires Node.js and sharp. Run from any directory: node tools/apply-investigator-mask.cjs
const fs = require('node:fs/promises');
const path = require('node:path');
const assert = require('node:assert/strict');
const sharp = require('sharp');

async function main() {
  const directory = path.join(__dirname, '..', 'assets', 'investigator');
  const reference = await sharp(path.join(directory, 'THE_ACTRESS.png'))
    .ensureAlpha().raw().toBuffer({resolveWithObject: true});
  const {width, height} = reference.info;
  for (const name of ['THE_BUTLER', 'THE_PRIEST', 'THE_NUN', 'THE_EXPLORER']) {
    const file = path.join(directory, name + '.png');
    const original = await sharp(file).ensureAlpha().raw().toBuffer({resolveWithObject: true});
    assert.equal(original.info.width, width);
    assert.equal(original.info.height, height);
    const pixels = Buffer.from(original.data);
    for (let i = 3; i < pixels.length; i += 4) pixels[i] = reference.data[i];
    const png = await sharp(pixels, {raw: {width, height, channels: 4}}).png().toBuffer();
    const decoded = await sharp(png).raw().toBuffer();
    assert.deepEqual(decoded, pixels, 'PNG must preserve every RGB and mask byte');
    await fs.writeFile(file, png);
    console.log(`${name}: exact shared alpha applied; RGB unchanged (${width} x ${height})`);
  }
}
main().catch(error => { console.error(error); process.exitCode = 1; });
