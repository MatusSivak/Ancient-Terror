package sk.sivak.eldritchhorror.core.eventlistener.encounter.otherworld;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.EncounterTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.OtherWorldEncounterTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;

public class OtherWorld13Encounter extends AbstractOtherWorldEncounter {

    public OtherWorld13Encounter() {
        super(13);
    }

    @Override
    protected void execute() {

        EncounterTemplate passTemplate = new TestPassFlavorInfoFailFlavorInfoTemplate(getTextBuilder(), Stat.LORE, 0,
                this::closeThisGateAndEnd,
                this::becomeDelayed)
                .withOtherWorldFlavor();

        EncounterTemplate failTemplate = new TestPassFlavorInfoFailFlavorInfoTemplate(getTextBuilder(), Stat.LORE, -1,
                this::closeThisGateAndEnd,
                () -> loseSanity(3))
                .withOtherWorldFlavor();

        new OtherWorldEncounterTemplate(getTextBuilder(), Stat.WILL, 0, passTemplate, failTemplate).execute();
    }
}
