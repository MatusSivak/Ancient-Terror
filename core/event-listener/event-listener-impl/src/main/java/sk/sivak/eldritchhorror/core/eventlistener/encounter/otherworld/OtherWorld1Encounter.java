package sk.sivak.eldritchhorror.core.eventlistener.encounter.otherworld;

import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.EncounterTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.InfoFlavorTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.OtherWorldEncounterTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.SomethingUnlessSpendTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.service.data.ClueFocusHealthSanity;

public class OtherWorld1Encounter extends AbstractOtherWorldEncounter {

    public OtherWorld1Encounter() {
        super(1);
    }

    @Override
    protected void execute() {

        InfoFlavorTemplate passTemplate = new InfoFlavorTemplate(getTextBuilder(), this::closeThisGate,
                new TestPassFlavorInfoFailFlavorInfoTemplate(getTextBuilder(), Stat.LORE, 0,
                // pass
                () -> selectReward(
                        () -> ServicePlatform.get().getTokenService().gainClueFromPool(),
                        () -> ServicePlatform.get().getGameService().gainSpell()),
                // fail
                () -> loseSanity(2)
        ).withoutPassFlavor().withoutPassInfo().withoutHidingPaperBeforePass().withOtherWorldFlavor());

        EncounterTemplate failTemplate = new SomethingUnlessSpendTemplate(getTextBuilder(), new ClueFocusHealthSanity(1, 0, 0, 0), () -> loseSanity(3));
        new OtherWorldEncounterTemplate(getTextBuilder(), Stat.OBSERVATION, -1, passTemplate, failTemplate).execute();
    }
}
