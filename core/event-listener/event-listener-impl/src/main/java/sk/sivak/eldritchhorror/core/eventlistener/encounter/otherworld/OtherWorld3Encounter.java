package sk.sivak.eldritchhorror.core.eventlistener.encounter.otherworld;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.EncounterTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.InfoFlavorTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.OtherWorldEncounterTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.SomethingUnlessSpendTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.service.data.ClueFocusHealthSanity;

public class OtherWorld3Encounter extends AbstractOtherWorldEncounter {

    public OtherWorld3Encounter() {
        super(3);
    }

    @Override
    protected void execute() {
        InfoFlavorTemplate passTemplate = new InfoFlavorTemplate(getTextBuilder(), this::closeThisGate,
                new TestPassFlavorInfoTemplate(getTextBuilder(), Stat.WILL, 0,
                        this::gainClue).withOtherWorldFlavor());

        EncounterTemplate failTemplate = new SomethingUnlessSpendTemplate(getTextBuilder(), new ClueFocusHealthSanity(1, 0, 0, 0), () -> {
            loseSanity(2);
            gainCondition(ConditionId.PARANOIA);
        });
        new OtherWorldEncounterTemplate(getTextBuilder(), Stat.LORE, -1, passTemplate, failTemplate).execute();
    }
}
