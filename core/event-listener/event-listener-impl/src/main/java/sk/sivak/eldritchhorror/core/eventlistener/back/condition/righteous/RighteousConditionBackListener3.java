package sk.sivak.eldritchhorror.core.eventlistener.back.condition.righteous;

import rx.Single;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionInfo;
import sk.sivak.eldritchhorror.core.constants.displayasset.DisplayAssetResponseType;
import sk.sivak.eldritchhorror.core.constants.displayasset.HideType;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequest;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardResponse;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.back.condition.AbstractConditionBackListener;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;
import sk.sivak.eldritchhorror.core.eventtype.data.SelectSingleGateData;

import java.util.List;

public class RighteousConditionBackListener3 extends AbstractConditionBackListener {

    public RighteousConditionBackListener3(ConditionInfo conditionInfo) {
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
                "[#GOOD]Discard any Conditions[]",
                "[#GOOD]Improve two skills[]").subscribe(choice -> {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().hideTypewriterPaper();
            if (choice == 0) {
                List<ConditionInfo> conditions = ServicePlatform.get().getConditionsDeck().getConditions(getActiveInvestigatorId());
                for (ConditionInfo condition : conditions) {
                    if (condition == getConditionInfo()) {
                        continue;
                    }
                    discardConditionQuestion(condition);
                }
            } else {
                for (int i = 0; i < 2; i++) {
                    ServicePlatform.get().getService().convertTo(Stat.class, () -> null);
                    ServicePlatform.get().getInvestigatorService().improveSkill(null);
                }
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

    private void discardConditionQuestion(ConditionInfo conditionInfo) {
        ShowCardRequest showCardRequest = new ShowCardRequest();
        showCardRequest.setConditionInfo(conditionInfo);
        showCardRequest.setHideType(HideType.DISCARD_ON_YES);
        showCardRequest.setTitle("Discard "+conditionInfo.getName()+" Condition?");
        showCardRequest.setDisplayAssetResponseType(DisplayAssetResponseType.YES_NO);
        Single<ShowCardResponse> displayCondition = ServicePlatform.get().getGameService().showCard(showCardRequest);
        displayCondition.subscribe(response -> {
            if (response == ShowCardResponse.YES) {
                ServicePlatform.get().getService().discardConditionFromInvestigator(getActiveInvestigatorId(), conditionInfo);
            }
        });
    }

}
