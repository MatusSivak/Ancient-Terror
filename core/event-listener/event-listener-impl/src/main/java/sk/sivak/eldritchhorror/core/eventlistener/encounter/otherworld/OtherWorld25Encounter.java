package sk.sivak.eldritchhorror.core.eventlistener.encounter.otherworld;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.BecomeDelayedTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.EncounterTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.InfoFlavorTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.OtherWorldEncounterTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.SomethingUnlessSpendTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;
import sk.sivak.eldritchhorror.core.service.data.ClueFocusHealthSanity;

public class OtherWorld25Encounter extends AbstractOtherWorldEncounter {

    public OtherWorld25Encounter() {
        super(25);
    }

    @Override
    protected void execute() {

        EncounterTemplate passTemplate = new InfoFlavorTemplate(getTextBuilder(), this::closeThisGate, new BecomeDelayedTemplate(getTextBuilder(), () -> {
            ServicePlatform.get().getInvestigatorService().improveSkill(Stat.LORE);
        }));

        EncounterTemplate failTemplate = new TestPassFlavorInfoFailFlavorInfoTemplate(getTextBuilder(), Stat.INFLUENCE, 0,
                this::closeThisGateAndEnd,
                () -> gainCondition(ConditionId.LOST_IN_TIME_AND_SPACE))
                .withOtherWorldFlavor();


        new OtherWorldEncounterTemplate(getTextBuilder(), Stat.STRENGTH, -2, passTemplate, failTemplate).execute();
    }
}
