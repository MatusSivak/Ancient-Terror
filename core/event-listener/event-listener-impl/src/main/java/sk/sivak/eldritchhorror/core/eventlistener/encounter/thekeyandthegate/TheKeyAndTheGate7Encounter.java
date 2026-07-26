package sk.sivak.eldritchhorror.core.eventlistener.encounter.thekeyandthegate;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.EncounterTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TheKeyAndTheGateEncounterTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;

import static sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils.getActiveInvestigatorId;

public class TheKeyAndTheGate7Encounter extends AbstractTheKeyAndTheGateEncounter {

    public TheKeyAndTheGate7Encounter() {
        super(7);
    }

    @Override
    protected void execute() {

        EncounterTemplate passTemplate = () -> {

            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().typeInfo("Roll one less die for each\ndefeated or devoured investigator.");

            int defeatedOrDevouredCount = ServicePlatform.get().getInvestigators().getDefeatedOrDevouredCount();
            new TestPassFlavorInfoFailFlavorInfoTemplate(getTextBuilder(), Stat.INFLUENCE, -defeatedOrDevouredCount, () -> {
                ServicePlatform.get().getGameService().advanceActiveMystery();
            }, () -> {
                ServicePlatform.get().getGameService().gainCondition(ConditionId.LOST_IN_TIME_AND_SPACE);
            }).execute();

            ServicePlatform.get().getService().release();
        };

        EncounterTemplate failTemplate = () -> {
            TypewriterUtils.confirmInfos(
                    getTextBuilder().withInfo().build()).subscribe(() -> {
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                ServicePlatform.get().getService().hideBackground();
                ServicePlatform.get().getService().moveCameraToLocation(ServicePlatform.get().getInvestigators().getActiveInvestigator().getCurrentLocationId());
                ServicePlatform.get().getInvestigatorService().devourInvestigator(getActiveInvestigatorId());
                ServicePlatform.get().getService().release();
            });
        };

        new TheKeyAndTheGateEncounterTemplate(getTextBuilder(), Stat.OBSERVATION, -1, passTemplate, failTemplate).execute();
    }
}
