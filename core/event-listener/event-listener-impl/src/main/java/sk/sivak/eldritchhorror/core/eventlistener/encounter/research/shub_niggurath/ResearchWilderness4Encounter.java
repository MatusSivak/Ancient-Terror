package sk.sivak.eldritchhorror.core.eventlistener.encounter.research.shub_niggurath;

import sk.sivak.eldritchhorror.core.constants.asset.AssetTrait;
import sk.sivak.eldritchhorror.core.constants.card.CardInfo;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils;
import sk.sivak.eldritchhorror.core.eventtype.SelectCardData;

import java.util.List;

import static sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils.getActiveInvestigatorId;

public class ResearchWilderness4Encounter extends AbstractShubNiggurathResearchEncounter{

    public ResearchWilderness4Encounter() {
        super(4, LocationType.WILDERNESS);
    }

    @Override
    protected void execute() {
        new TestPassFlavorInfoFailFlavorInfoTemplate(getTextBuilder(), Stat.OBSERVATION, -1, () -> {
            ServicePlatform.get().getService().hold();
            gainThisClue();
            ServicePlatform.get().getTokenService().gainClueFromPool();
            ServicePlatform.get().getService().release();
        }, () -> {
            ServicePlatform.get().getService().hold();
            List<? extends CardInfo> investigatorAllies = findInvestigatorAllies();
            if (!investigatorAllies.isEmpty()) {
                selectAllyToDiscard(investigatorAllies);
            }
            ServicePlatform.get().getTokenService().loseSanity(2);
            ServicePlatform.get().getService().release();
        }).withResearchFlavor().withTwoPassInfos().withTwoFailInfos().execute();
    }

    private List<? extends CardInfo> findInvestigatorAllies() {
        return EncounterUtils.getPossession(getActiveInvestigatorId(), AssetTrait.ALLY);
    }

    private void selectAllyToDiscard(List<? extends CardInfo> investigatorAllies) {
        SelectCardData selectCardData = new SelectCardData();
        selectCardData.setAvailableCards(investigatorAllies);
        selectCardData.setHideText("Display Inventory?");
        selectCardData.setTitleText("Select Ally");
        ServicePlatform.get().getCardService().selectSingleCard(selectCardData).subscribe(EncounterUtils::onSelectCardToDiscard);
    }
}
