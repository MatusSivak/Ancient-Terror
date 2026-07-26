package sk.sivak.eldritchhorror.core.eventlistener.back.condition.debt;

import rx.Single;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionInfo;
import sk.sivak.eldritchhorror.core.constants.displayasset.DisplayAssetResponseType;
import sk.sivak.eldritchhorror.core.constants.displayasset.HideType;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequest;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardResponse;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.back.condition.AbstractConditionBackListener;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;

public class DebtConditionBackListener8 extends AbstractConditionBackListener{

    public DebtConditionBackListener8(ConditionInfo conditionInfo) {
        super(conditionInfo);
    }

    @Override
    protected void execute() {
        boolean hasDarkPact = ServicePlatform.get().getConditionsDeck().hasCondition(
                getActiveInvestigatorId(), ConditionId.DARK_PACT);
        if (hasDarkPact) {
            advanceDoom();
        } else {
            TypewriterUtils.noYesQuestion("Gain a [#BAD]Dark Pact Condition?[]", this::advanceDoom, () -> {
                ServicePlatform.get().getGameService().hold();
                ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                ServicePlatform.get().getGameService().gainCondition(ConditionId.DARK_PACT);
                discardThisCard();
                ServicePlatform.get().getGameService().release();
            });
        }
    }

    private void advanceDoom() {
        ServicePlatform.get().getService().hold();
        ServicePlatform.get().getEncounterService().typeFlavor("The ticking noise begins to hypnotize you.");
        TypewriterUtils.confirmInfos(
                "[#BAD]Advance Doom[]",
                "[#GOOD]Discard this card[]").subscribe(() -> {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            ServicePlatform.get().getDoomOmenService().advanceDoom();
            discardThisCard();
            ServicePlatform.get().getService().release();
        });
        ServicePlatform.get().getService().release();
    }

    private void discardThisCard() {
        ShowCardRequest showCardRequest = new ShowCardRequest();
        showCardRequest.setConditionInfo(getConditionInfo());
        showCardRequest.setHideType(HideType.DISCARD_ALWAYS);
        showCardRequest.setTitle("Discard Debt Condition");
        showCardRequest.setDisplayAssetResponseType(DisplayAssetResponseType.OK);
        Single<ShowCardResponse> displayCondition = ServicePlatform.get().getGameService().showCard(showCardRequest);
        displayCondition.subscribe(response -> {
            ServicePlatform.get().getService().discardConditionFromInvestigator(getActiveInvestigatorId(), getConditionInfo());
        });
    }
}
