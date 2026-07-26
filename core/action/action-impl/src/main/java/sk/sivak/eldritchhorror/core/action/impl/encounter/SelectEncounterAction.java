package sk.sivak.eldritchhorror.core.action.impl.encounter;

import java8.features.function.Predicate;
import java8.features.stream.Stream;
import rx.Single;
import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.action.Action;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.encounter.EncounterButtonData;
import sk.sivak.eldritchhorror.core.constants.encounter.EncounterType;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.AvailableEncounters;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.ConditionEncounter;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.Encounter;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class SelectEncounterAction implements Action<AvailableEncounters, Encounter> {


    private AvailableEncounters input;
    private SingleSubscriber<? super Encounter> sub;

    @Override
    public Single<Encounter> execute() {

        return Single.create(x -> {
            this.sub = x;

            int disabledCount = Stream.count(input.getEncounters(), encounter -> !encounter.getEncounterButtonData().isEnabled());
            if (disabledCount == input.getEncounters().size()) {
                // all monsters were encountered, some were not defeated
                sub.onSuccess(null);
                return;
            }
            Collection<EncounterButtonData> encounterButtonDataList = Stream.map(input.getEncounters(), Encounter::getEncounterButtonData);
            ServicePlatform.get().getGameController().selectEncounter(encounterButtonDataList)
                    .subscribe(this::onSelectEncounter);
        });

    }

    private void onSelectEncounter(String uuid) {
        Predicate<Encounter> predicate = encounter -> uuid.equals(encounter.getEncounterButtonData().getUuid());
        Encounter selectedEncounter = Stream.findFirstOrException(input.getEncounters(), predicate);
        sub.onSuccess(selectedEncounter);
    }

    @Override
    public void setInput(AvailableEncounters input) {
        this.input = input;
    }
}
