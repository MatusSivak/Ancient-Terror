package sk.sivak.eldritchhorror.core.eventlistener.encounter.otherworld;

import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.BecomeDelayedTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.EncounterTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.InfoFlavorTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.OtherWorldEncounterTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestFailFlavorInfoTemplate;

public class OtherWorld5Encounter extends AbstractOtherWorldEncounter {

    public OtherWorld5Encounter() {
        super(5);
    }

    @Override
    protected void execute() {
        InfoFlavorTemplate passTemplate = new InfoFlavorTemplate(getTextBuilder(), this::closeThisGate,
                new BecomeDelayedTemplate(getTextBuilder(), this::gainTwoClues));

        EncounterTemplate failTemplate = new TestFailFlavorInfoTemplate(getTextBuilder(), Stat.STRENGTH, 0, () -> {
            loseHealth(6);
        }).withOtherWorldFlavor();
        new OtherWorldEncounterTemplate(getTextBuilder(), Stat.INFLUENCE, -1, passTemplate, failTemplate).execute();
    }
}
