package sk.sivak.eldritchhorror.core.eventlistener.encounter.voidbetweenworlds;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.EncounterTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.VoidBetweenWorldsEncounterTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;

import static sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils.getActiveInvestigatorId;

public class VoidBetweenWorlds7Encounter extends AbstractVoidBetweenWorldsEncounter {

    public VoidBetweenWorlds7Encounter() {
        super(7);
    }

    @Override
    protected void execute() {



        EncounterTemplate passTemplate = () -> {
            if (ServicePlatform.get().getConditionsDeck().canGetConditionId(getActiveInvestigatorId(), ConditionId.DARK_PACT)) {
                TypewriterUtils.noYesQuestion(getTextBuilder().withQuestion().build(), () -> {
                    ServicePlatform.get().getService().hold();
                    ServicePlatform.get().getEncounterService().typeFlavor(" \n" + getTextBuilder().withFail().withFlavor().build());
                    TypewriterUtils.confirmInfos(getTextBuilder().withFail().withInfo().build()).subscribe(this::gainLostInTimeAndSpace);
                    ServicePlatform.get().getService().release();
                }, () -> {
                    ServicePlatform.get().getService().hold();
                    ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                    gainCondition(ConditionId.DARK_PACT);
                    ServicePlatform.get().getGameService().advanceActiveMystery();
                    closeThisGateAndEnd();
                    ServicePlatform.get().getService().release();
                });
            } else {
                TypewriterUtils.confirmInfos("You cannot gain a Dark Pact Condition.").subscribe(() -> {
                    ServicePlatform.get().getService().hold();
                    ServicePlatform.get().getEncounterService().typeFlavor(" \n" + getTextBuilder().withFail().withFlavor().build());
                    TypewriterUtils.confirmInfos(getTextBuilder().withFail().withInfo().build()).subscribe(this::gainLostInTimeAndSpace);
                    ServicePlatform.get().getService().release();
                });
            }

        };

        EncounterTemplate failTemplate = () -> {
            TypewriterUtils.confirmInfos(getTextBuilder().withInfo().build()).subscribe(this::gainLostInTimeAndSpace);
        };



        new VoidBetweenWorldsEncounterTemplate(getTextBuilder(), Stat.LORE, -1, passTemplate, failTemplate).execute();
    }


    private void gainLostInTimeAndSpace() {
        ServicePlatform.get().getService().hold();
        ServicePlatform.get().getEncounterService().finishTypewriterPaper();
        gainCondition(ConditionId.LOST_IN_TIME_AND_SPACE);
        ServicePlatform.get().getService().release();
    }
}
