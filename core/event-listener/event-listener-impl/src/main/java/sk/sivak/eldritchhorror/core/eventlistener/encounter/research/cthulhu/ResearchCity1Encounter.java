package sk.sivak.eldritchhorror.core.eventlistener.encounter.research.cthulhu;

import java8.features.stream.Stream;
import java8.features.util.IterableUtils;
import sk.sivak.eldritchhorror.core.constants.clue.ClueInfo;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.FindNearestData;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.constants.omen.OmenId;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.research.azathoth.AbstractAzathothResearchEncounter;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;

public class ResearchCity1Encounter extends AbstractCthulhuResearchEncounter {

    public ResearchCity1Encounter() {
        super(1, LocationType.CITY);
    }

    @Override
    protected void execute() {
        new TestPassFlavorInfoFailFlavorInfoTemplate(getTextBuilder(), Stat.OBSERVATION, 0,
                this::gainThisClue,
                this::moveClueToSea
        ).withResearchFlavor().withoutFailFlavor().execute();
    }
}
