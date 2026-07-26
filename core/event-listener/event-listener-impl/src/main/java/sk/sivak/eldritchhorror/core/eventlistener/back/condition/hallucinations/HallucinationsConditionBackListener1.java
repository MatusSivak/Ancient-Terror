package sk.sivak.eldritchhorror.core.eventlistener.back.condition.hallucinations;

import rx.Single;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionInfo;
import sk.sivak.eldritchhorror.core.constants.displayasset.DisplayAssetResponseType;
import sk.sivak.eldritchhorror.core.constants.displayasset.HideType;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequest;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardResponse;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.test.TestFlavorType;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.back.condition.AbstractConditionBackListener;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestFlavorRequest;

public class HallucinationsConditionBackListener1 extends AbstractConditionBackListener {

    public HallucinationsConditionBackListener1(ConditionInfo conditionInfo) {
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
        TypewriterUtils.confirmInfos(
                "[#BAD]Discard all of your Clues[]",
                "[#GOOD]Discard this card[]").subscribe(() -> {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            for (int i = 0; i < ServicePlatform.get().getCluePool().getClueCount(getActiveInvestigatorId()); i++) {
                ServicePlatform.get().getTokenService().loseClue();
            }
            discardThisCard();
            ServicePlatform.get().getService().release();
        });
    }

    private void discardThisCard() {
        ShowCardRequest showCardRequest = new ShowCardRequest();
        showCardRequest.setConditionInfo(getConditionInfo());
        showCardRequest.setHideType(HideType.DISCARD_ALWAYS);
        showCardRequest.setTitle("Discard Hallucinations Condition");
        showCardRequest.setDisplayAssetResponseType(DisplayAssetResponseType.OK);
        Single<ShowCardResponse> displayCondition = ServicePlatform.get().getGameService().showCard(showCardRequest);
        displayCondition.subscribe(response -> {
            ServicePlatform.get().getService().discardConditionFromInvestigator(getActiveInvestigatorId(), getConditionInfo());
        });
    }

}
