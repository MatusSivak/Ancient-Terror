package sk.sivak.eldritchhorror.core.eventlistener.encounter.thekeyandthegate;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.EncounterTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TheKeyAndTheGateEncounterTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;

import static sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils.getActiveInvestigatorId;

public class TheKeyAndTheGate1Encounter extends AbstractTheKeyAndTheGateEncounter {

    public TheKeyAndTheGate1Encounter() {
        super(1);
    }

    @Override
    protected void execute() {

        EncounterTemplate passTemplate = new TestPassFlavorInfoFailFlavorInfoTemplate(getTextBuilder(), Stat.LORE, 0,() -> {

            TypewriterUtils.noYesQuestion(getTextBuilder().withPass().withQuestion().build(), () -> {
                TypewriterUtils.confirmInfos(" \n"+getTextBuilder().withFail().withInfo().build()).subscribe(() -> { // ok
                    ServicePlatform.get().getService().hold();
                    ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                    ServicePlatform.get().getTokenService().loseSanity(3);
                    ServicePlatform.get().getService().release();
                });
            }, () -> {
                if (ServicePlatform.get().getConditionsDeck().canGetConditionId(getActiveInvestigatorId(), ConditionId.AMNESIA)) { // ok
                    ServicePlatform.get().getService().hold();
                    ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                    ServicePlatform.get().getGameService().gainCondition(ConditionId.AMNESIA);
                    ServicePlatform.get().getGameService().advanceActiveMystery();
                    ServicePlatform.get().getService().release();
                } else { // ok
                    TypewriterUtils.confirmInfos("You cannot gain another Amnesia Condition.\n \n"+getTextBuilder().withFail().withInfo().build()).subscribe(() -> {
                        ServicePlatform.get().getService().hold();
                        ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                        ServicePlatform.get().getTokenService().loseSanity(3);
                        ServicePlatform.get().getService().release();
                    });
                }
            });
        }, () -> {
            ServicePlatform.get().getTokenService().loseSanity(3); //ok
        }).withoutPassInfo().withoutHidingPaperBeforePass().withoutPassFlavor().withoutFailFlavor();



        EncounterTemplate failTemplate = () -> { //ok
            TypewriterUtils.confirmInfos(getTextBuilder().withInfo().build()).subscribe(() -> {
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                ServicePlatform.get().getService().hideBackground();
                ServicePlatform.get().getService().moveCameraToLocation(ServicePlatform.get().getInvestigators().getActiveInvestigator().getCurrentLocationId());
                ServicePlatform.get().getInvestigatorService().devourInvestigator(getActiveInvestigatorId());
                ServicePlatform.get().getService().release();
            });
        };

        new TheKeyAndTheGateEncounterTemplate(getTextBuilder(), Stat.STRENGTH, -1, passTemplate, failTemplate).execute();
    }
}
