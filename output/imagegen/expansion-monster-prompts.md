# Expansion monster artwork

Generated with the built-in image_gen tool. Each new placeholder receives a separate transparent full-body sprite, installed under its existing game filename.

Final game assets are 512 x 512 RGBA PNGs in `assets/monster/`. Generated images are proportionally downsampled into a 492 x 492 area centered on the transparent canvas, retaining the generated alpha and adding a small safety margin. The original generation files remain in the image tool's output directory. `expansion-monsters-preview.png` is a labeled overview of the final game exports.

Validation: all 26 exports match the newly added monster filenames and pass size, RGBA, transparent-border, and nonempty-artwork checks. The existing `:desktop:previewMonsterSheet` task passed for all 72 bundled monsters, including label bounds, health tokens, rule reattachment, scrolling, and reuse. The complete export contact sheet and representative production card screenshots were visually inspected.

## Shared prompt

Use case: stylized-concept. Asset type: Ancient Terror dark cosmic-horror board-game monster sprite. Create a NEW original illustration. Style: detailed realistic painterly fantasy bestiary cutout, tactile weathered skin, fur and fabric, muted charcoal, dusty ochre and desaturated olive colors, warm directional highlights and cool deep shadows. Clear readable silhouette at small token size, menacing restrained horror. Composition: square 1024x1024, centered FULL BODY three-quarter view, occupy about 82 percent of canvas with at least 7 percent transparent margin on EVERY edge. Every horn, finger, wing and tail must fit entirely inside the image. Background: actual transparent alpha including negative spaces between limbs. No environment, floor, cast shadow, scenery, base, badge, circular token, frame, text, letters, logo, watermark or checkerboard. Subject: 

## Per-monster subject prompts

### ChildOfTheGoat.png

One adult-sized grotesque goat-like cosmic abomination: a hunched shaggy charcoal body, cloven hind hooves, long clawed forelimbs, a narrow goat skull-like face with curled horns, and a few sinewy tendrils emerging from its shoulders. Diseased bark-like hide and matted fur, dark ochre eyes. Distinct from a normal goat, no human child.

### CrawlingOne.png

One humanoid silhouette composed entirely of a dense crawling swarm of pale grubs and dark earthworms, wearing a tattered brown hooded overcoat. The hood frames a hollow suggestion of a face made of worms; two worm-composed hands reach forward, the base separates into two legs and scattered attached writhing strands. No gore.

### CrazedMob.png

A compact group of FIVE distinct frenzied adult townspeople in worn 1920s shirts, waistcoats, skirts and coats, arranged in a tight readable triangular crowd, faces twisted with supernatural rage. One raised wooden club and one pitchfork, other empty clenched hands. Muted brown and gray clothing. Entire group isolated, all feet visible.

### DeepOneHybrid.png

One unsettling adult male coastal fisherman midway through transformation into an amphibious humanoid. Bulging yellow eyes, flattened fishlike face, gill slits, patchy slate-green scales, webbed clawed hands, hunched human proportions. Worn 1920s fisherman's coat, trousers and boots, no hat; full body stalking pose.

### Dhole.png

One colossal blind burrowing worm presented as a compact full-body coiled creature. Thick leathery ochre-gray annulated body, blunt head raised with a circular tooth-lined maw surrounded by fleshy sensory tendrils, tapering tail coiling into the base. Immense weight, cracked earth-like hide, no human face or limbs.

### DimensionalShambler.png

One gangly ape-like extradimensional predator with a low narrow head, recessed dull eyes, leathery gray hide, hunched broad shoulders, extremely long muscular forearms ending in hooked claws, short bowed hind legs. Loose folds of skin, strange asymmetrical facial ridges, coherent two-arm two-leg anatomy.

### FireVampire.png

One living predatory knot of crimson and amber fire, an elongated spiraling core forming a sinister half-suggested face and curling flame tendrils. Entity itself made of textured fire and dark ember filaments; strong compact readable silhouette, restrained red-orange light, isolated alpha around individual flames, no smoke cloud or background glow.

### Firebug.png

One sinister adult human arsonist in a ragged 1920s brown long coat and worn trousers, face pale and feverish, clutching a battered metal fuel can in one hand and a burning rag torch in the other. Crouched prowling stance, grime and soot, modest orange flame as the only bright accent.

### FlyingPolyp.png

One floating alien polyp: irregular tapering tubular fleshy body with pocked leathery gray-green skin, several uneven fleshy lobes and long curling tentacles, asymmetrical slit-like sensory pits. Wingless, no humanoid face or skeleton, partly twisted vertical floating pose, compact readable silhouette.

### FormlessSpawn.png

One amorphous glossy black tar-like creature rearing up from a low spreading mass. Its central neck thrusts forward into a gaping irregular mouth; several uneven pseudopods form hooked temporary limbs and curling appendages. Wet charcoal folds and restrained olive highlights, no eyes, no humanoid anatomy.

### Ghast.png

One lean subterranean humanoid beast, hairless pale ochre hide, broad flattened noseless face with tiny dark eyes and long teeth, hunched muscular torso, long clawed arms, disproportionately powerful backward-bent hopping hind legs. Animalistic crouch, leathery skin, no clothes.

### GiantPenguin.png

One enormous blind albino cave penguin, full body three-quarter view, tall heavy pear-shaped torso, dingy ivory and ash-gray feathers, vestigial milky eyes, long weathered pale beak, slightly outstretched flippers and broad scaly feet. Unsettling ancient animal, realistic anatomy, no cute or cartoon expression.

### HonoredDead.png

One ancient skeletal guardian mummy, gaunt exposed skull-like face, weathered linen wrappings and remnants of tarnished bronze Egyptian burial armor, faded ceremonial collar, carrying a short ancient curved sword at its side. Upright dignified threatening stance, entire body and weapon visible, no pedestal.

### HuntingByakhee.png

One lean aerial predator combining bat, carrion bird and insect features: narrow snarling head, long hooked beaklike jaws with teeth, shaggy sinewy torso, two ragged leathery bat wings, two grasping clawed forelimbs and two taloned hind legs. Dark umber and olive weathered hide, compact descending hunting pose, all wing tips visible.

### HuntingHorror.png

One huge black serpentine predator with a single elongated reptilian head, needle teeth, an extremely long coiled tapering body and exactly two broad ragged membranous bat wings attached behind the neck. No arms or legs. Charcoal and dark olive scales, dynamic upright S-shaped flight pose, all wings and tail contained.

### Jiangshi.png

One reanimated adult corpse wearing a weathered dark indigo Qing-era official robe and round black cap, pale gray-green rigid face, small paper talisman attached to forehead with indistinct nonlegible ink marks. Both arms outstretched forward, clawlike fingers, both feet visible in stiff hopping pose. Dignified eerie horror, no caricature.

### MoonBeast.png

One bulky hairless pale gray toad-like alien, blind and eyeless with a cluster of short pinkish sensory tentacles replacing its face. Broad squat torso, long powerful clawed arms and bent short legs, mottled fleshy folds. Crouched three-quarter pose, no clothes, weapons or scenery.

### Poltergeist.png

One violent spectral entity, a ragged translucent gray-green humanoid apparition with a hollow screaming face, elongated reaching hands and lower body dissolving into curling spectral ribbons. A few small broken wooden splinters orbit close to its silhouette. Subtle translucent body with true alpha outside, no opaque fog rectangle.

### ProtoShoggoth.png

One squat primordial mound of black-green protoplasm, smaller and less massive than a shoggoth, studded with a sparse scattering of amber eyes and two irregular toothy mouths, thick blunt pseudopods pulling it forward. Viscous ridged surface and organic folds, compact asymmetric silhouette, no humanoid form.

### RatThing.png

One large hunched rat-like nightmare with matted brown-gray fur, a disturbing subtly humanlike adult face blending into a rodent muzzle, small sharp teeth, long delicate clawed forepaws, strong haunches and one long naked curling tail. Menacing feral crouch, no clothes, whole tail visible.

### SandDweller.png

One lean desert-dwelling alien humanoid with rough sand-colored leathery skin, bulbous bald head, disproportionately large dark eyes, narrow mouth, long thin clawed arms and bent muscular legs. Angular bony ridges and coarse grainy hide, hunched stalking posture, no clothing or props.

### Shan.png

One sinister alien insect approximately scarab-shaped: dark iridescent charcoal chitin, ridged bulbous carapace, small many-faceted eyes, six thin jointed limbs with hooked ends, delicate folded veined wings partly lifted, short writhing sensory proboscis. Three-quarter frontal view readable as a complete isolated creature, restrained antique bronze-green highlights.

### SpawnOfSebak.png

One massive crocodilian humanoid abomination, long crocodile jaws full of uneven ivory teeth, heavy olive armored scales, thick powerful torso, two muscular clawed arms, two squat reptilian legs, a single long heavy tail curling beside the feet. Ancient menacing predatory stance, no armor or clothing.

### TchoTcho.png

One fictional adult occult humanoid, small wiry build, eerie ashen gray skin with subtle nonhuman texture, bald head, sharp angular inhuman features and luminous pale eyes. Ragged dark earth-brown ritual tunic and wraps, clutching a small chipped stone dagger low at its side. No resemblance to any real ethnic group, no ethnic caricature. Cunning low crouch.

### YoungCthonian.png

One young burrowing chthonian: thick dark gray annulated worm body coiled into a compact low base, bulbous front end lifted with a cluster of six long writhing octopus-like feeding tentacles concealing the mouth. No eyes, legs or wings. Rough leathery hide, earthy highlights, every tentacle and tapering tail contained.

### Zoog.png

One small wiry woodland predator, brown-gray fur, rodent-like body, large alert dark eyes, short facial tentacles around a small sharp-toothed mouth, grasping clawed forepaws and hind feet, a long thin curling prehensile tail. Crouched sly pose, unsettling realistic animal rather than cute mascot. Entire tail visible.

## Crawling One full prompt override

Use case: stylized-concept. Create a NEW Ancient Terror cosmic-horror board-game monster sprite of a CRAWLING ONE: one full-body humanoid silhouette composed entirely of dense writhing pale grubs and dark earthworms, wearing a tattered brown hooded overcoat. Hood frames a hollow suggestion of a face made of worms; two worm-composed hands reach forward; base separates into two legs and close attached strands. Detailed realistic painterly fantasy bestiary cutout, tactile weathered cloth and organic textures, muted charcoal and dusty ochre, warm directional highlights, deep cool shadows, crisp silhouette readable at small token size, no gore. Square 1024x1024 transparent RGBA PNG, complete body centered at 82 percent canvas size with at least 7 percent clear margin on EVERY edge. True alpha in all empty space. No environment, floor, shadow, scenery, badge, token border, text, watermark or checkerboard.
