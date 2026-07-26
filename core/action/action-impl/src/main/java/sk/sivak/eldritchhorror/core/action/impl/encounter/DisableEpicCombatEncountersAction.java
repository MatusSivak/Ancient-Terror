package sk.sivak.eldritchhorror.core.action.impl.encounter;

import java8.features.function.Predicate;
import java8.features.stream.Stream;
import java8.features.util.IterableUtils;
import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.action.AbstractHookableAction;
import sk.sivak.eldritchhorror.core.constants.encounter.EncounterType;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.AvailableEncounters;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.CombatEncounter;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.Encounter;

public class DisableEpicCombatEncountersAction extends AbstractHookableAction<AvailableEncounters, AvailableEncounters> {

    public DisableEpicCombatEncountersAction() {
        super(BeforeAfterEvent.DISABLE_EPIC_COMBAT_ENCOUNTERS);
    }

    @Override
    protected void onExecute(SingleSubscriber<? super AvailableEncounters> ss) {
        boolean hasNonEpicActiveMonsterCombatEncounter = Stream.anyMatch(input.getEncounters(), hasNonEpicActiveMonsterCombatEncounter());

        if (hasNonEpicActiveMonsterCombatEncounter) {
            disableEpicCombatEncounters();
        }
        ss.onSuccess(input);
    }

    private void disableEpicCombatEncounters() {
        IterableUtils.forEach(input.getEncounters(), encounter -> {
            if (encounter.getEncounterType() == EncounterType.COMBAT && ((CombatEncounter) encounter).getMonsterInfo().isEpic()) {
                encounter.getEncounterButtonData().disable("Encounter Non-Epic Monsters first");
            }
        });
    }


    private Predicate<Encounter> hasNonEpicActiveMonsterCombatEncounter() {
        return encounter ->
                encounter.getEncounterType() == EncounterType.COMBAT &&
                        !((CombatEncounter) encounter).getMonsterInfo().isEpic() &&
                        encounter.getEncounterButtonData().isEnabled();
    }
}
