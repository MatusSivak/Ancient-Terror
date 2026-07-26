package sk.sivak.eldritchhorror.core.eventlistener.encounter.general;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;
import sk.sivak.eldritchhorror.core.eventtype.data.investigator.DelayedData;

import static sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils.getActiveInvestigatorId;

public class Sea16Encounter extends AbstractGeneralEncounter {

    public Sea16Encounter() {
        super(16, LocationType.SEA);
    }

    @Override
    protected void execute() {
        TypewriterUtils.noYesQuestion(getTextBuilder().withQuestion().build(), null, this::onYes);
    }

    private void onYes() {
        ServicePlatform.get().getEncounterService().hold();
        ServicePlatform.get().getEncounterService().hideTypewriterPaper();
        ServicePlatform.get().getInvestigatorService().becomeDelayed(new DelayedData(getActiveInvestigatorId(), false));
        ServicePlatform.get().getEncounterService().showTypewriterPaper(false);
        ServicePlatform.get().getEncounterService().typeFlavor(getTextBuilder().withPass().withFlavor().build());
        TypewriterUtils.confirmInfos(getTextBuilder().withPass().withInfo().build()).subscribe(() -> {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            if (ServicePlatform.get().getInvestigators().getActiveInvestigator().isDelayed()) {
                ServicePlatform.get().getTokenService().spawnClues(2).subscribe();
            }
            ServicePlatform.get().getService().release();
        });
        ServicePlatform.get().getEncounterService().release();
    }


}
