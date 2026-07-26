package sk.sivak.eldritchhorror.core.eventlistener.back.condition.righteous;

import rx.Single;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionInfo;
import sk.sivak.eldritchhorror.core.constants.displayasset.DisplayAssetResponseType;
import sk.sivak.eldritchhorror.core.constants.displayasset.HideType;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequest;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardResponse;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.back.condition.AbstractConditionBackListener;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;

public class RighteousConditionBackListener1 extends AbstractConditionBackListener {

    public RighteousConditionBackListener1(ConditionInfo conditionInfo) {
        super(conditionInfo);
    }

    @Override
    public void executeWhole() {
        ShowCardRequest request = new ShowCardRequest();
        request.setHideType(HideType.RETURN);
        request.setConditionInfo(getConditionInfo());
        request.setTitle("Flip " + getConditionInfo().getName() +" Condition?");
        request.setDisplayAssetResponseType(DisplayAssetResponseType.YES_NO);
        ServicePlatform.get().getGameService().showCard(request).subscribe(this::onAnswer);
    }

    private void onAnswer(ShowCardResponse showCardResponse) {
        if (showCardResponse != ShowCardResponse.YES) {
            return;
        }
        onFlip(showCardResponse);
    }

    @Override
    protected void execute() {
        ServicePlatform.get().getEncounterService().typeInfo(TypewriterUtils.SELECT_REWARD);
        ServicePlatform.get().getEncounterService().displayButtons(
                "[#GOOD]Advance the active Mystery[]",
                "[#GOOD]Gain two Clues[]").subscribe(choice -> {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().hideTypewriterPaper();
            if (choice == 0) {
                ServicePlatform.get().getGameService().advanceActiveMystery();
            } else {
                ServicePlatform.get().getTokenService().gainClueFromPool();
                ServicePlatform.get().getTokenService().gainClueFromPool();
            }
            ServicePlatform.get().getEncounterService().showTypewriterPaper(false);
            TypewriterUtils.confirmInfos("[#BAD]Discard this card[]").subscribe(() -> {
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                discardThisCard();
                ServicePlatform.get().getService().release();
            });
            ServicePlatform.get().getService().release();
        });
    }

    private void discardThisCard() {
        ShowCardRequest showCardRequest = new ShowCardRequest();
        showCardRequest.setConditionInfo(getConditionInfo());
        showCardRequest.setHideType(HideType.DISCARD_ALWAYS);
        showCardRequest.setTitle("Discard Righteous Condition");
        showCardRequest.setDisplayAssetResponseType(DisplayAssetResponseType.OK);
        Single<ShowCardResponse> displayCondition = ServicePlatform.get().getGameService().showCard(showCardRequest);
        displayCondition.subscribe(response -> {
            ServicePlatform.get().getService().discardConditionFromInvestigator(getActiveInvestigatorId(), getConditionInfo());
        });
    }
}
