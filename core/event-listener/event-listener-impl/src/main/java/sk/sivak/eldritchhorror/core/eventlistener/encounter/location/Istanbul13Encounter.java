package sk.sivak.eldritchhorror.core.eventlistener.encounter.location;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.SpendCluePassFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.LocationEncounter;

public class Istanbul13Encounter extends AbstractLocationEncounter{

    public Istanbul13Encounter() {
        super(13, LocationEncounter.LocationEncounterType.ISTANBUL);
    }

    @Override
    protected void execute() {
        new SpendCluePassFlavorInfoTemplate(getTextBuilder(), () -> {
            ServicePlatform.get().getService().convertTo(Stat.class, () -> null);
            ServicePlatform.get().getInvestigatorService().improveSkill(null);
        }).withOnNoAction(() -> finishAfterFlavor(getTextBuilder().withFail().withFlavor().build()))
                .withOnMissingClue(() -> finishAfterFlavor(getTextBuilder().withFail().withFlavor(2).build())).execute();
    }

}
