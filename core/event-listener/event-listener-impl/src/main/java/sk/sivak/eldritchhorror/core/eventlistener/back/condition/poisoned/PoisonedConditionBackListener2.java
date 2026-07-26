package sk.sivak.eldritchhorror.core.eventlistener.back.condition.poisoned;

import java8.features.function.Predicate;
import java8.features.stream.Stream;
import rx.Single;
import sk.sivak.eldritchhorror.core.constants.asset.AssetInfo;
import sk.sivak.eldritchhorror.core.constants.asset.AssetTrait;
import sk.sivak.eldritchhorror.core.constants.card.CardInfo;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionInfo;
import sk.sivak.eldritchhorror.core.constants.displayasset.DisplayAssetResponseType;
import sk.sivak.eldritchhorror.core.constants.displayasset.HideType;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequest;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardResponse;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.back.condition.AbstractConditionBackListener;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;
import sk.sivak.eldritchhorror.core.eventtype.SelectCardData;

import java.util.List;

public class PoisonedConditionBackListener2 extends AbstractConditionBackListener{

    public PoisonedConditionBackListener2(ConditionInfo conditionInfo) {
        super(conditionInfo);
    }

    @Override
    protected void execute() {
        TypewriterUtils.displayTestConditionButton(Stat.STRENGTH, getConditionInfo(), this::onSuccess, this::onFail);
    }

    private void onSuccess() {
        TypewriterUtils.confirmInfos("[#GOOD]Discard this card[]").subscribe(this::onConfirmSuccessResult);
    }

    private void onConfirmSuccessResult() {
        ServicePlatform.get().getEncounterService().hold();
        ServicePlatform.get().getEncounterService().finishTypewriterPaper();
        discardThisCard();
        ServicePlatform.get().getEncounterService().release();
    }

    private void discardThisCard() {
        ShowCardRequest showCardRequest = new ShowCardRequest();
        showCardRequest.setConditionInfo(getConditionInfo());
        showCardRequest.setHideType(HideType.DISCARD_ALWAYS);
        showCardRequest.setTitle("Discard Poisoned Condition");
        showCardRequest.setDisplayAssetResponseType(DisplayAssetResponseType.OK);
        Single<ShowCardResponse> displayCondition = ServicePlatform.get().getGameService().showCard(showCardRequest);
        displayCondition.subscribe(response -> {
            ServicePlatform.get().getService().discardConditionFromInvestigator(getActiveInvestigatorId(), getConditionInfo());
        });
    }

    private void onFail() {
        ServicePlatform.get().getService().hold();
        ServicePlatform.get().getEncounterService().typeFlavor("Those around you fear the illness to be contagious.");
        TypewriterUtils.confirmInfos("[#BAD]Discard one Ally[]").subscribe(() -> {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            List<AssetInfo> investigatorAllies = findInvestigatorAllies();
            if (!investigatorAllies.isEmpty()) {
                SelectCardData selectCardData = new SelectCardData();
                selectCardData.setAvailableCards(investigatorAllies);
                selectCardData.setHideText("Display Inventory?");
                selectCardData.setTitleText("Select Ally");
                ServicePlatform.get().getCardService().selectSingleCard(selectCardData).subscribe(EncounterUtils::onSelectCardToDiscard);
            }
            ServicePlatform.get().getService().release();
        });
        ServicePlatform.get().getService().release();
    }

    private List<AssetInfo> findInvestigatorAllies() {
        Predicate<AssetInfo> predicate = assetInfo -> assetInfo.getTraits().contains(AssetTrait.ALLY);
        List<AssetInfo> assets = ServicePlatform.get().getAssetDeck().getAssets(getActiveInvestigatorId());
        return Stream.collectToList(assets, predicate);
    }
}
