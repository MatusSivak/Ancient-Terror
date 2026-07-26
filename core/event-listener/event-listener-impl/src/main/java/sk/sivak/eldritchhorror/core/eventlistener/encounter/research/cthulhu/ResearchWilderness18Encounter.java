package sk.sivak.eldritchhorror.core.eventlistener.encounter.research.cthulhu;

import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.constants.monster.NonEpicMonsterId;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.AmbushTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;

public class ResearchWilderness18Encounter extends AbstractCthulhuResearchEncounter {

    public ResearchWilderness18Encounter() {
        super(18, LocationType.WILDERNESS);
    }

    @Override
    protected void execute() {
       new AmbushTemplate(getTextBuilder(), NonEpicMonsterId.DEEP_ONE, () -> {
           ServicePlatform.get().getGameService().gainSpell();
       },null)
               .execute();
    }
}
