package sk.sivak.eldritchhorror.core.eventlistener.encounter.general;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;

public class Sea7Encounter extends AbstractGeneralEncounter {

    public Sea7Encounter() {
        super(7, LocationType.SEA);
    }

    @Override
    protected void execute() {
        TypewriterUtils.confirmInfos(
                getTextBuilder().withInfo(1).build(),
                getTextBuilder().withInfo(2).build()).subscribe(() -> {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            ServicePlatform.get().getTokenService().loseSanity(1);
            ServicePlatform.get().getGameService().gainCondition(ConditionId.BACK_INJURY);
            ServicePlatform.get().getService().release();
        });
    }
}
