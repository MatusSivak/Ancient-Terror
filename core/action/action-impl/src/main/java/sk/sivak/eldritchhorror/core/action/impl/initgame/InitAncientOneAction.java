package sk.sivak.eldritchhorror.core.action.impl.initgame;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import rx.Single;
import sk.sivak.eldritchhorror.core.action.Action;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.ancientone.AncientOneInfo;

/**
 * @author msivak
 */
public class InitAncientOneAction implements Action<Object, Void> {

    private static final Logger logger = LogManager.getLogger(InitAncientOneAction.class);

    @Override
    public Single<Void> execute() {
        Single<Void> single = Single.create(onSub());
        return single.cache();
    }

    private Single.OnSubscribe<Void> onSub() {
        return sub -> {
            ServicePlatform servicePlatform = ServicePlatform.get();
            AncientOneInfo activeAncientOne = ServicePlatform.get().getModel().getAncientOne().getAncientOneInfo();
            servicePlatform.getEventListenerProvider().registerAncientOneListeners(activeAncientOne);
            int players = servicePlatform.getModel().getReferenceCard().getPlayers();
            servicePlatform.getMysteryDeck().initMysteryDeck(activeAncientOne.getAncientOneId(), players);
            servicePlatform.getMysteryDeck().drawNewMystery();
            sub.onSuccess(null);
        };

    }

    @Override
    public void setInput(Object input) {
    }
}
