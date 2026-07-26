package sk.sivak.eldritchhorror.core.eventlistener.encounter.research.cthulhu;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.constants.monster.NonEpicMonsterId;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;

public class ResearchCity10Encounter extends AbstractCthulhuResearchEncounter {

    public ResearchCity10Encounter() {
        super(10, LocationType.CITY);
    }

    @Override
    protected void execute() {
        TypewriterUtils.confirmInfos(getTextBuilder().withInfo().build()).subscribe(() -> {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().hideTypewriterPaper();
            ServicePlatform.get().getMonsterService().ambush(NonEpicMonsterId.DEEP_ONE).subscribe(combatData -> {
                if (combatData.getHorrorTestResult().isSuccessful()) {
                    onSuccessful();
                } else {
                    ServicePlatform.get().getService().hold();
                    ServicePlatform.get().getEncounterService().showTypewriterPaper(false);
                    TypewriterUtils.confirmInfos("[#BAD]You didn't pass the Will test[]").subscribe(() -> {
                        ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                    });
                    ServicePlatform.get().getService().release();
                }
            });
            ServicePlatform.get().getService().release();
        });
    }

    private void onSuccessful() {
        ServicePlatform.get().getService().hold();
        ServicePlatform.get().getEncounterService().showTypewriterPaper(false);
        ServicePlatform.get().getEncounterService().typeInfo("[#GOOD]You passed the Will test[]");
        ServicePlatform.get().getEncounterService().typeFlavor(" \n"+getTextBuilder().withPass().withFlavor().build());
        TypewriterUtils.confirmInfos(getTextBuilder().withPass().withInfo().build()).subscribe(() -> {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            gainThisClue();
            ServicePlatform.get().getService().release();
        });
        ServicePlatform.get().getService().release();
    }
}
