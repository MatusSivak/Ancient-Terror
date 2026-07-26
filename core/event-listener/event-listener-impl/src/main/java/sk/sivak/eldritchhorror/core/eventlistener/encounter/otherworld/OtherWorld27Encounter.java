package sk.sivak.eldritchhorror.core.eventlistener.encounter.otherworld;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.EncounterTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.InfoFlavorTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.OtherWorldEncounterTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoTemplate;

public class OtherWorld27Encounter extends AbstractOtherWorldEncounter {

    public OtherWorld27Encounter() {
        super(27);
    }

    @Override
    protected void execute() {

       EncounterTemplate passTemplate = new TestPassFlavorInfoFailFlavorInfoTemplate(getTextBuilder(), Stat.INFLUENCE, -1,
               this::closeThisGateAndEnd,
               () -> gainCondition(ConditionId.AMNESIA))
               .withOtherWorldFlavor();

        EncounterTemplate failTemplate = new InfoFlavorTemplate(getTextBuilder(), () -> gainCondition(ConditionId.BACK_INJURY),
                new TestPassFlavorInfoTemplate(getTextBuilder(), Stat.LORE, -1,
                        this::gainSpell)
                        .withOtherWorldFlavor());

        new OtherWorldEncounterTemplate(getTextBuilder(), Stat.STRENGTH, -1, passTemplate, failTemplate).execute();
    }
}
