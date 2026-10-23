# Regular monster artwork refresh

Generated with the built-in image_gen tool, matching the 26 expansion monster sprites created on October 22, 2026. Replaces the 31 older regular monster PNGs under their existing filenames in `assets/monster/`. The recently redesigned Yig cultist and all epic monsters retain their existing artwork.

Final exports: 512 x 512 RGBA, proportionally fitted within 492 x 492 pixels, centered on a transparent canvas, preserving generated alpha. The labeled overview is `regular-monsters-preview.png`.

Validation: all 31 exports pass size, RGBA, nonempty artwork, and transparent-border checks. The replacement set exactly covers the 31 regular monsters outside yesterday's 26 expansion sprites. The production `:desktop:previewMonsterSheet` task passed for all 72 bundled monsters, including label bounds, rule reattachment, health tokens, scrolling, and reuse. The complete contact sheet and representative production card screenshots were visually inspected.

## Shared prompt

Use case: stylized-concept. Asset type: Ancient Terror dark cosmic-horror board-game monster sprite. Create a NEW original illustration in exactly the style of the expansion monster set: detailed realistic painterly fantasy bestiary cutout, tactile weathered skin, fur and fabric, muted charcoal, dusty ochre and desaturated olive colors, warm directional highlights and cool deep shadows. Clear readable silhouette at small token size, menacing restrained horror. Composition: square 1024x1024, centered FULL BODY three-quarter view, occupy about 82 percent of canvas with at least 7 percent transparent margin on EVERY edge. Every horn, finger, wing and tail must fit entirely inside the image. Background: actual transparent alpha including negative spaces between limbs. No environment, floor, cast shadow, scenery, base, badge, circular token, frame, text, letters, logo, watermark or checkerboard. Subject:

## Per-monster subject prompts

### Byakhee.png

One bestial winged cosmic predator, a hunched sinewy bird-bat-insect hybrid with a narrow hooked toothy beak, dull pale eyes, ridged charcoal torso, two huge weathered leathery bat wings, two long clawed forearms and two powerful taloned hind legs. Broad heavy crouch distinct from the lean Hunting Byakhee; all wing tips contained.

### ColourOutOfSpace.png

One noncorporeal alien colour entity, a compact vertical twisting knot of translucent opalescent tendrils around a dark luminous core, subtle violet, muted blue-green and sickly yellow iridescence, wisps forming an uncanny almost-organic silhouette. Painterly, restrained luminosity rather than neon rainbow; no normal face, human anatomy, eyes, stars or background nebula. Alpha surrounds every wisp.

### Cthonian.png

One mature massive burrowing chthonian, leathery charcoal-ochre annulated worm body coiled into a broad heavy base, raised bulbous front crowned with many powerful curling octopus-like feeding tentacles concealing its maw. Blind, no eyes, no legs or wings. More massive and richly ridged than the Young Cthonian. Entire tail and tentacles contained.

### Cultist.png

One sinister adult human cult devotee, gaunt stern pale face inside a weathered charcoal hood, layered ragged dark olive ritual robes, worn boots, a belt of small bone talismans, holding a tall gnarled ritual staff with a small aged skull finial in one hand. Other hand slightly outstretched. Restrained green stone amulet, no lettering or readable symbols. Full standing body and staff.

### DarkYoung.png

One towering dark young abomination, a massive bark-like black trunk torso with several gaping vertical mouths, three thick gnarled hoofed root-legs and a crown of long curling tentacles instead of a head. Mossy charcoal and umber hide, sculptural knotted flesh, huge unsettling organic mass. Entire tentacle crown fits square, no actual tree foliage.

### DeepOne.png

One amphibious fish-frog humanoid predator, broad hunched muscular slate-olive scaled body, bulging amber fish eyes, wide lipless mouth with fine needle teeth, visible neck gills, dorsal ridges, long webbed clawed hands and broad webbed hind feet. No clothing or props. Low stalking three-quarter stance.

### ElderThing.png

One ancient alien with a tall barrel-shaped ribbed leathery torso, five-pointed star-shaped head with small sensory tubes, folded radial membranous fan wings flaring behind, five flexible lower tentacular limbs splaying beneath. Symmetrical alien plant-echinoderm anatomy, dark olive and umber with muted ochre ridges, not humanoid.

### Ghost.png

One mournful spectral adult apparition in tattered antiquated burial clothing, hollow sunken face, two thin reaching hands, a complete flowing pale gray-green shroud dissolving into curling translucent wisps below. Quiet haunting upright silhouette, distinctive from the more violent Poltergeist. Translucent entity, no opaque fog backdrop.

### Ghoul.png

One lean graveyard scavenger humanoid, canine snout with bared worn fangs, pointed ears, mottled pallid gray-ochre hide, hunched spine, sinewy long arms and clawed hands, bent digitigrade legs with clawed feet. Feral crouch, scraps of dark burial cloth around waist, restrained horror without gore.

### GnophKeh.png

One huge shaggy six-limbed arctic cosmic beast, white-gray matted fur with dirty ochre shadows, a single long rhinoceros-like horn on its bestial head, tiny dark eyes and tusked snarling mouth. Two pairs of clawed forelimbs and one pair of thick hind legs, heavy compact stance, all six limbs visually readable. No snow or ground.

### GoatSpawn.png

One sinister upright goat-human abomination, horned goatlike head with narrow amber eyes, coarse dark umber fur on shoulders and haunches, sinewy humanoid torso, two clawed hands, two digitigrade legs ending in cloven hooves. Ragged brown waistcloth, forward aggressive stance. No extra tentacles; distinct from the larger Child of the Goat.

### Gug.png

One massive shaggy subterranean giant with charcoal-brown fur, a long vertical tooth-lined slit splitting its entire head, two small eyes to either side of that slit, two enormous arms each bifurcating at elbow into two clawed forearms, and two heavy bent hind legs. Four hands clearly readable, hulking low three-quarter posture.

### HoundOfTindalos.png

One emaciated alien hound, angular skeletal canine body with gray-green leathery skin, sharp ridged spine, very long narrow jaws with needle teeth and a dark forked tongue, four sinewy clawed legs and one long whiplike tail. Low tense stalking silhouette, no smoke, geometric portal or base.

### Lloigor.png

One solid manifestation of a reptilian cosmic entity, heavy dark olive dragonlike body with a long serpentine neck and narrow crested head, many short curling facial feelers, four powerful splayed clawed legs, and a long heavy curling tail. Rugged segmented ridges and weathered scales, compact low coiled stance, no wings or scenery.

### Maniac.png

One threatening adult human in worn 1920s clothes, unkempt hair, hollow feverish eyes, ragged brown coat over dirty shirt and trousers, heavy boots, gripping a battered wood-handled axe down at one side. Forward hunched stalking stance, no blood or gore, complete figure and axe visible.

### MiGo.png

One alien crustacean-fungoid creature, segmented pinkish-umber chitin body, bulbous head composed of clustered fungal nodules and short feelers rather than a face, six jointed clawlike limbs, and two broad veined membranous wings. Floating crouched three-quarter pose, earthy muted palette and leathery organic textures, all wing tips visible.

### Mummy.png

One gaunt ancient reanimated human mummy, sunken dark eye sockets and weathered desiccated face, complete body wrapped in tattered dusty ochre linen, strips hanging close to arms and legs, hunched forward with both bony hands reaching. No armor, weapon, crown or burial props, distinct from armored Honored Dead.

### NightGaunt.png

One slender faceless winged humanoid nightmare, completely smooth featureless black head with two short swept-back horns, sleek charcoal leathery skin, two long arms ending in hooked fingers, two digitigrade clawed legs, long curling barbed tail, exactly two broad bat wings raised above. Entire wing tips and tail visible, no eyes or mouth.

### Riot.png

A compact tight group of FIVE furious adult 1920s townspeople, worn gray and brown work coats, caps, waistcoats and long skirt. One swings a wooden baton, one grips a broken chair leg, others raise fists; dynamic turbulent overlapping triangular crowd with clearly distinct heads and fully visible feet. No signs, flags, fire, text, scenery or gore.

### SerpentPeople.png

One intelligent serpent humanoid occultist, a single cobra-like head with modest hood, amber eyes and forked tongue, humanoid scaled torso with two clawed arms, a long thick snake lower body coiled into a compact base instead of legs. Weathered olive scales, ochre belly plates, ragged brown ritual shawl and modest aged bronze ornaments. One hand poised in a spell gesture, no deity crown or wings.

### ServitorOfTheOuterGods.png

One amorphous cosmic flutist, a lumpy swollen gray-olive fleshy mass held upright on several short twisting rootlike tentacles, with multiple long boneless arms holding a primitive dark bone flute to an irregular lipless mouth. Sparse sensory pits, leathery folds, entirely nonhumanoid anatomy. Compact complete figure, no music notes.

### Shoggoth.png

One enormous spreading mound of black-olive protoplasm, many irregular amber eyes scattered across viscous folds, several gaping tooth-filled mouths at different heights, thick curling pseudopods and lobes extending around a broad base. More massive and complex than Proto Shoggoth. Dark wet tactile ridges with restrained warm highlights, no humanoid form.

### Skeleton.png

One complete animated human skeleton, aged ivory bones with ochre grime, empty dark eye sockets, bared teeth, ragged small strips of faded brown burial cloth around waist and shoulders. Forward stalking crouch with two bony hands grasping, both feet fully visible. Anatomically coherent, no armor, weapon, grave or pedestal.

### StarSpawn.png

One hulking winged cosmic cephalopod humanoid, massive muscular olive-gray torso, octopus-like head with a hanging beard of thick curled tentacles, two clawed arms, two powerful taloned legs, and two weathered batlike wings folded upward behind shoulders. Compact forward crouch; lesser monster, no crown or deity regalia, entire wings contained.

### StarVampire.png

One floating blood-fed cosmic predator, a compact shapeless translucent reddish-umber saclike body with clusters of short feeding tendrils and several long curling tentacles, faint veins visible inside leathery translucent flesh, a central circular tooth-lined sucker-mouth. Not humanoid or batlike. Restrained dark crimson accents, no blood splashes or surrounding glow.

### Vampire.png

One sinister pale adult male vampire, gaunt aristocratic face with subtle bared fangs, swept-back dark hair, weathered 1920s dark charcoal suit and waistcoat beneath a tattered long brown-black cloak. Two long clawed hands reaching, hunched predatory full-body stance, worn shoes visible. No bats, blood, castle or bright red cape.

### Warlock.png

One elderly male occult sorcerer, gaunt face, receding silver hair and long straggly beard, layered worn brown and charcoal robes, a faded olive sash with small bronze charms, one hand raised with crooked fingers and the other clutching an aged closed leather grimoire. Bare head, no hood or staff; tiny restrained amber magical ember near fingertips, full body.

### Werewolf.png

One powerful wolf-human predator, gray-brown matted fur, long snarling wolf muzzle with ivory fangs, pointed ears, broad hunched shoulders, two massive clawed forearms, two digitigrade lupine legs, short ragged scraps of trousers. Dynamic low stalking pose, full body including feet and modest bushy tail, no blood.

### Witch.png

One eerie elderly human witch, deeply weathered face and wispy gray hair, stooped body in tattered layered dark olive and brown skirts, ragged shawl and loose cloth head covering, holding a gnarled walking staff in one hand and a small knotted charm in the other. Menacing restrained realism, no pointed fantasy hat, broom, cauldron or scenery.

### Wraith.png

One menacing tall hooded spectral entity, hollow black face with two faint pale eye pinpoints, long reaching skeletal hands, torn charcoal burial robes flowing into several curling gray-green spectral ribbons instead of legs. Darker and more threatening than Ghost; sharply readable silhouette, true transparency between ribbons, no fog rectangle or weapon.

### Zombie.png

One reanimated adult human corpse in torn dusty 1920s shirt, waistcoat, trousers and worn boots, pallid gray-green decomposed skin, vacant milky eyes, slack jaw, thin disheveled hair. Lurching forward full-body stance with uneven reaching arms, desiccated restrained horror without exposed organs, blood or gore.
