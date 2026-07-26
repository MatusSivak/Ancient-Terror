package sk.sivak.eldritchhorror.core.eventlistener.encounter.voidbetweenworlds;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.rlyehrisen.AbstractRlyehRisenEncounter;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.EncounterTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.RlyehRisenEncounterTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.VoidBetweenWorldsEncounterTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;

public class VoidBetweenWorlds1Encounter extends AbstractVoidBetweenWorldsEncounter {

    public VoidBetweenWorlds1Encounter() {
        super(1);
    }

    @Override
    protected void execute() {

        EncounterTemplate passTemplate = new TestPassFlavorInfoFailFlavorInfoTemplate(getTextBuilder(), Stat.LORE, 0,() -> {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getGameService().advanceActiveMystery();
            closeThisGateAndEnd();
            ServicePlatform.get().getService().release();
        }, () -> {
            ServicePlatform.get().getGameService().gainCondition(ConditionId.CURSED);
        }).withTwoPassInfos();

        EncounterTemplate failTemplate = new TestFailFlavorInfoTemplate(getTextBuilder(), Stat.LORE, -1, () -> {
            ServicePlatform.get().getGameService().gainCondition(ConditionId.LOST_IN_TIME_AND_SPACE);
        }).withoutFailFlavor();

        new VoidBetweenWorldsEncounterTemplate(getTextBuilder(), Stat.OBSERVATION, -1, passTemplate, failTemplate).execute();
    }
}
