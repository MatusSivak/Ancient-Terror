package sk.sivak.eldritchhorror.core.eventlistener.encounter.otherworld;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.EncounterTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.OtherWorldEncounterTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.service.data.ClueFocusHealthSanity;

public class OtherWorld12Encounter extends AbstractOtherWorldEncounter {

    public OtherWorld12Encounter() {
        super(12);
    }

    @Override
    protected void execute() {

        EncounterTemplate passTemplate = new TestPassFlavorInfoFailFlavorInfoTemplate(getTextBuilder(), Stat.LORE, 0,
                this::closeThisGateAndEnd,
                () -> gainCondition(ConditionId.PARANOIA))
                .withOtherWorldFlavor();

        EncounterTemplate failTemplate = somethingUnlessDelayedTemplate(() -> loseSanity(2));

        new OtherWorldEncounterTemplate(getTextBuilder(), Stat.OBSERVATION, 0, passTemplate, failTemplate).execute();
    }
}
