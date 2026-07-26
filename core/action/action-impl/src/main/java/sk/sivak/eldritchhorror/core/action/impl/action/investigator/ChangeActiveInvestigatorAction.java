package sk.sivak.eldritchhorror.core.action.impl.action.investigator;

import rx.Single;
import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.action.Action;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.model.InvestigatorWrite;
import sk.sivak.eldritchhorror.core.model.InvestigatorsWrite;

public class ChangeActiveInvestigatorAction implements Action<Object, InvestigatorId> {

    private InvestigatorId targetInvestigatorId;

    public ChangeActiveInvestigatorAction(InvestigatorId investigatorId) {
        this.targetInvestigatorId = investigatorId;
    }

    public ChangeActiveInvestigatorAction() {
    }

    @Override
    public Single<InvestigatorId> execute() {
        return Single.create(this::execute);
    }

    private void execute(SingleSubscriber<? super InvestigatorId> onSub) {

        if (targetInvestigatorId != null) {
            changeActiveInvestigatorToTarget(onSub);
        } else {
            changeActiveInvestigator(onSub);
        }
    }

    private void changeActiveInvestigatorToTarget(SingleSubscriber<? super InvestigatorId> onSub) {
        InvestigatorsWrite investigators = ServicePlatform.get().getInvestigators();
        investigators.changeActiveInvestigator(targetInvestigatorId);
        onSub.onSuccess(investigators.getActiveInvestigatorId());
    }

    private void changeActiveInvestigator(SingleSubscriber<? super InvestigatorId> onSub) {
        InvestigatorsWrite investigators = ServicePlatform.get().getInvestigators();
        InvestigatorWrite activeInvestigator = investigators.getActiveInvestigator();
        if (activeInvestigator == null) {
            investigators.initActiveInvestigator();
            onSub.onSuccess(investigators.getActiveInvestigatorId());
            return;
        }
        investigators.changeActiveInvestigator();
        onSub.onSuccess(investigators.getActiveInvestigatorId());
    }

    @Override
    public void setInput(Object input) {

    }
}
