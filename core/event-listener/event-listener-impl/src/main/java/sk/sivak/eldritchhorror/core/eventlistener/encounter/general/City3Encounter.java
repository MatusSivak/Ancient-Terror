package sk.sivak.eldritchhorror.core.eventlistener.encounter.general;

import java8.features.function.Predicate;
import java8.features.stream.Stream;
import sk.sivak.eldritchhorror.core.constants.asset.AssetInfo;
import sk.sivak.eldritchhorror.core.constants.asset.AssetTrait;
import sk.sivak.eldritchhorror.core.constants.card.CardInfo;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;
import sk.sivak.eldritchhorror.core.eventtype.SelectCardData;

import java.util.List;

import static sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils.getActiveInvestigatorId;

public class City3Encounter extends AbstractGeneralEncounter {

    public City3Encounter() {
        super(3, LocationType.CITY);
    }

    @Override
    protected void execute() {
        TypewriterUtils.displayTestButton(Stat.STRENGTH, this::onSuccess, this::onFail);
    }

    private void onSuccess() {
        if (findItemsInReserve().isEmpty()) {
            onSuccessWithNoItemsInReserve();
        } else {
            onSuccessWithItemsInReserve();
        }
    }

    private void onSuccessWithItemsInReserve() {
        ServicePlatform.get().getEncounterService().typeFlavor(getTextBuilder().withPass().withFlavor().build());
        ServicePlatform.get().getEncounterService().typeInfo(TypewriterUtils.SELECT_REWARD);
        ServicePlatform.get().getEncounterService().displayButtons(
                getTextBuilder().withPass().withOption(1).build(),
                getTextBuilder().withPass().withOption(2).build()
        ).subscribe(x -> {
            ServicePlatform.get().getEncounterService().hold();
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            if (x == 0) {
                // From reserve
                List<AssetInfo> itemsInReserve = findItemsInReserve();
                SelectCardData selectCardData = new SelectCardData();
                selectCardData.setAvailableCards(itemsInReserve);
                selectCardData.setHideText("Display Reserve?");
                selectCardData.setTitleText("Select Item");
                ServicePlatform.get().getCardService().selectSingleCard(selectCardData).subscribe(this::onSelectCardFromReserve);
            } else {
                // From the Deck
                ServicePlatform.get().getGameService().gainAsset(ServicePlatform.get().getAssetDeck().findFirst(AssetTrait.ITEM));
            }
            ServicePlatform.get().getEncounterService().release();
        });
    }

    private void onSelectCardFromReserve(CardInfo cardInfo) {
        ServicePlatform.get().getService().hold();
        ServicePlatform.get().getGameService().gainAsset((AssetInfo) cardInfo);
        ServicePlatform.get().getCardService().refillReserve();
        ServicePlatform.get().getService().release();
    }

    private void onSuccessWithNoItemsInReserve() {
        // From the Deck
        ServicePlatform.get().getEncounterService().typeFlavor(getTextBuilder().withPass().withFlavor().build());
        TypewriterUtils.confirmInfos(getTextBuilder().withPass().withOption(2).build()).subscribe(() -> {
            ServicePlatform.get().getEncounterService().hold();
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            ServicePlatform.get().getGameService().gainAsset(ServicePlatform.get().getAssetDeck().findFirst(AssetTrait.ITEM));
            ServicePlatform.get().getEncounterService().release();
        });
    }

    private List<AssetInfo> findItemsInReserve() {
        Predicate<AssetInfo> predicate = assetInfo -> assetInfo.getTraits().contains(AssetTrait.ITEM);
        return Stream.collectToList(ServicePlatform.get().getAssetDeck().getReserve(), predicate);
    }

    private List<? extends CardInfo> findInvestigatorItems() {
        return EncounterUtils.getPossession(getActiveInvestigatorId(), AssetTrait.ITEM);
    }

    private void onFail() {
        TypewriterUtils.confirmInfos(
                getTextBuilder().withFail().withInfo(1).build(),
                getTextBuilder().withFail().withInfo(2).build()
                ).subscribe(() -> {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            ServicePlatform.get().getTokenService().loseHealth(1);

            List<? extends CardInfo> investigatorItems = findInvestigatorItems();
            if (!investigatorItems.isEmpty()) {
                SelectCardData selectCardData = new SelectCardData();
                selectCardData.setAvailableCards(investigatorItems);
                selectCardData.setHideText("Display Inventory?");
                selectCardData.setTitleText("Select Item");
                ServicePlatform.get().getCardService().selectSingleCard(selectCardData).subscribe(EncounterUtils::onSelectCardToDiscard);
            }

            ServicePlatform.get().getService().release();
        });
    }
}
