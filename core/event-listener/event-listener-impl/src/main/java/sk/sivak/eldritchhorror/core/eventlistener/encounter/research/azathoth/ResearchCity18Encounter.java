package sk.sivak.eldritchhorror.core.eventlistener.encounter.research.azathoth;

import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.constants.monster.NonEpicMonsterId;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;

public class ResearchCity18Encounter extends AbstractAzathothResearchEncounter {

    public ResearchCity18Encounter() {
        super(18, LocationType.CITY);
    }

    @Override
    protected void execute() {
        TypewriterUtils.confirmInfos(getTextBuilder().withInfo().build()).subscribe(() -> {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().hideTypewriterPaper();
            ServicePlatform.get().getMonsterService().ambush(NonEpicMonsterId.CULTIST).subscribe(combatData -> {
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getEncounterService().showTypewriterPaper(false);
                if (combatData.getHorrorTestResult().isSuccessful()) {
                    onSuccess();
                } else {
                    onFail();
                }
                ServicePlatform.get().getService().release();
            });
            ServicePlatform.get().getService().release();
        });
    }

    private void onSuccess() {
        ServicePlatform.get().getEncounterService().typeFlavor(" \n"+getTextBuilder().withPass().withFlavor().build());
        TypewriterUtils.confirmInfos(getTextBuilder().withPass().withInfo().build()).subscribe(() -> {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            gainThisClue();
            ServicePlatform.get().getService().release();
        });
    }

    private void onFail() {
        ServicePlatform.get().getEncounterService().typeFlavor(" \n"+getTextBuilder().withFail().withFlavor().build());
        TypewriterUtils.confirmInfos(getTextBuilder().withFail().withInfo().build()).subscribe(() -> {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            ServicePlatform.get().getDoomOmenService().advanceOmen();
            ServicePlatform.get().getService().release();
        });
    }
}
