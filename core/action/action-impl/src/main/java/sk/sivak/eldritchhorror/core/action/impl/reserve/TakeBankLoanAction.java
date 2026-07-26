package sk.sivak.eldritchhorror.core.action.impl.reserve;

import rx.Single;
import sk.sivak.eldritchhorror.core.action.Action;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.reserve.ShowReserveDragAndDropOutput;

public class TakeBankLoanAction implements Action<ShowReserveDragAndDropOutput, ShowReserveDragAndDropOutput> {

    private ShowReserveDragAndDropOutput input;

    @Override
    public Single<ShowReserveDragAndDropOutput> execute() {
        return Single.create(mainSub -> {
            ServicePlatform.get().getGameService().gainCondition(ConditionId.DEBT);
            mainSub.onSuccess(input);
        });
    }

    @Override
    public void setInput(ShowReserveDragAndDropOutput input) {
        this.input = input;
    }
}
