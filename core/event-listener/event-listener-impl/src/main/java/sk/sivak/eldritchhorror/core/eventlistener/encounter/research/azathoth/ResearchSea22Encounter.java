package sk.sivak.eldritchhorror.core.eventlistener.encounter.research.azathoth;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.constants.omen.OmenColor;
import sk.sivak.eldritchhorror.core.constants.omen.OmenId;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;

import java.util.List;

public class ResearchSea22Encounter extends AbstractAzathothResearchEncounter {

    public ResearchSea22Encounter() {
        super(22, LocationType.SEA);
    }

    @Override
    protected void execute() {
        ServicePlatform.get().getEncounterService().typeInfo(getTextBuilder().withInfo().build());
        int tokensCount = ServicePlatform.get().getOmenTrack().getOmenInfo(OmenId.NORTH).getTokensCount();
        new TestPassFlavorInfoFailFlavorInfoTemplate(getTextBuilder(), Stat.OBSERVATION, - tokensCount,
                this::gainThisClue,
                () -> {
                    ServicePlatform.get().getDoomOmenService().advanceOmen();
                }
        ).withResearchFlavor().execute();
    }
}
