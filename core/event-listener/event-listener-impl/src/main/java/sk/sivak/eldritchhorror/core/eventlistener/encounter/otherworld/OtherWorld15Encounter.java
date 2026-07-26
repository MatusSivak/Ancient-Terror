package sk.sivak.eldritchhorror.core.eventlistener.encounter.otherworld;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.EncounterTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;

public class OtherWorld15Encounter extends AbstractOtherWorldEncounter {

    public OtherWorld15Encounter() {
        super(15);
    }

    @Override
    protected void execute() {

        EncounterTemplate passTemplate = new TestPassFlavorInfoFailFlavorInfoTemplate(getTextBuilder(), Stat.LORE, 0,
                this::closeThisGateAndEnd,
                () -> gainCondition(ConditionId.HALLUCINATIONS))
                .withOtherWorldFlavor();

        EncounterTemplate failTemplate = new TestPassFlavorInfoFailFlavorInfoTemplate(getTextBuilder(), Stat.WILL, 0,
                this::closeThisGateAndEnd,
                () -> {
                    loseHealth(1);
                })
                .withOtherWorldFlavor().withoutFailFlavor();

        spendOneClueToResolvePassTemplate(passTemplate, failTemplate).execute();
    }
}
