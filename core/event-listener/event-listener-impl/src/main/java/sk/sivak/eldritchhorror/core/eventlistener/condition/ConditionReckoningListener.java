package sk.sivak.eldritchhorror.core.eventlistener.condition;

import java8.features.function.Supplier;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.eventlistener.EventListenerImpl;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.back.condition.AbstractConditionBackListener;
import sk.sivak.eldritchhorror.core.eventtype.data.ReckoningFireType;
import sk.sivak.eldritchhorror.core.model.InvestigatorRead;

public class ConditionReckoningListener extends EventListenerImpl<ReckoningFireType> {

    private Supplier<AbstractConditionBackListener> findConditionBackListenerSupplier;
    private Supplier<InvestigatorId> cardOwnerSupplier;
    private boolean charged = false;

    public ConditionReckoningListener(AbstractConditionListener abstractConditionListener) {
        findConditionBackListenerSupplier = abstractConditionListener::findConditionBackListener;
        cardOwnerSupplier = () -> abstractConditionListener.investigatorId;
    }

    @Override
    public void onNotify(ReckoningFireType eventData) {
        if (eventData == ReckoningFireType.CHARGE) {
            charged = true;
            return;
        }
        if (eventData == ReckoningFireType.FIRE && !charged) {
            return;
        }
        charged = false;
        ServicePlatform.get().getService().addEventCommand(in -> {
            if (!ServicePlatform.get().getInvestigators().isOnBoard(cardOwnerSupplier.get())) {
                return;
            }

            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getDoomOmenService().onTriggeredReckoning();
            ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(cardOwnerSupplier.get());
            ServicePlatform.get().getInvestigatorService().showActiveInvestigator(true);

            AbstractConditionBackListener conditionBackListener = findConditionBackListenerSupplier.get();
            conditionBackListener.executeWhole();

            ServicePlatform.get().getGameService().checkIfSomeoneIsDefeated();
            ServicePlatform.get().getService().release();
        });
    }

    @Override
    public Class<ReckoningFireType> getDataClass() {
        return ReckoningFireType.class;
    }
}
