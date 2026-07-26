package sk.sivak.eldritchhorror.core.eventlistener.encounter.general;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;

public class Sea3Encounter extends AbstractGeneralEncounter {

    public Sea3Encounter() {
        super(3, LocationType.SEA);
    }

    @Override
    protected void execute() {
        TypewriterUtils.displayTestButton(Stat.LORE,-1, this::onSuccess, null);
    }

    private void onSuccess() {
        ServicePlatform.get().getEncounterService().typeFlavor(getTextBuilder().withPass().withFlavor().build());
        TypewriterUtils.confirmInfos(
                getTextBuilder().withPass().withInfo(1).build(),
                getTextBuilder().withPass().withInfo(2).build(),
                getTextBuilder().withPass().withInfo(3).build()).subscribe(() -> {
            ServicePlatform.get().getEncounterService().hold();
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            ServicePlatform.get().getGameService().gainCondition(ConditionId.BLESSED);
            ServicePlatform.get().getTokenService().gainSanity(1);
            ServicePlatform.get().getTokenService().gainHealth(1);
            ServicePlatform.get().getEncounterService().release();
        });
    }

}
