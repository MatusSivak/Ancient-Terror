package sk.sivak.eldritchhorror.core.eventlistener.encounter.otherworld;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.BecomeDelayedTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.EncounterTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.InfoFlavorTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.OtherWorldEncounterTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.service.data.ClueFocusHealthSanity;

public class OtherWorld4Encounter extends AbstractOtherWorldEncounter {

    public OtherWorld4Encounter() {
        super(4);
    }

    @Override
    protected void execute() {
        InfoFlavorTemplate passTemplate = new InfoFlavorTemplate(getTextBuilder(), this::closeThisGate,
                new BecomeDelayedTemplate(getTextBuilder(), this::gainTwoClues));

        EncounterTemplate failTemplate = new TestFailFlavorInfoTemplate(getTextBuilder(), Stat.INFLUENCE, 0, () -> {
            ServicePlatform.get().getDoomOmenService().advanceDoom();
        }).withOtherWorldFlavor();
        new OtherWorldEncounterTemplate(getTextBuilder(), Stat.LORE, -1, passTemplate, failTemplate).execute();
    }
}
