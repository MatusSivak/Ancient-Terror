package sk.sivak.eldritchhorror.core.eventlistener.back.condition.poisoned;

import rx.Single;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionInfo;
import sk.sivak.eldritchhorror.core.constants.displayasset.DisplayAssetResponseType;
import sk.sivak.eldritchhorror.core.constants.displayasset.HideType;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequest;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardResponse;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.back.condition.AbstractConditionBackListener;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;

public class PoisonedConditionBackListener1 extends AbstractConditionBackListener{

    public PoisonedConditionBackListener1(ConditionInfo conditionInfo) {
        super(conditionInfo);
    }

    @Override
    protected void execute() {
        if (isOnCity()) {
            TypewriterUtils.displayTestConditionButton(Stat.INFLUENCE, -1, getConditionInfo(), this::onSuccess, this::onFail);
        } else {
            TypewriterUtils.confirmInfos("[#BAD]You are not in the City[]").subscribe(() -> {
                ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            });
        }
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
        if (ServicePlatform.get().getConditionsDeck().hasCondition(getActiveInvestigatorId(), ConditionId.DEBT)) {
            TypewriterUtils.confirmInfos("[#BAD]You cannot afford the antidote[]").subscribe(() -> {
                ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            });
        } else {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().typeFlavor("You cannot afford the antidote");
            TypewriterUtils.noYesQuestion("[#BAD]Gain a Debt Condition[] to [#GOOD]Discard this card?[]", this::onNotGainDebt, this::onGainDebt);
            ServicePlatform.get().getService().release();
        }

    }

    private void onGainDebt() {
        ServicePlatform.get().getService().hold();
        ServicePlatform.get().getEncounterService().finishTypewriterPaper();
        ServicePlatform.get().getGameService().gainCondition(ConditionId.DEBT);
        discardThisCard();
        ServicePlatform.get().getService().release();
    }

    private void onNotGainDebt() {
        ServicePlatform.get().getEncounterService().finishTypewriterPaper();
    }

    private boolean isOnCity() {
        return ServicePlatform.get().getLocationMap().getLocationInfo(getActiveInvestigator().getCurrentLocationId()).getLocationType() == LocationType.CITY;
    }
}
