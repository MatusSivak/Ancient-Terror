package sk.sivak.eldritchhorror.core.action.impl.reserve;

import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.action.AbstractHookableAction;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.asset.AssetId;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.reserve.ShowReserveDragAndDropInput;
import sk.sivak.eldritchhorror.core.constants.reserve.ShowReserveDragAndDropOutput;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;

import static java8.features.stream.Stream.anyMatch;
import static java8.features.util.IterableUtils.removeIf;

public class ShowReserveDragAndDropAction extends AbstractHookableAction<ShowReserveDragAndDropInput, ShowReserveDragAndDropOutput> {

    private SingleSubscriber<? super ShowReserveDragAndDropOutput> ss;

    public ShowReserveDragAndDropAction() {
        super(BeforeAfterEvent.SHOW_RESERVE_DRAG_AND_DROP);
    }

    @Override
    protected void onExecute(SingleSubscriber<? super ShowReserveDragAndDropOutput> ss) {
        this.ss = ss;
        setBankLoanAvailable();
        displaySelectAssetsText();
        ServicePlatform.get().getCardController()
                .showReserveDragAndDrop(input.getTestScore(), input.isBankLoanAvailable(), input.getMandatoryAssetId())
                .subscribe(this::onResponse);
    }

    private void onResponse(ShowReserveDragAndDropOutput output) {
        boolean bankLoanPresent = anyMatch(output.getSelectedAssets(), assetInfo -> AssetId.BANK_LOAN == assetInfo.getId());
        if (bankLoanPresent) {
            removeIf(output.getSelectedAssets(), assetInfo -> assetInfo.getId() == AssetId.BANK_LOAN);
            ServicePlatform.get().getCardService().hold();
            ServicePlatform.get().getCardService().takeBankLoan();
            ServicePlatform.get().getCardService().convertTo(ShowReserveDragAndDropOutput.class, () -> output);
            ServicePlatform.get().getCardService().release();
        }
        ss.onSuccess(output);
    }

    private void setBankLoanAvailable() {
        InvestigatorId activeInvestigatorId = ServicePlatform.get().getInvestigators().getActiveInvestigatorId();
        boolean hasDebt = ServicePlatform.get().getConditionsDeck().hasCondition(activeInvestigatorId, ConditionId.DEBT);
        input.setBankLoanAvailable(!hasDebt);
    }

    private void displaySelectAssetsText() {
        ServicePlatform.get().getGameController().displayText("Select Assets");
    }
}
