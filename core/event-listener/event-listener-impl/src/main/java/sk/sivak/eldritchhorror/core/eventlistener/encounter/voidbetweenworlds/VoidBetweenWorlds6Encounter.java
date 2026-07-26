package sk.sivak.eldritchhorror.core.eventlistener.encounter.voidbetweenworlds;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.EncounterTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.VoidBetweenWorldsEncounterTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;

public class VoidBetweenWorlds6Encounter extends AbstractVoidBetweenWorldsEncounter {

    public VoidBetweenWorlds6Encounter() {
        super(6);
    }

    @Override
    protected void execute() {

        EncounterTemplate passTemplate = new TestPassFlavorInfoFailFlavorInfoTemplate(getTextBuilder(), Stat.WILL, 0,() -> {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getGameService().advanceActiveMystery();
            closeThisGateAndEnd();
            ServicePlatform.get().getService().release();
        }, () -> {
            gainCondition(ConditionId.LOST_IN_TIME_AND_SPACE);
        }).withTwoPassInfos();

        EncounterTemplate failTemplate = () -> {
            TypewriterUtils.confirmInfos(
                    getTextBuilder().withInfo().build()
                    ).subscribe(() -> {
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                ServicePlatform.get().getDoomOmenService().advanceDoom();
                ServicePlatform.get().getService().release();
            });
        };

        new VoidBetweenWorldsEncounterTemplate(getTextBuilder(), Stat.OBSERVATION, -1, passTemplate, failTemplate).execute();
    }
}
