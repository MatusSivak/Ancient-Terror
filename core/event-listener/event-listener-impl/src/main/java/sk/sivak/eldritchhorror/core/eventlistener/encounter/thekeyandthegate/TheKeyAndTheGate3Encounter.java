package sk.sivak.eldritchhorror.core.eventlistener.encounter.thekeyandthegate;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.EncounterTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TheKeyAndTheGateEncounterTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;

import static sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils.getActiveInvestigatorId;

public class TheKeyAndTheGate3Encounter extends AbstractTheKeyAndTheGateEncounter {

    public TheKeyAndTheGate3Encounter() {
        super(3);
    }

    @Override
    protected void execute() {

        EncounterTemplate passTemplate = () -> {
            int ownedSpells = ServicePlatform.get().getSpellsDeck().getSpells(getActiveInvestigatorId()).size();

            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().typeInfo("This test has negative modifier\nequal to owned Spells.");

            new TestPassFlavorInfoFailFlavorInfoTemplate(getTextBuilder(), Stat.WILL, -ownedSpells, () -> {
                ServicePlatform.get().getGameService().advanceActiveMystery();
            }, () -> {
                ServicePlatform.get().getTokenService().loseSanity(3);
            }).withoutPassFlavor().withoutFailFlavor().execute();

            ServicePlatform.get().getService().release();
        };

        EncounterTemplate failTemplate = () -> { //ok
            TypewriterUtils.confirmInfos(
                    getTextBuilder().withInfo(1).build(),
                    getTextBuilder().withInfo(2).build()).subscribe(() -> {
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                ServicePlatform.get().getGameService().gainSpell();
                ServicePlatform.get().getGameService().gainSpell();
                ServicePlatform.get().getGameService().gainCondition(ConditionId.DARK_PACT);
                ServicePlatform.get().getService().release();
            });
        };

        new TheKeyAndTheGateEncounterTemplate(getTextBuilder(), Stat.LORE, -1, passTemplate, failTemplate).execute();
    }
}
