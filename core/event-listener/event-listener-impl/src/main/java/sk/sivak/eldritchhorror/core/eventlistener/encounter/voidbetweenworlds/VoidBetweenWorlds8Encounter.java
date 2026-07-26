package sk.sivak.eldritchhorror.core.eventlistener.encounter.voidbetweenworlds;

import sk.sivak.eldritchhorror.core.constants.card.CardInfo;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.EncounterTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.VoidBetweenWorldsEncounterTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;
import sk.sivak.eldritchhorror.core.eventtype.SelectCardData;

import static sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils.getActiveInvestigatorId;

public class VoidBetweenWorlds8Encounter extends AbstractVoidBetweenWorldsEncounter {

    public VoidBetweenWorlds8Encounter() {
        super(8);
    }

    @Override
    protected void execute() {
        EncounterTemplate passTemplate = () -> {
            if (!ServicePlatform.get().getSpellsDeck().getSpells(getActiveInvestigatorId()).isEmpty()) {
                TypewriterUtils.noYesQuestion(getTextBuilder().withQuestion().build(), () -> {
                    ServicePlatform.get().getService().hold();
                    TypewriterUtils.confirmInfos(" \n"+getTextBuilder().withFail().withInfo().build()).subscribe(() -> {
                        ServicePlatform.get().getService().hold();
                        ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                        gainCondition(ConditionId.LOST_IN_TIME_AND_SPACE);
                        ServicePlatform.get().getService().release();
                    });
                    ServicePlatform.get().getService().release();
                }, () -> {
                    ServicePlatform.get().getService().hold();
                    ServicePlatform.get().getEncounterService().finishTypewriterPaper();

                    SelectCardData selectCardData = new SelectCardData();
                    selectCardData.setAvailableCards(ServicePlatform.get().getSpellsDeck().getSpells(getActiveInvestigatorId()));
                    selectCardData.setHideText("Display Spells?");
                    selectCardData.setTitleText("Select Spell to discard");
                    ServicePlatform.get().getCardService().selectSingleCard(selectCardData).subscribe(cardInfo -> {
                        ServicePlatform.get().getService().hold();
                        EncounterUtils.onSelectCardToDiscard(cardInfo);
                        ServicePlatform.get().getGameService().advanceActiveMystery();
                        closeThisGateAndEnd();
                        ServicePlatform.get().getService().release();
                    });
                    ServicePlatform.get().getService().release();
                });
            } else {
                TypewriterUtils.confirmInfos("You cannot discard a Spell.").subscribe(() -> {
                    ServicePlatform.get().getService().hold();
                    TypewriterUtils.confirmInfos(" \n" + getTextBuilder().withFail().withInfo().build()).subscribe(() -> {
                        ServicePlatform.get().getService().hold();
                        ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                        gainCondition(ConditionId.LOST_IN_TIME_AND_SPACE);
                        ServicePlatform.get().getService().release();
                    });
                    ServicePlatform.get().getService().release();
                });
            }

        };

        EncounterTemplate failTemplate = () -> {
            TypewriterUtils.confirmInfos(getTextBuilder().withInfo().build()).subscribe(this::oneGateIsSpawned);
        };



        new VoidBetweenWorldsEncounterTemplate(getTextBuilder(), Stat.LORE, -1, passTemplate, failTemplate).execute();
    }


    private void oneGateIsSpawned() {
        ServicePlatform.get().getService().hold();
        ServicePlatform.get().getEncounterService().finishTypewriterPaper();
        ServicePlatform.get().getService().hideBackground();
        ServicePlatform.get().getGameService().spawnGates(1,1).subscribe();
        ServicePlatform.get().getService().release();
    }
}
