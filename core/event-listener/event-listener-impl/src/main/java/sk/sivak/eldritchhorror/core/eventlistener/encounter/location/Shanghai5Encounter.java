package sk.sivak.eldritchhorror.core.eventlistener.encounter.location;

import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.SpendCluePassFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.LocationEncounter;

public class Shanghai5Encounter extends AbstractLocationEncounter{

    public Shanghai5Encounter() {
        super(5, LocationEncounter.LocationEncounterType.SHANGHAI);
    }

    @Override
    protected void execute() {
        new SpendCluePassFlavorInfoTemplate(getTextBuilder(), () -> ServicePlatform.get().getInvestigatorService().improveSkill(Stat.LORE))
                .withOnNoAction(() -> finishAfterFlavor(getTextBuilder().withFail().withFlavor().build()))
                .withOnMissingClue(() -> finishAfterFlavor(getTextBuilder().withFail().withFlavor(2).build())).execute();
    }
}
