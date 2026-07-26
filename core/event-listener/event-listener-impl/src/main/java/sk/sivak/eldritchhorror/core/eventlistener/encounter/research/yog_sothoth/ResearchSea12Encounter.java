package sk.sivak.eldritchhorror.core.eventlistener.encounter.research.yog_sothoth;

import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils;
import sk.sivak.eldritchhorror.core.eventtype.SelectCardData;

import static sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils.getActiveInvestigatorId;

public class ResearchSea12Encounter extends AbstractYogSothothResearchEncounter {

    public ResearchSea12Encounter() {
        super(12, LocationType.SEA);
    }

    @Override
    protected void execute() {
        new TestPassFlavorInfoFailFlavorInfoTemplate(getTextBuilder(), Stat.OBSERVATION, -1,
                this::gainThisClue, () -> {
            ServicePlatform.get().getService().hold();
            discardThisClue();
            discardOneSpell();
            ServicePlatform.get().getService().release();
        }).withTwoFailInfos().withResearchFlavor().execute();
    }

    private void discardThisClue() {
        ServicePlatform.get().getService().hold();
        ServicePlatform.get().getService().hideBackground();
        ServicePlatform.get().getTokenService().discardClue(getLocationId());
        ServicePlatform.get().getService().release();
    }

    private void discardOneSpell() {
        if (!ServicePlatform.get().getSpellsDeck().getSpells(getActiveInvestigatorId()).isEmpty()) {
            SelectCardData selectCardData = new SelectCardData();
            selectCardData.setAvailableCards(ServicePlatform.get().getSpellsDeck().getSpells(getActiveInvestigatorId()));
            selectCardData.setHideText("Display Spells?");
            selectCardData.setTitleText("Select Spell");
            ServicePlatform.get().getCardService().selectSingleCard(selectCardData).subscribe(EncounterUtils::onSelectCardToDiscardByChoice);
        }
    }
}
