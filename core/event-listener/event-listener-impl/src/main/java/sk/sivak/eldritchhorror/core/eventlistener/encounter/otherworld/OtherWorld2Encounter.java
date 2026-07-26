package sk.sivak.eldritchhorror.core.eventlistener.encounter.otherworld;

import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.InfoFlavorTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.OtherWorldEncounterTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoTemplate;

public class OtherWorld2Encounter extends AbstractOtherWorldEncounter {

    public OtherWorld2Encounter() {
        super(2);
    }

    @Override
    protected void execute() {
        InfoFlavorTemplate passTemplate = new InfoFlavorTemplate(getTextBuilder(), this::closeThisGate,
                new TestPassFlavorInfoTemplate(getTextBuilder(), Stat.OBSERVATION, 0,
                        this::gainTwoClues)
                        .withoutPassFlavor().withOtherWorldFlavor());

        TestPassFlavorInfoFailFlavorInfoTemplate failTemplate = new TestFailFlavorInfoTemplate(getTextBuilder(), Stat.LORE, 0,
                () -> loseSanity(6)
        ).withOtherWorldFlavor();
        new OtherWorldEncounterTemplate(getTextBuilder(), Stat.LORE, -1, passTemplate, failTemplate).execute();
    }
}
