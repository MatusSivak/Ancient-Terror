package sk.sivak.eldritchhorror.core.eventlistener.encounter.voidbetweenworlds;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.EncounterTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.VoidBetweenWorldsEncounterTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;

public class VoidBetweenWorlds5Encounter extends AbstractVoidBetweenWorldsEncounter {

    public VoidBetweenWorlds5Encounter() {
        super(5);
    }

    @Override
    protected void execute() {

        EncounterTemplate passTemplate = new TestPassFlavorInfoFailFlavorInfoTemplate(getTextBuilder(), Stat.WILL, -1,() -> {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getGameService().advanceActiveMystery();
            closeThisGateAndEnd();
            ServicePlatform.get().getService().release();
        }, () -> {
            gainCondition(ConditionId.POISONED);
        }).withTwoPassInfos();

        EncounterTemplate failTemplate = () -> {
            TypewriterUtils.confirmInfos(
                    getTextBuilder().withInfo().build()
                    ).subscribe(() -> {
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                gainCondition(ConditionId.LOST_IN_TIME_AND_SPACE);
                ServicePlatform.get().getService().release();
            });
        };

        new VoidBetweenWorldsEncounterTemplate(getTextBuilder(), Stat.STRENGTH, 0, passTemplate, failTemplate).execute();
    }
}
