package sk.sivak.eldritchhorror.core.eventlistener.encounter.otherworld;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.EncounterTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.InfoFlavorTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.OtherWorldEncounterTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;

public class OtherWorld20Encounter extends AbstractOtherWorldEncounter {

    public OtherWorld20Encounter() {
        super(20);
    }

    @Override
    protected void execute() {

        EncounterTemplate passTemplate = new InfoFlavorTemplate(getTextBuilder(), this::gainSpell,
                new TestPassFlavorInfoFailFlavorInfoTemplate(getTextBuilder(), Stat.INFLUENCE, -1,
                        this::closeThisGateAndEnd,
                        this::becomeDelayed)
                .withOtherWorldFlavor());

        EncounterTemplate failTemplate = new InfoFlavorTemplate(getTextBuilder(), () -> gainCondition(ConditionId.HALLUCINATIONS),
                new TestFailFlavorInfoTemplate(getTextBuilder(), Stat.WILL, 0,
                        () -> loseHealth(2))
                        .withOtherWorldFlavor().withoutFailFlavor());

        new OtherWorldEncounterTemplate(getTextBuilder(), Stat.LORE, 0, passTemplate, failTemplate).execute();
    }
}
