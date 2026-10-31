# Evening bug and crash review — 30 October 2026

Fetched both live report collections: 26 reports, nine marked new. Five reports
arrived after the previous review. Original reports, saves, screenshots and logs
are retained in ignored `build/report-review-oct30-followup/`. Remote report
statuses were not changed.

## Explorer starting-skill crash

Crash `e3cc8040-25ba-47e2-a15e-3efd91cd938b` occurs after acknowledging
"The Explorer starts with one skill improved." The log passes the resulting
`Answer` directly into `BEFORE_IMPROVE_SKILL`, which requires `Stat`.

`InvestigatorService.improveSkill(null)` means the player may choose a skill.
The shared hookable-command builder instead treats null initialization as
inheriting the previous command output. Most callers explicitly cleared that
output; Explorer's new acknowledgement flow did not.

The investigator service now clears the input before a skill-choice improvement,
grouping the conversion and improvement commands together. This also prevents a
previous `Stat` from silently choosing the next improvement. Explicit skill
requests and shared command-builder behavior remain intact. The service
interface documents what null means.

Four regression tests use the real service, command queue, event validation and
improvement action. They cover the Explorer acknowledgement through asynchronous
skill selection, restoration of the previous investigator, one-time setup and
reload registration, stale answers, stale skills, and explicit skill requests.
Before the fix, three tests failed: Explorer and stale answers reproduced the
reported class mismatch, and a stale skill incorrectly bypassed player choice.
All four pass with the fix.

## Defensive spell button order

Report `a767cf53-c55f-4823-954f-f04b00fcd0c3` requests swapping the choices for
Instill Bravery and Flesh Ward. Both now use the standard No/Yes ordering.
Existing tests verify decline, acceptance, self-protection, damage prevention,
caster focus and target restoration with the updated order.

## Other reports

- Android crash `8a221d29-de16-402e-a563-f2c412312334` has the same stale
  preplayed-health rollback signature as the earlier Desktop Flesh Ward crash
  `2d9ee869-eaa1-41ec-9791-b5637c0d0704`. The existing `af09d40` ownership,
  contents-generation and capacity protections address this path; `07f82cc`
  already added the nested-confirmation regression preview.
- The Brain Case crash and the two Yig research reports were addressed in
  `4bd99eb`; see the earlier October 30 reviews.
- `7e6b40f1-ee63-4203-9945-f34bd8cbba66` requests a detention overlay and
  `8d7dd366-982e-4f64-b2fd-e9961fa90e19` requests a monster-discard animation.
  These feature requests remain open and are outside this bug/crash fix pass.

## Validation

- Full JVM regression suite: 586 tests, zero failures, errors or skipped tests.
- Desktop compilation passed.
- Android packaging and device testing were not performed locally.
- The pre-existing `assets/save.json` edit is excluded from the commit.
