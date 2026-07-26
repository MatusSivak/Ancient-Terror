package sk.sivak.eldritchhorror.core.eventlistener.encounter.otherworld;

import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.BecomeDelayedTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.EncounterTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.InfoFlavorTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.OtherWorldEncounterTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestFailFlavorInfoTemplate;

public class OtherWorld6Encounter extends AbstractOtherWorldEncounter {

    public OtherWorld6Encounter() {
        super(6);
    }

    @Override
    protected void execute() {
        InfoFlavorTemplate passTemplate = new InfoFlavorTemplate(getTextBuilder(), this::closeThisGate,
                new BecomeDelayedTemplate(getTextBuilder(), this::gainSpell));

        EncounterTemplate failTemplate = new TestFailFlavorInfoTemplate(getTextBuilder(), Stat.WILL, 0, () -> {
            loseSanity(3);
        }).withoutFailFlavor().withOtherWorldFlavor();
        new OtherWorldEncounterTemplate(getTextBuilder(), Stat.LORE, 0, passTemplate, failTemplate).execute();
    }
}
