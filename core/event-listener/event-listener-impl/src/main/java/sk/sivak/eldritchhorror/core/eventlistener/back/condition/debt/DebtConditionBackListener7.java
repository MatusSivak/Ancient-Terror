package sk.sivak.eldritchhorror.core.eventlistener.back.condition.debt;

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

import static sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils.confirmInfos;

public class DebtConditionBackListener7 extends AbstractConditionBackListener{

    public DebtConditionBackListener7(ConditionInfo conditionInfo) {
        super(conditionInfo);
    }

    @Override
    protected void execute() {
        TypewriterUtils.displayTestConditionButton(Stat.WILL, 0, getConditionInfo(),
                () -> {
                    confirmInfos(
                            "[#GOOD]Discard this card[]").subscribe(() -> {
                        ServicePlatform.get().getService().hold();
                        ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                        discardThisCard();
                        ServicePlatform.get().getService().release();
                    });
                },
                () -> {
                    ServicePlatform.get().getEncounterService().typeFlavor("A fragment of your soul is ripped away.");
                    confirmInfos(
                            "[#BAD]Lose 3 Sanity[]",
                            "[#GOOD]Discard this card[]").subscribe(() -> {
                        ServicePlatform.get().getService().hold();
                        ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                        ServicePlatform.get().getTokenService().loseSanity(3);
                        discardThisCard();
                        ServicePlatform.get().getService().release();
                    });
                });
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
