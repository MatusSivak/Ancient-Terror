package sk.sivak.eldritchhorror.core.action.impl.encounter;

import rx.Single;
import sk.sivak.eldritchhorror.core.action.Action;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.encounter.EncounterButtonData;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.AvailableEncounters;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.Encounter;

public class BuildEncounterButtonsAction implements Action<AvailableEncounters, AvailableEncounters> {


    private AvailableEncounters input;

    @Override
    public Single<AvailableEncounters> execute() {

        return Single.create(sub -> {

            for (Encounter encounter : input.getEncounters()) {
                EncounterButtonData encounterButtonData = encounter.buildButtonData();
                encounter.setEncounterButtonData(encounterButtonData);
            }
            sub.onSuccess(input);
        });

    }

    @Override
    public void setInput(AvailableEncounters input) {
        this.input = input;
    }
}
