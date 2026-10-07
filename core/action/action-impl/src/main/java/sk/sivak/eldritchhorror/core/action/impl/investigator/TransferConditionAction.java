package sk.sivak.eldritchhorror.core.action.impl.investigator;

import rx.Single;
import sk.sivak.eldritchhorror.core.action.Action;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionInfo;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;

public class TransferConditionAction implements Action<Object, Void> {
    private final InvestigatorId source, target;
    private final ConditionInfo condition;
    public TransferConditionAction(InvestigatorId source, InvestigatorId target, ConditionInfo condition) {
        this.source = source;
        this.target = target;
        this.condition = condition;
    }
    @Override public void setInput(Object input) {}
    @Override public Single<Void> execute() {
        return Single.create(subscriber -> {
            if (ServicePlatform.get().getConditionsDeck().transfer(source, target, condition)) {
                ServicePlatform.get().getConditionListenerProvider().unregister(condition);
                // Transfers preserve the actual card; Blessed still cancels Cursed.
                if (condition.getId() == ConditionId.BLESSED
                        && ServicePlatform.get().getConditionsDeck().hasCondition(target, ConditionId.CURSED)) {
                    ConditionInfo cursed = ServicePlatform.get().getConditionsDeck().getCondition(target, ConditionId.CURSED);
                    ServicePlatform.get().getConditionListenerProvider().unregister(cursed);
                    ServicePlatform.get().getConditionsDeck().discard(target, cursed);
                    ServicePlatform.get().getConditionsDeck().discard(target, condition);
                } else {
                    ServicePlatform.get().getConditionListenerProvider().getConditionListener(condition).register(condition, target);
                }
            }
            ServicePlatform.get().getGameController().updateHud().subscribe(() -> subscriber.onSuccess(null));
        });
    }
}

