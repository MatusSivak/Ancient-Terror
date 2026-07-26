package sk.sivak.eldritchhorror.core.action.impl.encounter;

import java8.features.function.Predicate;
import java8.features.stream.Stream;
import java8.features.util.IterableUtils;
import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.action.AbstractHookableAction;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.encounter.EncounterType;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.AvailableEncounters;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.CombatEncounter;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.Encounter;
import sk.sivak.eldritchhorror.core.model.PerformedEncountersRead;
import sk.sivak.eldritchhorror.core.model.PerformedEncountersWrite;

public class DisablePerformedEncountersAction extends AbstractHookableAction<AvailableEncounters, AvailableEncounters> {

    public DisablePerformedEncountersAction() {
        super(BeforeAfterEvent.DISABLE_PERFORMED_ENCOUNTERS);
    }

    @Override
    protected void onExecute(SingleSubscriber<? super AvailableEncounters> ss) {
        PerformedEncountersWrite performedEncounters = ServicePlatform.get().getPerformedEncounters();
        InvestigatorId activeInvestigatorId = ServicePlatform.get().getInvestigators().getActiveInvestigatorId();
        for (Encounter encounter : input.getEncounters()) {
            boolean performed = performedEncounters.isPerformed(activeInvestigatorId, encounter.getEncounterType(), encounter.getUuid());
            if (performed) {
                encounter.getEncounterButtonData().disable("Monster already encountered");
            }
        }

        ss.onSuccess(input);
    }
}
