package sk.sivak.eldritchhorror.core.action.impl.encounter;

import java8.features.function.Predicate;
import java8.features.stream.Stream;
import java8.features.util.IterableUtils;
import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.action.AbstractHookableAction;
import sk.sivak.eldritchhorror.core.constants.encounter.EncounterType;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.AvailableEncounters;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.Encounter;

public class DisableNonCombatEncountersAction extends AbstractHookableAction<AvailableEncounters, AvailableEncounters>{

    public DisableNonCombatEncountersAction() {
        super(BeforeAfterEvent.DISABLE_NON_COMBAT_ENCOUNTERS);
    }

    @Override
    protected void onExecute(SingleSubscriber<? super AvailableEncounters> ss) {
        boolean hasCombatEncounter = Stream.anyMatch(input.getEncounters(), hasCombatEncounter());

        if (hasCombatEncounter) {
            disableNonCombatEncounters();
        }
        ss.onSuccess(input);
    }

    private void disableNonCombatEncounters() {
        IterableUtils.forEach(input.getEncounters(), encounter -> {
            if (encounter.getEncounterType() != EncounterType.COMBAT) {
                encounter.getEncounterButtonData().disable("Defeat Monsters first");
            }
        });
    }

    private Predicate<Encounter> hasCombatEncounter() {
        return encounter -> encounter.getEncounterType() == EncounterType.COMBAT;
    }
}
