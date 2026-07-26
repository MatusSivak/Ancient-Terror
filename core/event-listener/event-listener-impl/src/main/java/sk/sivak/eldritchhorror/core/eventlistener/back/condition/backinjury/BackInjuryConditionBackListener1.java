package sk.sivak.eldritchhorror.core.eventlistener.back.condition.backinjury;

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

public class BackInjuryConditionBackListener1 extends AbstractConditionBackListener {

    public BackInjuryConditionBackListener1(ConditionInfo conditionInfo) {
        super(conditionInfo);
    }

    @Override
    public void executeWhole() {
        ShowCardRequest request = new ShowCardRequest();
        request.setHideType(HideType.RETURN);
        request.setConditionInfo(getConditionInfo());
        request.setTitle("Test Strength");
        request.setDisplayAssetResponseType(DisplayAssetResponseType.OK);
        ServicePlatform.get().getGameService().showCard(request).subscribe(this::onTest);
    }

    @Override
    protected void execute() {
        TypewriterUtils.confirmInfos(
                "[#BAD]Lose two Health[]",
                "[#BAD]Lose two Sanity[]").subscribe(() -> {
                    ServicePlatform.get().getService().hold();
                    ServicePlatform.get().getEncounterService().hideTypewriterPaper();
                    ServicePlatform.get().getTokenService().loseHealth(2);
                    ServicePlatform.get().getTokenService().loseSanity(2);
                    ServicePlatform.get().getEncounterService().showTypewriterPaper(false);
                    ServicePlatform.get().getEncounterService().typeFlavor(" \nIn the morning, you discover your back has been scarred with ancient hieroglyphs.");
                    TypewriterUtils.displayTestConditionButton(Stat.LORE, -1, getConditionInfo(), () -> {
                        TypewriterUtils.confirmInfos(
                                "[#GOOD]Gain one Clue[]",
                                "[#GOOD]Discard this card[]").subscribe(() -> {
                            ServicePlatform.get().getService().hold();
                            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                            ServicePlatform.get().getTokenService().gainClueFromPool();
                            discardThisCard();
                            ServicePlatform.get().getService().release();
                        });
                    },() -> {
                        TypewriterUtils.confirmInfos(
                                "[#GOOD]Discard this card[]").subscribe(() -> {
                            ServicePlatform.get().getService().hold();
                            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                            discardThisCard();
                            ServicePlatform.get().getService().release();
                        });
                    });
                    ServicePlatform.get().getService().release();
        });
    }

    private void onTest(ShowCardResponse showCardResponse) {
        ServicePlatform.get().getTestService().test(Stat.STRENGTH, 0, 1,
                new TestFlavorRequest(TestFlavorType.CONDITION, getConditionInfo())).subscribe(testResult -> {
            if (!testResult.isSuccessful()) {
                onFlip(null);
            }
        });
    }

    private void discardThisCard() {
        ShowCardRequest showCardRequest = new ShowCardRequest();
        showCardRequest.setConditionInfo(getConditionInfo());
        showCardRequest.setHideType(HideType.DISCARD_ALWAYS);
        showCardRequest.setTitle("Discard Back Injury Condition");
        showCardRequest.setDisplayAssetResponseType(DisplayAssetResponseType.OK);
        Single<ShowCardResponse> displayCondition = ServicePlatform.get().getGameService().showCard(showCardRequest);
        displayCondition.subscribe(response -> {
            ServicePlatform.get().getService().discardConditionFromInvestigator(getActiveInvestigatorId(), getConditionInfo());
        });
    }
}
