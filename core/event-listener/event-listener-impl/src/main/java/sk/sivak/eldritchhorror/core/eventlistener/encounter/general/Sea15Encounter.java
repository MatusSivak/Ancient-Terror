package sk.sivak.eldritchhorror.core.eventlistener.encounter.general;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;

public class Sea15Encounter extends AbstractGeneralEncounter {

    public Sea15Encounter() {
        super(15, LocationType.SEA);
    }

    @Override
    protected void execute() {
        TypewriterUtils.displayTestButton(Stat.WILL, null, this::onFail);
    }

    private void onFail() {
        TypewriterUtils.confirmInfos(
                getTextBuilder().withFail().withInfo(1).build(),
                getTextBuilder().withFail().withInfo(2).build()).subscribe(() -> {
            ServicePlatform.get().getEncounterService().hold();
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            ServicePlatform.get().getTokenService().loseSanity(1);
            ServicePlatform.get().getGameService().gainCondition(ConditionId.PARANOIA);
            ServicePlatform.get().getEncounterService().release();
        });
    }
}
