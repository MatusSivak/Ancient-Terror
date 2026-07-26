package sk.sivak.eldritchhorror.core.eventlistener.encounter.thekeyandthegate;

import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.EncounterTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TheKeyAndTheGateEncounterTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;
import sk.sivak.eldritchhorror.core.eventtype.SelectCardData;

import static sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils.getActiveInvestigatorId;

public class TheKeyAndTheGate4Encounter extends AbstractTheKeyAndTheGateEncounter {

    public TheKeyAndTheGate4Encounter() {
        super(4);
    }

    @Override
    protected void execute() {

        EncounterTemplate passTemplate = new TestPassFlavorInfoFailFlavorInfoTemplate(getTextBuilder(), Stat.LORE, 0,() -> {

            TypewriterUtils.noYesQuestion(getTextBuilder().withPass().withQuestion().build(), () -> {
                TypewriterUtils.confirmInfos(" \n"+getTextBuilder().withFail().withInfo().build()).subscribe(() -> {
                    ServicePlatform.get().getService().hold();
                    ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                    ServicePlatform.get().getTokenService().loseSanity(3);
                    ServicePlatform.get().getService().release();
                });
            }, () -> {
                if (canDiscardAnySpell()) {
                    ServicePlatform.get().getService().hold();
                    ServicePlatform.get().getEncounterService().finishTypewriterPaper();

                    SelectCardData selectCardData = new SelectCardData();
                    selectCardData.setAvailableCards(ServicePlatform.get().getSpellsDeck().getSpells(getActiveInvestigatorId()));
                    selectCardData.setHideText("Display Spells?");
                    selectCardData.setTitleText("Select Spell");
                    ServicePlatform.get().getCardService().selectSingleCard(selectCardData).subscribe(spell -> {
                        ServicePlatform.get().getService().hold();
                        EncounterUtils.onSelectCardToDiscardByChoice(spell);
                        ServicePlatform.get().getGameService().advanceActiveMystery();
                        ServicePlatform.get().getService().release();
                    });
                    ServicePlatform.get().getService().release();
                } else {
                    TypewriterUtils.confirmInfos("You cannot discard any Spell.\n \n"+getTextBuilder().withFail().withInfo().build()).subscribe(() -> {
                        ServicePlatform.get().getService().hold();
                        ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                        ServicePlatform.get().getTokenService().loseSanity(3);
                        ServicePlatform.get().getService().release();
                    });
                }
            });
        }, () -> {
            ServicePlatform.get().getTokenService().loseSanity(3); // ok
        }).withoutPassInfo().withoutHidingPaperBeforePass().withoutPassFlavor().withoutFailFlavor();



        EncounterTemplate failTemplate = () -> { // ok
            TypewriterUtils.confirmInfos(getTextBuilder().withInfo().build()).subscribe(() -> {
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                ServicePlatform.get().getService().hideBackground();
                ServicePlatform.get().getService().moveCameraToLocation(ServicePlatform.get().getInvestigators().getActiveInvestigator().getCurrentLocationId());
                ServicePlatform.get().getInvestigatorService().devourInvestigator(getActiveInvestigatorId());
                ServicePlatform.get().getService().release();
            });
        };

        new TheKeyAndTheGateEncounterTemplate(getTextBuilder(), Stat.WILL, -1, passTemplate, failTemplate).execute();
    }

    private boolean canDiscardAnySpell() {
        return !ServicePlatform.get().getSpellsDeck().getSpells(getActiveInvestigatorId()).isEmpty();
    }
}
