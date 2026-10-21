# Typewriter glyph

`typewriter.svg` is the editable vector source for `assets/glyphs/typewriter.png`.
The icon replaces the flip-card symbol: paper and typed lines indicate that the
card's typewriter resolution follows. The raster is a transparent 256×256 PNG.

Regenerate with Node.js and Sharp available in the module search path:

```javascript
require('sharp')('source_assets/glyphs/typewriter.svg')
  .resize(256, 256).png().toFile('assets/glyphs/typewriter.png');
```
