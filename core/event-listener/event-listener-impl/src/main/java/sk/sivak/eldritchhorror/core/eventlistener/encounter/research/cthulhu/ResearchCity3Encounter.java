package sk.sivak.eldritchhorror.core.eventlistener.encounter.research.cthulhu;

import java8.features.stream.Stream;
import sk.sivak.eldritchhorror.core.constants.asset.AssetInfo;
import sk.sivak.eldritchhorror.core.constants.asset.AssetTrait;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils;
import sk.sivak.eldritchhorror.core.eventtype.SelectCardData;

import java.util.List;

import static sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils.getActiveInvestigatorId;

public class ResearchCity3Encounter extends AbstractCthulhuResearchEncounter {

    public ResearchCity3Encounter() {
        super(3, LocationType.CITY);
    }

    @Override
    protected void execute() {
        new TestPassFlavorInfoFailFlavorInfoTemplate(getTextBuilder(), Stat.OBSERVATION, -1,
                () -> {
                    ServicePlatform.get().getService().hold();
                    gainThisClue();
                    ServicePlatform.get().getTokenService().gainClueFromPool();
                    ServicePlatform.get().getService().release();
                },
                () -> {
                    List<AssetInfo> items = Stream.collectToList(ServicePlatform.get().getAssetDeck().getAssets(getActiveInvestigatorId()),
                            assetInfo -> assetInfo.getTraits().contains(AssetTrait.ITEM));
                    if (items.isEmpty()) {
                        return;
                    }
                    SelectCardData selectCardData = new SelectCardData();
                    selectCardData.setAvailableCards(items);
                    selectCardData.setHideText("Display Inventory?");
                    selectCardData.setTitleText("Select Item");
                    ServicePlatform.get().getCardService().selectSingleCard(selectCardData).subscribe(EncounterUtils::onSelectCardToDiscard);
                }
        ).withResearchFlavor().withTwoPassInfos().execute();
    }
}
