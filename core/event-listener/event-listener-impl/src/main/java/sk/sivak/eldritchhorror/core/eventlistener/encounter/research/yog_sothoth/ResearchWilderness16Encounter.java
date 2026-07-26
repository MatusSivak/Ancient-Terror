package sk.sivak.eldritchhorror.core.eventlistener.encounter.research.yog_sothoth;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;
import sk.sivak.eldritchhorror.core.eventtype.SelectCardData;

import static sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils.getActiveInvestigatorId;

public class ResearchWilderness16Encounter extends AbstractYogSothothResearchEncounter {

    public ResearchWilderness16Encounter() {
        super(16, LocationType.WILDERNESS);
    }

    @Override
    protected void execute() {
        if (!ServicePlatform.get().getSpellsDeck().getSpells(getActiveInvestigatorId()).isEmpty()) {
            TypewriterUtils.noYesQuestion("[#BAD]Discard one Spell[] to\n[#GOOD]roll two additional dice?[]", () -> {
                new TestFailFlavorInfoTemplate(getTextBuilder(), Stat.OBSERVATION, -1, () -> {
                    ServicePlatform.get().getTokenService().loseSanity(2);
                }).withResearchFlavor().execute();
            }, () -> {
                ServicePlatform.get().getService().hold();

                ServicePlatform.get().getEncounterService().hideTypewriterPaper();

                SelectCardData selectCardData = new SelectCardData();
                selectCardData.setAvailableCards(ServicePlatform.get().getSpellsDeck().getSpells(getActiveInvestigatorId()));
                selectCardData.setHideText("Display Spells?");
                selectCardData.setTitleText("Select Spell");
                ServicePlatform.get().getCardService().selectSingleCard(selectCardData).subscribe(selectedCard-> {
                    ServicePlatform.get().getService().hold();
                    EncounterUtils.onSelectCardToDiscardByChoice(selectedCard);
                    ServicePlatform.get().getEncounterService().showTypewriterPaper(false);

                    new TestFailFlavorInfoTemplate(getTextBuilder(), Stat.OBSERVATION, +1, () -> {
                        ServicePlatform.get().getTokenService().loseSanity(2);
                    }).withResearchFlavor().execute();

                    ServicePlatform.get().getService().release();
                });

                ServicePlatform.get().getService().release();
            });
        } else {
            new TestFailFlavorInfoTemplate(getTextBuilder(), Stat.OBSERVATION, -1, () -> {
                ServicePlatform.get().getTokenService().loseSanity(2);
            }).withResearchFlavor().execute();
        }

    }


}
