package sk.sivak.eldritchhorror.core.eventlistener.encounter.voidbetweenworlds;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.EncounterTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.VoidBetweenWorldsEncounterTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;

public class VoidBetweenWorlds2Encounter extends AbstractVoidBetweenWorldsEncounter {

    public VoidBetweenWorlds2Encounter() {
        super(2);
    }

    @Override
    protected void execute() {

        EncounterTemplate passTemplate = new TestPassFlavorInfoFailFlavorInfoTemplate(getTextBuilder(), Stat.LORE, -1,() -> {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getGameService().advanceActiveMystery();
            closeThisGateAndEnd();
            ServicePlatform.get().getService().release();
        }, () -> {
            ServicePlatform.get().getMonsterService().ambush(null).subscribe();
        }).withTwoPassInfos().withoutPassFlavor();

        EncounterTemplate failTemplate = () -> {
            TypewriterUtils.confirmInfos(
                    getTextBuilder().withInfo(1).build(),
                    getTextBuilder().withInfo(2).build()
                    ).subscribe(() -> {
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                gainCondition(ConditionId.HALLUCINATIONS);
                gainCondition(ConditionId.LOST_IN_TIME_AND_SPACE);
                ServicePlatform.get().getService().release();
            });
        };

        new VoidBetweenWorldsEncounterTemplate(getTextBuilder(), Stat.INFLUENCE, 0, passTemplate, failTemplate).execute();
    }
}
