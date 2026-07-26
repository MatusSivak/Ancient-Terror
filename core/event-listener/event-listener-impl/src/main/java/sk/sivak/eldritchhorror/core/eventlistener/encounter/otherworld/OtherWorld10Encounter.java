package sk.sivak.eldritchhorror.core.eventlistener.encounter.otherworld;

import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.EncounterTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.OtherWorldEncounterTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.SomethingUnlessSpendTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.service.data.ClueFocusHealthSanity;

public class OtherWorld10Encounter extends AbstractOtherWorldEncounter {

    public OtherWorld10Encounter() {
        super(10);
    }

    @Override
    protected void execute() {

        EncounterTemplate passTemplate = new TestPassFlavorInfoFailFlavorInfoTemplate(getTextBuilder(), Stat.LORE, 0,
                this::closeThisGateAndEnd,
                () -> loseSanity(1))
                .withOtherWorldFlavor();

        EncounterTemplate failTemplate = new SomethingUnlessSpendTemplate(getTextBuilder(),
                new ClueFocusHealthSanity(1, 0, 0, 0), () -> loseSanity(2));
        new OtherWorldEncounterTemplate(getTextBuilder(), Stat.INFLUENCE, 0, passTemplate, failTemplate).execute();
    }
}
