package sk.sivak.eldritchhorror.core.eventlistener.encounter.otherworld;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.BecomeDelayedTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.EncounterTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.InfoFlavorTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.OtherWorldEncounterTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;

public class OtherWorld8Encounter extends AbstractOtherWorldEncounter {

    public OtherWorld8Encounter() {
        super(8);
    }

    @Override
    protected void execute() {
        EncounterTemplate passTemplate = new TestPassFlavorInfoFailFlavorInfoTemplate(getTextBuilder(), Stat.LORE, 0,
                this::closeThisGateAndEnd,
                () -> loseHealth(2))
                .withOtherWorldFlavor();

        EncounterTemplate failTemplate = new TestFailFlavorInfoTemplate(getTextBuilder(), Stat.STRENGTH, 0, () -> {
            gainCondition(ConditionId.AMNESIA);
        }).withOtherWorldFlavor();

        new OtherWorldEncounterTemplate(getTextBuilder(), Stat.WILL, 0, passTemplate, failTemplate).execute();
    }
}
