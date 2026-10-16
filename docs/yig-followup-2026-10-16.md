# Yig follow-up fixes — 16 October 2026

Children of Yig now chooses its escape destination from every location except
its current one. Movement still waits until the combat table closes.

Yig's awakening shows only "Yig awakens!" (localized in Slovak) with an OK button
before changing to the awakened rules and opening the Ancient One sheet. Both
midnight and solving the third Mystery use the same announcement. Solving three Mysteries while Yig
is asleep now awakens him and draws Serpent's Nest instead of declaring victory.
Both awakening paths reset Doom to −8, displayed as 23:20. Repeating the awakening
action on an already awakened Yig preserves the current countdown.

The transition removes the sleeping listeners before registering awakened rules,
preventing duplicate Reckoning effects after early awakening. All four awakened
Ancient One sheets now show an explicit localized Awakened status. The older
three awakened sheets also no longer reference a missing banner asset, and their
art and flavor text fit without overlapping.

## Validation

391 tests pass with no failures or skipped tests across action, event listener,
service, controller, model, and view modules. Regression coverage includes escape
destinations from every map location, waiting for acknowledgement, early clock
resets, repeated awakening, third-Mystery routing, and listener replacement.

The desktop preview passes for both English and Slovak confirmations and all four
awakened sheets. It checks text bounds, separation from the OK button, and clock
hands at 23:20 after resetting from Doom 0, 1, 5, 8, and 10 at normal and
fast-forward speed. The resulting screens were visually inspected.

```powershell
.\gradlew.bat -I tools/report-fixes-preview/preview.gradle :eldritch-horror-core:action:action-impl:test :eldritch-horror-core:event-listener:event-listener-impl:test :eldritch-horror-core:service:service-impl:test :eldritch-horror-core:controller:controller-impl:test :eldritch-horror-core:model:model-impl:test :eldritch-horror-core:view:view-impl:test :desktop:previewReportFixes --args="--awakening" --console=plain
```

Screenshots are in `build/report-fixes-preview/awakening-*` and `awakened-*`.
No Android device test or release build was performed.

## Artwork follow-up

Yig now has a wiki-inspired awakening illustration, an illustrated defeat dialog,
a redesigned Cultist sprite, and a matching epic monster sprite. The generic
Cultist remains the fallback for other Ancient Ones; adding a PNG named after an
Ancient One under `assets/monster/cultist/` enables its own variant across cards,
selection, combat and asynchronous map spawning. Save formats and monster rules
are unchanged by the artwork selection.

Final validation: 395 tests pass across the six modules above. The production
monster preview verifies artwork switching and renders all monster surfaces.
Ending previews pass for Yig and Azathoth in English and Slovak. The full suite
also exposed an intermittent crash-report timestamp serialization issue: JSON
prototype omission could drop `capturedAt`. Crash report persistence now writes
all values, preserving the original timestamp when retrying after a restart.
