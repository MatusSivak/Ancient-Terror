package sk.sivak.eldritchhorror.core.eventlistener.encounter.research.cthulhu;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.InfoFlavorTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoTemplate;

public class ResearchWilderness15Encounter extends AbstractCthulhuResearchEncounter {

    public ResearchWilderness15Encounter() {
        super(15, LocationType.WILDERNESS);
    }

    @Override
    protected void execute() {
        new InfoFlavorTemplate(getTextBuilder(), () -> {
            ServicePlatform.get().getGameService().gainCondition(ConditionId.CURSED);
        }, new TestPassFlavorInfoTemplate(getTextBuilder(), Stat.WILL, 0, () -> {
            gainThisClue();
        }).withResearchFlavor()
        ).execute();
    }
}
