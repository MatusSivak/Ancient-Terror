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

public class ResearchWilderness6Encounter extends AbstractCthulhuResearchEncounter {

    public ResearchWilderness6Encounter() {
        super(6, LocationType.WILDERNESS);
    }

    @Override
    protected void execute() {
        new TestPassFlavorInfoFailFlavorInfoTemplate(getTextBuilder(), Stat.OBSERVATION, 0,
                this::gainThisClue,
                () -> {
                    ServicePlatform.get().getService().hold();

                    for (int i = 0; i < 2; i++) {
                        ServicePlatform.get().getService().addEventCommand(in -> {
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
                        });
                    }

                    moveClueToSea();
                    ServicePlatform.get().getService().release();
                }

        ).withTwoFailInfos().withoutFailFlavor().withResearchFlavor().execute();
    }
}
