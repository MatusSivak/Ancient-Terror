package sk.sivak.eldritchhorror.core.eventlistener.encounter.location;

import sk.sivak.eldritchhorror.core.constants.clue.ClueInfo;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.location.PathType;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.LocationEncounter;

import java.util.Collections;
import java.util.List;

public class London16Encounter extends AbstractLocationEncounter{

    public London16Encounter() {
        super(16, LocationEncounter.LocationEncounterType.LONDON);
    }

    @Override
    protected void execute() {
        new TestPassFlavorInfoFailFlavorInfoTemplate(getTextBuilder(), Stat.OBSERVATION, 0,
                () -> {
                    ServicePlatform.get().getTokenService().spawnClues(1).subscribe();
                    ServicePlatform.get().getBasicActionService().gainTravelTicket(PathType.SHIP);
                },
                () -> {
                    ServicePlatform.get().getTokenService().loseSanity(1);
                    ServicePlatform.get().getGameService().gainCondition(ConditionId.DETAINED);
                }).withTwoFailInfos().withTwoPassInfos().withoutFailFlavor().execute();
    }
}
