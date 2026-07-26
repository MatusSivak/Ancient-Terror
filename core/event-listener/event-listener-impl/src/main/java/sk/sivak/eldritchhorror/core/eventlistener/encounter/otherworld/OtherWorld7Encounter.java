package sk.sivak.eldritchhorror.core.eventlistener.encounter.otherworld;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.EncounterTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.OtherWorldEncounterTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;

public class OtherWorld7Encounter extends AbstractOtherWorldEncounter {

    public OtherWorld7Encounter() {
        super(7);
    }

    @Override
    protected void execute() {
        InvestigatorId activeInvestigatorId = ServicePlatform.get().getInvestigators().getActiveInvestigatorId();
        EncounterTemplate passTemplate = new TestPassFlavorInfoFailFlavorInfoTemplate(getTextBuilder(), Stat.STRENGTH, 0,
                this::closeThisGateAndEnd,
                () -> {
                    ServicePlatform.get().getService().hold();
                    loseHealth(2);
                    becomeDelayed();
                    ServicePlatform.get().getService().release();
                })
                .withOtherWorldFlavor().withTwoFailInfos();

        EncounterTemplate failTemplate = () -> {
            TypewriterUtils.confirmInfos(
                    getTextBuilder().withInfo(1).build(),
                    getTextBuilder().withInfo(2).build()).subscribe(() -> {
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                becomeDelayed();
                gainCondition(ConditionId.HALLUCINATIONS);
                ServicePlatform.get().getService().release();
            });
        };


        int clueCount = ServicePlatform.get().getCluePool().getClueCount(activeInvestigatorId);
        new OtherWorldEncounterTemplate(getTextBuilder(), Stat.WILL, -1 + clueCount, passTemplate, failTemplate).execute();
    }
}
