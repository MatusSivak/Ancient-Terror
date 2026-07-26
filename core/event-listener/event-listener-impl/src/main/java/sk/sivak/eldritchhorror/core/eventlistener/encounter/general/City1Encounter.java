package sk.sivak.eldritchhorror.core.eventlistener.encounter.general;

import java8.features.function.Predicate;
import java8.features.stream.Stream;
import sk.sivak.eldritchhorror.core.constants.asset.AssetInfo;
import sk.sivak.eldritchhorror.core.constants.asset.AssetTrait;
import sk.sivak.eldritchhorror.core.constants.card.CardInfo;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;
import sk.sivak.eldritchhorror.core.eventtype.SelectCardData;

import java.util.List;

public class City1Encounter extends AbstractGeneralEncounter {

    public City1Encounter() {
        super(1, LocationType.CITY);
    }

    @Override
    protected void execute() {
        TypewriterUtils.displayTestButton(Stat.OBSERVATION, this::onSuccess, this::onFail);
    }

    private void onSuccess() {
        if (findItemsInReserve().isEmpty()) {
            onSuccessWithNoItemsInReserve();
        } else {
            onSuccessWithItemsInReserve();
        }
    }

    private void onSuccessWithItemsInReserve() {
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

    private void onFail() {
        ServicePlatform.get().getEncounterService().typeFlavor(getTextBuilder().withFail().withFlavor().build());
        TypewriterUtils.confirmInfos(getTextBuilder().withFail().withInfo().build()).subscribe(() -> {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            ServicePlatform.get().getGameService().gainCondition(ConditionId.DETAINED);
            ServicePlatform.get().getService().release();
        });
    }
}
