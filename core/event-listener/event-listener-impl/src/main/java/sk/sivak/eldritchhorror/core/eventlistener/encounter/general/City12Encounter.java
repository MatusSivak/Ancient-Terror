package sk.sivak.eldritchhorror.core.eventlistener.encounter.general;

import java8.features.function.Predicate;
import java8.features.stream.Stream;
import sk.sivak.eldritchhorror.core.constants.asset.AssetInfo;
import sk.sivak.eldritchhorror.core.constants.asset.AssetTrait;
import sk.sivak.eldritchhorror.core.constants.card.CardInfo;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;
import sk.sivak.eldritchhorror.core.eventtype.SelectCardData;

import java.util.List;

public class City12Encounter extends AbstractGeneralEncounter {

    public City12Encounter() {
        super(12, LocationType.CITY);
    }

    @Override
    protected void execute() {
        TypewriterUtils.displayTestButton(Stat.INFLUENCE, this::onSuccess, this::onFail);
    }

    private void onFail() {
        finishAfterFlavor(getTextBuilder().withFail().withFlavor().build());
    }

    private void onSuccess() {
        chooseReserveOrDeckReward(!findServicesInReserve().isEmpty(), this::selectFromReserve, this::gainFromDeck);
    }

    private void selectFromReserve() {
        SelectCardData selectCardData = new SelectCardData();
        selectCardData.setAvailableCards(findServicesInReserve());
        selectCardData.setHideText("Display Reserve?");
        selectCardData.setTitleText("Select Service");
        ServicePlatform.get().getCardService().selectSingleCard(selectCardData).subscribe(this::onSelectCardFromReserve);
    }

    private void gainFromDeck() {
        ServicePlatform.get().getService().hideBackground();
        ServicePlatform.get().getGameService().gainAsset(ServicePlatform.get().getAssetDeck().findFirst(AssetTrait.SERVICE));
    }

    private void onSelectCardFromReserve(CardInfo cardInfo) {
        ServicePlatform.get().getService().hold();
        ServicePlatform.get().getService().hideBackground();
        ServicePlatform.get().getGameService().gainAsset((AssetInfo) cardInfo);
        ServicePlatform.get().getCardService().refillReserve();
        ServicePlatform.get().getService().release();
    }

    private List<AssetInfo> findServicesInReserve() {
        Predicate<AssetInfo> predicate = assetInfo -> assetInfo.getTraits().contains(AssetTrait.SERVICE);
        return Stream.collectToList(ServicePlatform.get().getAssetDeck().getReserve(), predicate);
    }
}
