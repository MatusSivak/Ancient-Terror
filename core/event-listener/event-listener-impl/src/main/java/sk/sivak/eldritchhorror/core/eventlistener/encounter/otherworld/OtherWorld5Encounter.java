package sk.sivak.eldritchhorror.core.eventlistener.encounter.otherworld;

import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.EncounterTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.InfoFlavorTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.OtherWorldEncounterTemplate;

public class OtherWorld5Encounter extends AbstractOtherWorldEncounter {

    public OtherWorld5Encounter() {
        super(5);
    }

    @Override
    protected void execute() {
        InfoFlavorTemplate passTemplate = new InfoFlavorTemplate(getTextBuilder(), this::closeThisGate,
                becomeDelayedForRewardTemplate(this::gainTwoClues));

        EncounterTemplate failTemplate = new TestPassFlavorInfoFailFlavorInfoTemplate(getTextBuilder(), Stat.STRENGTH, 0, NOTHING, () -> {
            loseHealth(6);
        }).withOkAfterPassFlavor().withOtherWorldFlavor();
        new OtherWorldEncounterTemplate(getTextBuilder(), Stat.INFLUENCE, -1, passTemplate, failTemplate).execute();
    }
}
