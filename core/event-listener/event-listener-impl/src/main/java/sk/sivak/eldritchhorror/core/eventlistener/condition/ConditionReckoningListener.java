package sk.sivak.eldritchhorror.core.eventlistener.condition;

import java8.features.function.Supplier;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.eventlistener.card.AbstractCardReckoningListener;
import sk.sivak.eldritchhorror.core.eventlistener.back.condition.AbstractConditionBackListener;

public class ConditionReckoningListener extends AbstractCardReckoningListener {

    private Supplier<AbstractConditionBackListener> findConditionBackListenerSupplier;
    private Supplier<InvestigatorId> cardOwnerSupplier;

    public ConditionReckoningListener(AbstractConditionListener abstractConditionListener) {
        findConditionBackListenerSupplier = abstractConditionListener::findConditionBackListener;
        cardOwnerSupplier = () -> abstractConditionListener.investigatorId;
    }

    @Override
    protected void onNotify() {
        findConditionBackListenerSupplier.get().executeWhole();
    }

    @Override
    protected InvestigatorId getCardOwnerId() {
        return cardOwnerSupplier.get();
    }
}
