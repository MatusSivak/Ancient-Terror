package sk.sivak.eldritchhorror.core.eventlistener.encounter.location;

import java8.features.stream.Stream;
import sk.sivak.eldritchhorror.core.constants.artifact.ArtifactInfo;
import sk.sivak.eldritchhorror.core.constants.asset.AssetInfo;
import sk.sivak.eldritchhorror.core.constants.asset.AssetTrait;
import sk.sivak.eldritchhorror.core.constants.card.CardInfo;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils;
import sk.sivak.eldritchhorror.core.eventtype.SelectCardData;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.LocationEncounter;

import java.util.LinkedList;
import java.util.List;

import static sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils.getActiveInvestigatorId;

public class Shanghai9Encounter extends AbstractLocationEncounter{

    public Shanghai9Encounter() {
        super(9, LocationEncounter.LocationEncounterType.SHANGHAI);
    }

    @Override
    protected void execute() {
        new TestPassFlavorInfoFailFlavorInfoTemplate(getTextBuilder(), Stat.INFLUENCE, 0,
                () -> ServicePlatform.get().getInvestigatorService().improveSkill(Stat.LORE),
                this::onFail)
                .execute();
    }

    private void onFail() {
        List<ArtifactInfo> artifacts = ServicePlatform.get().getArtifactsDeck().getArtifacts(getActiveInvestigatorId());
        List<AssetInfo> assets = ServicePlatform.get().getAssetDeck().getAssets(getActiveInvestigatorId());

        List<? extends CardInfo> artifactsAndTrinkets = new LinkedList<>(artifacts);
        artifactsAndTrinkets.addAll(Stream.collectToList(assets, assetInfo -> assetInfo.getTraits().contains(AssetTrait.TRINKET)));

        if (!artifactsAndTrinkets.isEmpty()) {
            selectItemToDiscard(artifactsAndTrinkets);
        }
    }

    private void selectItemToDiscard(List<? extends CardInfo> investigatorItems) {
        SelectCardData selectCardData = new SelectCardData();
        selectCardData.setAvailableCards(investigatorItems);
        selectCardData.setHideText("Display Inventory?");
        selectCardData.setTitleText("Select Trinket or Artifact");
        ServicePlatform.get().getCardService().selectSingleCard(selectCardData).subscribe(EncounterUtils::onSelectCardToDiscard);
    }
}
