package sk.sivak.eldritchhorror.core.eventlistener.encounter.otherworld;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.EncounterTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.OtherWorldEncounterTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;

public class OtherWorld17Encounter extends AbstractOtherWorldEncounter {

    public OtherWorld17Encounter() {
        super(17);
    }

    @Override
    protected void execute() {

        EncounterTemplate passTemplate = new TestPassFlavorInfoFailFlavorInfoTemplate(getTextBuilder(), Stat.LORE, 0,
                this::closeThisGateAndEnd,
                () -> loseSanity(1))
                .withOtherWorldFlavor().withoutPassFlavor();

        EncounterTemplate failTemplate = new TestPassFlavorInfoFailFlavorInfoTemplate(getTextBuilder(), Stat.LORE, -1,
                this::closeThisGateAndEnd,
                () -> loseHealth(2))
                .withOtherWorldFlavor();

        new OtherWorldEncounterTemplate(getTextBuilder(), Stat.WILL, 0, passTemplate, failTemplate).execute();
    }
}
