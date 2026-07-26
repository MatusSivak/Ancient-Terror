package sk.sivak.eldritchhorror.core.eventlistener.encounter.research.cthulhu;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.constants.monster.NonEpicMonsterId;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;

public class ResearchWilderness1Encounter extends AbstractCthulhuResearchEncounter {

    public ResearchWilderness1Encounter() {
        super(1, LocationType.WILDERNESS);
    }

    @Override
    protected void execute() {
        TypewriterUtils.confirmInfos(getTextBuilder().withInfo().build()).subscribe(() -> {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().hideTypewriterPaper();
            ServicePlatform.get().getMonsterService().ambush(NonEpicMonsterId.CULTIST).subscribe(combatData -> {
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getEncounterService().showTypewriterPaper(false);
                if (combatData.getMonsterInfo().isAlive()) {
                    ServicePlatform.get().getEncounterService().typeFlavor(" \n"+getTextBuilder().withFail().withFlavor().build());
                    TypewriterUtils.confirmInfos(getTextBuilder().withFail().withInfo().build()).subscribe(() -> {
                        ServicePlatform.get().getService().hold();
                        ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                        ServicePlatform.get().getGameService().gainCondition(ConditionId.CURSED);
                        ServicePlatform.get().getService().release();
                    });
                } else {
                    ServicePlatform.get().getEncounterService().typeFlavor(" \n"+getTextBuilder().withPass().withFlavor().build());
                    TypewriterUtils.confirmInfos(getTextBuilder().withPass().withInfo().build()).subscribe(() -> {
                        ServicePlatform.get().getService().hold();
                        ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                        gainThisClue();
                        ServicePlatform.get().getService().release();
                    });
                }
                ServicePlatform.get().getService().release();
            });
            ServicePlatform.get().getService().release();
        });
    }
}
