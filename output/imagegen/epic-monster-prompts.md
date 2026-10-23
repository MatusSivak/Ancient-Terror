# Epic monster artwork refresh

Generated with the built-in image_gen tool, matching the refreshed regular monster set. Replaces all 15 epic monster PNGs under their existing filenames in `assets/monster/epic/`. Current monster identities are retained, including Yig's two-legged reptilian design.

Final exports: 1024 x 1024 RGBA, proportionally fitted within 984 x 984 pixels and centered with a 20-pixel transparent safety border, preserving generated alpha. The labeled overview is `epic-monsters-preview.png`.

Validation: all 15 exports pass dimensions, RGBA, nonempty artwork, and transparent-border checks. Source-edge inspection and visual review found no clipped silhouettes. The changed PNG set exactly matches all 15 epic monsters; regular monster and cultist PNGs are unchanged. The production `:desktop:previewMonsterSheet` task passed for all 72 monsters, including label bounds, rule reattachment, health tokens, scrolling, and reuse. `:desktop:previewMonsterArtwork` passed artwork resolution, map, selection, card, and combat checks. The complete contact sheet and representative card and combat screenshots were visually inspected.

## Shared prompt

Use case: stylized-concept. Asset type: Ancient Terror dark cosmic-horror board-game EPIC monster sprite. Create a NEW original illustration matching the refreshed regular monster set: detailed realistic painterly fantasy bestiary cutout, tactile weathered skin, fur and fabric, muted charcoal, dusty ochre and desaturated olive colors, warm directional highlights and cool deep shadows. Rich sculptural detail, menacing restrained horror, commanding epic presence, clear readable silhouette at small token size. Composition: square 1024x1024, centered FULL BODY three-quarter view, occupy about 82 percent of canvas with at least 7 percent clear transparent margin on EVERY edge. Every horn, claw, wing, tendril, foot and tail must fit entirely inside the image. Background: actual transparent alpha including negative spaces between limbs. No environment, floor, cast shadow, scenery, pedestal, base, badge, circular token, frame, text, letters, logo, watermark or checkerboard. Subject:

## Per-monster subject prompts

### ChildrenOfYig.png

One uncanny serpent-humanoid offspring of Yig, an oversized rounded reptilian skull with large amber slit-pupil eyes and a forked tongue, a small lean scaled humanoid torso with exactly two long clawed arms, and one thick snake lower body tightly coiled into a broad compact base instead of legs. Weathered olive and dusty ochre scales, tense forward crouch, intelligent and unsettling rather than cute. Preserve this existing serpent-humanoid identity, no wings, crown, clothes or jewelry.

### Cthulhu.png

One colossal ancient Cthulhu, an immense broad-shouldered humanoid cosmic deity with a large cephalopod head, deep-set amber eyes, a long beard of thick hanging and curling facial tentacles, two huge clawed arms, two powerful taloned legs, and exactly two vast ragged batlike wings lifted behind the shoulders. Dark olive and charcoal leathery ridged skin, ancient scarred texture and heavy sculptural mass, commanding upright advancing pose. More monumental than the regular Star Spawn. Entire wings, feet and tentacle tips contained. No crown, clothing or scenery.

### Cthylla.png

One compact winged cephalopod cosmic horror, bulbous ridged reddish-umber head with a cluster of six small amber eyes, one vertical fanged maw, eight long muscular suckered octopus tentacles curling into a coherent low base, and exactly two small ragged membranous bat wings extending behind the head. Dark muted wine-red and umber flesh, weathered organic ridges, menacing nonhumanoid anatomy. All tentacles and wings visible.

### Doppelganger.png

One adult male shapeshifting doppelganger in worn 1920s brown coat, white shirt, loosened dark tie, trousers and boots. One side of its face and body still looks human and alarmed, while the other side transitions into a grinning charcoal-black shadow mimic with pale eyes, extended clawed fingers and jagged fibrous wisps. Two closely overlapping aspects of the SAME figure, coherent full-body pose with both feet visible, human hand recoiling and shadow hand reaching. No gore, no detached bust, no second unrelated person or background shadow.

### DunwichHorror.png

One enormous amorphous yellow-gray cosmic abomination, a broad oval leathery body densely covered in rope-like tentacles, many small amber eyes clustered over a huge irregular vertical tooth-lined maw, heavy trunklike lower appendages and fringes of shorter grasping tendrils. A vaguely face-like central arrangement without normal human anatomy, mottled ochre-gray hide with wrinkled ridges and ancient folds. Complete heavy compact creature, no ooze puddle, scenery or gore.

### Hydra.png

One immense ancient aquatic humanoid cosmic horror matching the project's single-headed Mother Hydra: broad hunched fishlike head with pale staring eyes, a large lipless toothy mouth surrounded by long hanging facial tendrils, massive mottled olive-gray torso, two powerful clawed arms and two squat webbed feet. Ragged membranous fins sweep from shoulders and flanks, barnacle-like encrustations and leathery maritime textures. Full heavy upright body, no wings, no multiple serpent heads, no water, ground or scenery.

### Nug.png

One squat massive cosmic abomination with rough dark plum-brown hide, a huge low broad snarling head fused into its torso, a pair of short curved horns, several amber eyes above a wide horizontal jagged-toothed maw, and a beard of fleshy tendrils. Two massive sinewy forearms end in long hooked claws, two short heavy legs support a crouched broad silhouette; a few thick curling appendages emerge from shoulders. Weathered sculptural texture, subdued reddish umber accents, no gore. Distinctive low wide silhouette.

### ShubNiggurath.png

One towering Black Goat cosmic deity, a vast black-olive organic trunk of knotted bark-like flesh with many scattered pale eyes and several toothy mouths, three heavy hoofed root-legs, a central horned goat-skull-like suggestion of a face, and a branching crown of long curling tentacles with a few open mouths at their tips. Ancient immense mass, shaggy charcoal strands intertwined with leathery ridges, imposing broad triangular silhouette. More elaborate and monumental than a regular Dark Young. All appendages contained, no tree foliage or landscape.

### TickTockMen.png

One sinister adult male representative of the Tick Tock Men, an unnervingly impassive pale face in the shade of a black fedora, weathered charcoal 1920s three-piece suit, long black-brown overcoat, white shirt, dark tie and worn black leather gloves. Small antique pocket-watch chain on waistcoat. One gloved hand holds a compact old revolver pointed down, the other rests on a slender dark cane. Complete upright figure with both shoes and cane tip visible. Painterly tactile fabric, restrained menace, no modern accessories, text or background.

### Tulzscha.png

One living pillar of eldritch green flame, a twisting vertical core of smoky olive-black fire with sickly green and pale yellow-green luminous edges, long curling flame tendrils forming a broad organic silhouette, several subtle eye-like shapes suggested inside the flames. Noncorporeal cosmic entity, no human body or face, no solid feet. Rich painterly fire texture and restrained green luminosity rather than fluorescent neon. Entire flame tips visible, actual alpha around every tendril; no fog rectangle, surrounding aura or scenery.

### WindWalker.png

One towering gaunt wind-spirit beast, an elongated skull-like horned head with hollow pale eyes and a narrow snarling mouth, matted icy gray-white fur streaming from shoulders, extremely long arms with hooked black claws, and an emaciated animalistic torso. Lower body tapers into twisting pale gray fur and spectral wind ribbons, complete floating silhouette. Cold ash-blue shadows with restrained warm bone highlights, ancient and predatory, no snowstorm, sky, ground or opaque mist.

### WingedSerpent.png

One immense winged serpent, long continuous dark olive scaled body curling into a dramatic upright S-shaped coil, one fierce narrow dragonlike snake head with amber eyes, swept-back spines, bared ivory fangs and forked tongue, and exactly two broad ragged leathery bat wings attached just behind its neck. Dusty ochre belly plates, weathered bronze highlights on scales and wing struts. No arms or legs, no feathered wings, no crown. Entire wings and tapering tail visible within square.

### Yeb.png

One tall wiry cosmic abomination related to Nug but distinctly upright, dark charcoal and muted rust-red sinewy hide, elongated snarling head with a cluster of ember-amber eyes, a huge narrow vertical tooth-filled maw, two curved crownlike organic horns and a beard of long writhing tendrils. Two long muscular arms end in clawed hands, two crooked digitigrade legs and curling shoulder tendrils. Lean looming silhouette rather than Nug's squat broad body. Organic weathered folds, no fire, gore or background glow.

### Yig.png

One formidable ancient humanoid serpent god matching the current project's Yig identity: long flattened rattlesnake-like head with amber eyes, open fanged mouth and forked tongue, broad powerful muscular reptilian torso, exactly two long scaled arms with strong clawed hands, exactly two muscular reptilian legs with taloned feet, and one heavy long snake tail curling low behind his legs. Mottled olive, dusty ochre and earth-brown scales with restrained bronze highlights. Upright dynamic threatening three-quarter stance, one foot stepping forward, arms spread. No clothing, armor, crown, jewelry, wings or horns. Anatomically nonsexual fantasy reptile. Complete feet, hands and tail visible.

### ZombieHorde.png

A compact threatening group of NINE distinct adult reanimated human corpses in ragged 1920s shirts, waistcoats, coats, trousers, long skirts and worn shoes, arranged in a dense triangular advancing crowd. Gaunt pallid gray-ochre faces, vacant milky eyes, desiccated skin, uneven reaching arms. Front three full figures clearly readable with all their feet visible, rear rows rise behind to show separate heads and shoulders. Cohesive full-body group silhouette, restrained horror without blood, exposed organs or gore. No ground, graves or background.
