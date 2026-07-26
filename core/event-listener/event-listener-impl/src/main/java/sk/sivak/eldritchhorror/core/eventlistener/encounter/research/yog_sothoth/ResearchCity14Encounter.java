package sk.sivak.eldritchhorror.core.eventlistener.encounter.research.yog_sothoth;

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

public class ResearchCity14Encounter extends AbstractYogSothothResearchEncounter {

    public ResearchCity14Encounter() {
        super(14, LocationType.CITY);
    }

    @Override
    protected void execute() {
        new TestPassFlavorInfoFailFlavorInfoTemplate(getTextBuilder(), Stat.INFLUENCE, -1,
                this::gainThisClue,
                this::selectItemToDiscard
        ).withResearchFlavor().execute();
    }

    private void selectItemToDiscard() {
        List<? extends CardInfo> investigatorItems = findInvestigatorItems();
        if (!investigatorItems.isEmpty()) {
            SelectCardData selectCardData = new SelectCardData();
            selectCardData.setAvailableCards(investigatorItems);
            selectCardData.setHideText("Display Inventory?");
            selectCardData.setTitleText("Select Item");
            ServicePlatform.get().getCardService().selectSingleCard(selectCardData).subscribe(EncounterUtils::onSelectCardToDiscard);
        }
    }

    private List<? extends CardInfo> findInvestigatorItems() {
        return EncounterUtils.getPossession(getActiveInvestigatorId(), AssetTrait.ITEM);
    }
}
