# Fast-forward reroll audit

Holding the fast-forward button or F accepts YES on explicitly marked free reroll prompts once their button becomes clickable. Starting to hold while such a prompt is waiting also accepts it. Releasing before it becomes clickable leaves it waiting. The live held state is used, not the cached animation-speed flag. Manual and automatic answers share a single decision guard.

The audit covered the current repository's card definitions, reroll listeners, spell backs, investigator reroll abilities, encounter rerolls, count modifiers, and post-reroll hooks. This describes implemented game behavior, not a separate tabletop rules review. New prompts default to manual and require an explicit opt-in after their affirmative handler is checked.

## Eligible free card prompts

These existing prompts auto-accept only when the risk checks below pass. Their normal availability checks still apply.

| Card | Eligible reroll |
| --- | --- |
| Personal Assistant | Influence |
| Agent of Secrets | Lore or Observation |
| Arcane Scholar | Lore |
| Private Investigator | Observation |
| Hired Muscle | Strength |
| Vatican Missionary | Will |
| Lucky Ring | Any test |
| Bull Whip, Fishing Net, Syndicate Agent | Combat Strength |
| Elder Sign, King James Bible, Profane Tome | Combat Will |
| Medical Journal | Strength tests for Injury/Illness conditions |
| Museum Curator | Expedition or Mystic Ruins |
| The Silver Key | Its free Other World reroll only |
| Wither, back 2 | Granted Strength-test reroll |
| Intervene, back 2 | Granted rerolls after the earlier compulsory Sanity loss has resolved; accepting the reroll itself has no additional cost |

This is 15 asset prompts, one artifact prompt, and two spell-back prompt sites. The Ex-Convict's separate free reroll of ones is also eligible. Its existing repeat-on-another-1 behavior remains, with each prompt going through the usual UI/animation path.

## Prompts that remain manual

| Card or ability | Reason |
| --- | --- |
| Axe | Spends 2 Sanity |
| Alien Device, Dragon Idol | Spend 1 Sanity |
| Hyperborean Crystal | Discards a Spell |
| Tear Gas | Discards itself |
| Lucky Rabbit's Foot, Gambler's Dice, Map of the Ley Lines | Disable/exhaust the card |
| The Silver Key's Clue replacement | Disables the Key; distinct from its free Other World reroll |
| Tome of Secrets | Spends Focus and disables the Tome |
| Clue/Focus rerolls, including Spy, Handyman, Mysterious Tome, Expedition Guide modifiers | Still spend a token |
| Astronomer's replacement | Spends Sanity and disables the passive ability |
| Yig research rerolls | Spend Health or Sanity |
| Other questions, die-count choices, casting choices and recovery prompts | No free-reroll opt-in |

The Yog-Sothoth resource text also mentions discarding a Spell for a reroll; it is not marked for automatic acceptance. Tick-Tock Men's Clue restriction remains in the existing paid-Clue path. The current count modifiers apply to Clue/Focus, not to the OTHER type used by free card rerolls.

## Risky free rerolls remain manual

The user explicitly chose to keep risky free rerolls manual.

- **Blunderbuss:** a new 1 can cancel an existing success. When its bonus is selected for Combat Strength, rerolling a value other than 1 stays manual. Rerolling existing ones remains eligible because it cannot add more ones.
- **Available +1 effects:** a failed die may become a guaranteed success later. Preserve that choice when applicable, non-disabled .18 Derringer, Gruesome Talisman, Lucky Cigarette Case, or Expedition Map bonuses could convert one of the dice about to be rerolled. Stacked bonuses and the actual Blessed/Cursed success threshold are included. This is conservative and may leave a prompt manual even if the player intends not to use the bonus.
- **Existing successes:** use the same numeric die ordering as the reroll action, and require every selected die to have BAD score. Do not infer failed dice from total score; double successes and score bonuses are not die counts.

No prompt text or card-wide allowlist determines safety. For example, The Silver Key's paid/exhausting option, Medical Journal's healing question, and King James Bible's recovery question are not covered by their free reroll opt-in. RETURN animation alone is insufficient: Axe and other Sanity payments also use it.

## Validation

Regression tests cover all 15 free assets, the Silver Key's separate options, both spell-back sites, the Ex-Convict, paid/exhausting/discarding examples, Blunderbuss, all four +1 cards, stacked modifiers, changed success thresholds, numeric selection, completed tests, click/automatic-answer races, and live fast-forward timing. Verification passed: 180 event-listener tests and 354 view tests (534 total), with zero failures or skips.

Existing reroll availability and selection rules are otherwise preserved. In particular, some pre-existing listeners compare total score with dice-pool size when deciding whether to offer a reroll; this change does not rewrite those rules.