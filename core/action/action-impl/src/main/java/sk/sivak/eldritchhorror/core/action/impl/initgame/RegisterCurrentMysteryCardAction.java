package sk.sivak.eldritchhorror.core.action.impl.initgame;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import rx.Single;
import sk.sivak.eldritchhorror.core.action.Action;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.MysteryCardInfo;

/**
 * @author msivak
 */
public class RegisterCurrentMysteryCardAction implements Action<Object, Void> {

    private static final Logger logger = LogManager.getLogger(RegisterCurrentMysteryCardAction.class);

    @Override
    public Single<Void> execute() {
        return Single.create(sub -> {
            MysteryCardInfo currentMysteryCard = ServicePlatform.get().getMysteryDeck().getCurrentMysteryCard();
            ServicePlatform.get().getEventListenerProvider().registerMysteryListeners(currentMysteryCard);
            sub.onSuccess(null);
        });
    }

    @Override
    public void setInput(Object input) {

    }
}
