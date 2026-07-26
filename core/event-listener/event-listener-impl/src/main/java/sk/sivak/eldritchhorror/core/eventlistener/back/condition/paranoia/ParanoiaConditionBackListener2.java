package sk.sivak.eldritchhorror.core.eventlistener.back.condition.paranoia;

import rx.Single;
import sk.sivak.eldritchhorror.core.constants.asset.AssetInfo;
import sk.sivak.eldritchhorror.core.constants.asset.AssetTrait;
import sk.sivak.eldritchhorror.core.constants.card.CardInfo;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionInfo;
import sk.sivak.eldritchhorror.core.constants.displayasset.DisplayAssetResponseType;
import sk.sivak.eldritchhorror.core.constants.displayasset.HideType;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequest;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardResponse;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.test.TestFlavorType;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.back.condition.AbstractConditionBackListener;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;
import sk.sivak.eldritchhorror.core.eventtype.SelectCardData;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestFlavorRequest;
import sk.sivak.eldritchhorror.core.model.InvestigatorRead;

import java.util.List;

public class ParanoiaConditionBackListener2 extends AbstractConditionBackListener {

    public ParanoiaConditionBackListener2(ConditionInfo conditionInfo) {
        super(conditionInfo);
    }

    @Override
    public void executeWhole() {
        ShowCardRequest request = new ShowCardRequest();
        request.setHideType(HideType.RETURN);
        request.setConditionInfo(getConditionInfo());
        request.setTitle("Test Will");
        request.setDisplayAssetResponseType(DisplayAssetResponseType.OK);
        ServicePlatform.get().getGameService().showCard(request).subscribe(this::onTest);
    }

    private void onTest(ShowCardResponse showCardResponse) {
        ServicePlatform.get().getTestService().test(Stat.WILL, 0, 1,
                new TestFlavorRequest(TestFlavorType.CONDITION, getConditionInfo())).subscribe(testResult -> {
            if (!testResult.isSuccessful()) {
                onFlip(null);
            }
        });
    }

    @Override
    protected void execute() {
        TypewriterUtils.confirmInfos("[#GOOD]Gain one random Item from the deck[]").subscribe(() -> {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().hideTypewriterPaper();
            AssetInfo randomItem = ServicePlatform.get().getAssetDeck().findFirst(AssetTrait.ITEM);
            ServicePlatform.get().getGameService().gainAsset(randomItem);
            ServicePlatform.get().getEncounterService().showTypewriterPaper(false);
            ServicePlatform.get().getEncounterService().typeFlavor("You must hide from the authorities.");
            ServicePlatform.get().getEncounterService().typeInfo("Roll at least "+randomItem.getCost()+" success(es).");
            TypewriterUtils.displayTestConditionButton(Stat.OBSERVATION, getConditionInfo(), testData -> {
                if (testData.getScore() < randomItem.getCost()) {
                    TypewriterUtils.confirmInfos(
                            "[#BAD]Discard gained Item[]",
                            "[#BAD]Gain a Detained Condition[]",
                            "[#GOOD]Discard this card[]").subscribe(() -> onTestFailed(randomItem));
                } else {
                    TypewriterUtils.confirmInfos(
                            "[#GOOD]Discard this card[]").subscribe(this::onTestPassed);
                }
            });
            ServicePlatform.get().getService().release();
        });
    }

    private void onTestPassed() {
        ServicePlatform.get().getService().hold();
        ServicePlatform.get().getEncounterService().finishTypewriterPaper();
        discardThisCard();
        ServicePlatform.get().getService().release();
    }

    private void onTestFailed(AssetInfo randomItem) {
        ServicePlatform.get().getService().hold();
        ServicePlatform.get().getEncounterService().finishTypewriterPaper();
        discardGainedCard(randomItem);
        ServicePlatform.get().getGameService().gainCondition(ConditionId.DETAINED);
        discardThisCard();
        ServicePlatform.get().getService().release();
    }

    private void discardGainedCard(AssetInfo assetInfo) {
        ShowCardRequest showCardRequest = new ShowCardRequest();
        showCardRequest.setAssetInfo(assetInfo);
        showCardRequest.setHideType(HideType.DISCARD_ALWAYS);
        showCardRequest.setTitle("Discard "+assetInfo.getName());
        showCardRequest.setDisplayAssetResponseType(DisplayAssetResponseType.OK);
        Single<ShowCardResponse> displayCondition = ServicePlatform.get().getGameService().showCard(showCardRequest);
        displayCondition.subscribe(response -> {
            ServicePlatform.get().getService().discardAssetFromInvestigator(getActiveInvestigatorId(), assetInfo, true);
        });
    }

    private void discardThisCard() {
        ShowCardRequest showCardRequest = new ShowCardRequest();
        showCardRequest.setConditionInfo(getConditionInfo());
        showCardRequest.setHideType(HideType.DISCARD_ALWAYS);
        showCardRequest.setTitle("Discard Paranoia Condition");
        showCardRequest.setDisplayAssetResponseType(DisplayAssetResponseType.OK);
        Single<ShowCardResponse> displayCondition = ServicePlatform.get().getGameService().showCard(showCardRequest);
        displayCondition.subscribe(response -> {
            ServicePlatform.get().getService().discardConditionFromInvestigator(getActiveInvestigatorId(), getConditionInfo());
        });
    }

}
