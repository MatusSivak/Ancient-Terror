package sk.sivak.eldritchhorror.core.eventlistener.encounter.location;

import sk.sivak.eldritchhorror.core.constants.clue.ClueInfo;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.LocationEncounter;

import java.util.Collections;
import java.util.List;

public class London15Encounter extends AbstractLocationEncounter{

    public London15Encounter() {
        super(15, LocationEncounter.LocationEncounterType.LONDON);
    }

    @Override
    protected void execute() {
        new TestPassFlavorInfoFailFlavorInfoTemplate(getTextBuilder(), Stat.OBSERVATION, 0,
                this::moveUpToTwoCluesToLondon,
                () -> ServicePlatform.get().getGameService().gainCondition(ConditionId.LOST_IN_TIME_AND_SPACE)).execute();
    }

    private void moveUpToTwoCluesToLondon() {
        List<? extends ClueInfo> spawnedClues = ServicePlatform.get().getCluePool().getSpawnedClues();
        Collections.shuffle(spawnedClues);

        ServicePlatform.get().getService().hold();
        for (int i=0; i< Math.min(spawnedClues.size(), 2); i++) {
            ServicePlatform.get().getTokenService().moveClue(spawnedClues.get(i).getSpawnLocationId(), LocationId.LONDON);
        }
        ServicePlatform.get().getService().release();
    }
}
