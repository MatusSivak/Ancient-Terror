package sk.sivak.eldritchhorror.core.eventlistener.encounter.research.cthulhu;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.constants.spell.SpellId;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;

public class ResearchCity8Encounter extends AbstractCthulhuResearchEncounter {

    public ResearchCity8Encounter() {
        super(8, LocationType.CITY);
    }

    @Override
    protected void execute() {
        new TestPassFlavorInfoFailFlavorInfoTemplate(getTextBuilder(), Stat.STRENGTH, 0,
                this::gainThisClue,
                () -> {
                    ServicePlatform.get().getGameService().hold();
                    ServicePlatform.get().getTokenService().loseHealth(1);
                    ServicePlatform.get().getGameService().gainCondition(ConditionId.DETAINED);
                    ServicePlatform.get().getGameService().release();
                }
        ).withResearchFlavor().withTwoFailInfos().execute();
    }
}
