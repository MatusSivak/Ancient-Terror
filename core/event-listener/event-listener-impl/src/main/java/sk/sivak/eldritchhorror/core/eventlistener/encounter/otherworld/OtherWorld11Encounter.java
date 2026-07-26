package sk.sivak.eldritchhorror.core.eventlistener.encounter.otherworld;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.EncounterTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.OtherWorldEncounterTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.SomethingUnlessSpendTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.service.data.ClueFocusHealthSanity;

public class OtherWorld11Encounter extends AbstractOtherWorldEncounter {

    public OtherWorld11Encounter() {
        super(11);
    }

    @Override
    protected void execute() {

        EncounterTemplate passTemplate = new TestPassFlavorInfoFailFlavorInfoTemplate(getTextBuilder(), Stat.WILL, 0,
                this::closeThisGateAndEnd,
                this::becomeDelayed)
                .withOtherWorldFlavor();

        EncounterTemplate failTemplate = new SomethingUnlessSpendTemplate(getTextBuilder(),
                new ClueFocusHealthSanity(1, 0, 0, 0), () -> gainCondition(ConditionId.AMNESIA));

        new OtherWorldEncounterTemplate(getTextBuilder(), Stat.LORE, 0, passTemplate, failTemplate).execute();
    }
}
