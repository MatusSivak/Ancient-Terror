package sk.sivak.eldritchhorror.core.action.impl.game;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import rx.Single;
import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.action.AbstractHookableAction;
import sk.sivak.eldritchhorror.core.action.Action;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.MysteryCardInfo;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;

/**
 * @author msivak
 */
public class AdvanceActiveMysteryAction extends AbstractHookableAction<Object, Void> {

    private static final Logger logger = LogManager.getLogger(AdvanceActiveMysteryAction.class);

    public AdvanceActiveMysteryAction() {
        super(BeforeAfterEvent.ADVANCE_ACTIVE_MYSTERY);
    }

    @Override
    protected void onExecute(SingleSubscriber<? super Void> ss) {
        MysteryCardInfo currentMysteryCard = ServicePlatform.get().getMysteryDeck().getCurrentMysteryCard();
        ServicePlatform.get().getEventListenerProvider().advanceActiveMysteryAction(currentMysteryCard);
        ss.onSuccess(null);
    }
}
