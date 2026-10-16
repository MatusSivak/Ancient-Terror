# Ancient One Cultist artwork

Each Ancient One can provide a Cultist sprite at `assets/monster/cultist/<ANCIENT_ONE_ID>.png`.
Use the exact enum name: `YIG`, `AZATHOTH`, `CTHULHU`, `SHUB_NIGGURATH`, or `YOG_SOTHOTH`.
Only Yig currently has a custom sprite. Missing variants fall back to `assets/monster/Cultist.png`.

`MonsterArtwork` resolves paths from the current game's Ancient One, supplied by
`GameViewImpl` through its controller. It reads the current state on each request,
so starting/loading another game or awakening does not retain a previous choice.
Both synchronous monster textures and asynchronous map spawning use this resolver.
No monster IDs, class names, save fields, or combat rules change.

The Yig sprite is a 1254 × 1254 RGBA PNG generated with the built-in image tool.
The generic Cultist inspired the initial style, followed by a redesign with a
bare head, ochre tunic, exposed arms, rattlesnake, and offering bowl. The rejected
hooded draft is not a game asset.

## Final generation prompt

Use case: stylized-concept. Create a distinctly NEW Yig Cultist monster sprite, a human serpent devotee for a dark cosmic-horror board game. The supplied prior cultist sprite is a style reference ONLY: deliberately redesign the silhouette, clothing, face, props and pose so this character looks clearly different. Full body, three-quarter facing left, thin wiry adult male with a completely bare shaved head and gaunt weathered face, small patches of mottled serpent scales along temples and bare forearms. A sleeveless weathered ochre tunic and asymmetrical earth-brown shawl across ONE shoulder, faded bone-white linen wrappings around waist and calves, dark moss-green sash and simple sandals. Both arms exposed, crouched slightly forward with bent knees, wide grounded stance. One arm extended sideways carrying a long living rattlesnake that loops over the forearm and lifts its head; the other hand holds a shallow bronze offering bowl close to his chest. Sparse bronze snake charms on a cord, subtle serpentine tattoos. Dangerous concentrated expression, eye-catching human silhouette, an unsettling cult devotee, NOT a monster god. NO hood, cloak, staff, skulls, pentagrams, leather armor, feather crown, previous robe design, glowing chest emblem or glowing green orb. Realistic painterly oil-and-ink horror game asset, crisp small-scale readability, earthy ochre cloth, olive snakes, muted copper details, pale skin in cool light. Full figure and snake completely visible with generous clear margins all around. Square canvas, genuine transparent alpha, clean painted cutout edges; no background, floor, cast shadow, text, logo, frame or lettering.

## Validation

Unit tests cover switching game context, missing artwork, future Ancient One
assets, and unchanged non-Cultist/epic paths. Run the production sprite preview:

```powershell
.\gradlew.bat -I tools/report-fixes-preview/preview.gradle :desktop:previewMonsterArtwork --console=plain
```

It checks the real files and cached textures, renders sleeping/awakened Yig Cultist
and Yig epic cards, selection widgets, map tokens, combat sprites, and overviews.
It also verifies switching back to another Ancient One restores generic artwork.
