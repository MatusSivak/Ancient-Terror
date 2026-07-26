package sk.sivak.eldritchhorror.core.eventlistener.encounter.otherworld;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.EncounterTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.InfoFlavorTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.OtherWorldEncounterTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.service.data.ClueFocusHealthSanity;

public class OtherWorld19Encounter extends AbstractOtherWorldEncounter {

    public OtherWorld19Encounter() {
        super(19);
    }

    @Override
    protected void execute() {

        EncounterTemplate passTemplate = new InfoFlavorTemplate(getTextBuilder(), this::gainTwoClues,
                new TestPassFlavorInfoFailFlavorInfoTemplate(getTextBuilder(), Stat.LORE, -1,
                        this::closeThisGateAndEnd,
                        () -> loseSanity(1))
                .withOtherWorldFlavor().withoutPassFlavor());

        EncounterTemplate failTemplate = somethingUnlessDelayedTemplate(() -> {
            ServicePlatform.get().getService().hold();
            gainCondition(ConditionId.PARANOIA);
            loseSanity(1);
            ServicePlatform.get().getService().release();
        });
        new OtherWorldEncounterTemplate(getTextBuilder(), Stat.INFLUENCE, 0, passTemplate, failTemplate).execute();
    }
}
