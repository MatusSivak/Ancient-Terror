package sk.sivak.eldritchhorror.core.eventlistener.encounter.research.yog_sothoth;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;
import sk.sivak.eldritchhorror.core.eventtype.SelectCardData;

import static sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils.getActiveInvestigatorId;

public class ResearchSea9Encounter extends AbstractYogSothothResearchEncounter {

    public ResearchSea9Encounter() {
        super(9, LocationType.SEA);
    }

    @Override
    protected void execute() {
        TypewriterUtils.noYesQuestion(getTextBuilder().withQuestion().build(), () -> {
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
        }, () -> {
            if (ServicePlatform.get().getSpellsDeck().getSpells(getActiveInvestigatorId()).isEmpty()) {
                TypewriterUtils.confirmInfos("You don't have any Spells.").subscribe(() -> {
                    ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                });
            } else {
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getEncounterService().finishTypewriterPaper();

                SelectCardData selectCardData = new SelectCardData();
                selectCardData.setAvailableCards(ServicePlatform.get().getSpellsDeck().getSpells(getActiveInvestigatorId()));
                selectCardData.setHideText("Display Spells?");
                selectCardData.setTitleText("Select Spell");
                ServicePlatform.get().getCardService().selectSingleCard(selectCardData).subscribe(spell -> {
                    ServicePlatform.get().getService().hold();
                    EncounterUtils.onSelectCardToDiscardByChoice(spell);
                    ServicePlatform.get().getService().hideBackground();
                    gainThisClue();
                    ServicePlatform.get().getService().release();
                });

                ServicePlatform.get().getService().release();
            }
        });
    }
}
