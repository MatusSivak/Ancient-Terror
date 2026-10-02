# Yig implementation

Yig is selectable alongside the existing Ancient Ones, using the existing full-game unlock. His starting Doom is 10. The implementation includes six mysteries, the Serpent's Nest final battle, 24 research cards (72 city/wilderness/sea encounters), eight K'n-yan special encounters, three epic monsters, and sleeping/awakened rules.

Rules were checked against the [Yig reference page](https://eldritchhorror.fandom.com/wiki/Yig), including its expanded encounter tables. The [Under the Pyramids rules](https://cdn.1j1ju.com/medias/19/bd/71-eldritch-horror-under-the-pyramids-rulebook.pdf) clarify advancing mysteries: Crown banks a Clue toward its payment, and Rise gains progress from the pool without removing a board token. Encounter narration and epilogues are original short adaptations; rule instructions are paraphrased. Artwork is newly generated, not copied from card scans.

Save/load preserves Yig's remaining Eldritch tokens, mystery progress, remaining board tokens, Crown's stored Clues, and the K'n-yan draw order. Restoring listeners does not repeat setup or heal wounded monsters. Research text caches are separated by Ancient One.

## Validation

Run from the repository root with a configured JDK and Gradle cache:

```powershell
.\gradlew.bat :eldritch-horror-core:event-listener:event-listener-impl:test :eldritch-horror-core:model:model-impl:test :eldritch-horror-core:view:view-impl:test :desktop:build --console=plain
.\gradlew.bat -I tools/yig-preview/preview.gradle :desktop:previewYig --offline --console=plain
```

Both commands passed. The 19 Yig-specific tests cover setup and scaling for 1–8 investigators, encounter availability and text caching, save/load, mystery advancement, poisoning after prevention, awakened ambush toughness, epic-monster wounds and effects, reckoning order, deferred awakening, final-token defeat, and final-battle victory. Existing tests in the affected modules also pass.

The preview uses the production widgets and writes screenshots to `build/yig-preview/` without opening a saved game. The selector, both Yig sheets, counter, and three epic monster cards were rendered and visually inspected. This is not a full interactive campaign playthrough.

Android build verification is unavailable on this machine: `local.properties` points to the missing `C:\WORK\android_sdk` directory. Desktop build verification passed. The pre-existing `assets/save.json` modification is unrelated and was left untouched.

## Artwork provenance

Created with the built-in image generation tool. Final project assets:

- `assets/ancient_one/YIG.png`: Yig illustration; also used as `assets/monster/epic/Yig.png`.
- `assets/ancient_one/button_yig.jpg`: JPEG conversion of the same illustration for the selector.
- `assets/monster/epic/ChildrenOfYig.png`: original serpent colony illustration.
- `assets/monster/epic/WingedSerpent.png`: original winged serpent illustration.

Generation prompts:

**Yig:** Use case: stylized-concept. Asset type: original dark cosmic horror game illustration, Yig the Father of Serpents. A towering ancient serpent deity, humanoid arms and immense coiled snake body, weathered jade and bronze scales, luminous amber eyes, framed by jungle temple ruins and mist in Central America. Ominous painterly realism, rich muted greens, aged gold, black shadows; detailed silhouette readable at small size. Landscape 3:2, subject centered with room around head for square portrait crops. No lettering, text, logos, border, or watermark. Full illustrated background.

**Children of Yig:** Use case: stylized-concept. Original dark cosmic horror board game monster illustration: Children of Yig, a writhing colony of large venomous snakes erupting from a ruined jade altar in a moonlit jungle. Distinct interwoven serpents, vivid amber eyes, worn green and bronze scales, dramatic dark emerald mist and painterly realism. Composition square, clear central creature silhouette readable as a small game token, fully illustrated background, no lettering, no logo, no border, no watermark.

**Winged Serpent:** Use case: stylized-concept. Original dark cosmic horror board game monster illustration: the Winged Serpent, an immense sinuous jade-green snake with sweeping leathery feather-edged wings, descending through storm clouds over an old Japanese harbor at night. Amber eyes, bronze scale highlights, dangerous hooked fangs, long coiled body, atmospheric painterly realism, muted emerald and gold palette, clear readable central silhouette. Square artwork with fully illustrated background. No text, letters, logos, border or watermark.
