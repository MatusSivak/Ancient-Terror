# Temporary investigator unlock

Desktop launches and Android debug builds temporarily expose all 24 investigators,
including the eight premium investigators, for local playtesting. Rebuild and
restart the game to use it. Android release builds keep the normal purchase checks.

The override is process-only: it does not write purchase preferences, grant other
products, or contact the store to unlock investigators. Initial selection and
replacement investigator selection use the same availability checks.

To restore normal local behavior, remove the two
`LocalTesting.setAllInvestigatorsUnlocked(...)` calls in `GameDesktop` and
`GameActivity`, or set their arguments to `false`, then rebuild and restart.
Actual purchases remain available after the override is disabled.
