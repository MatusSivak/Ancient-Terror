package sk.sivak.eldritchhorror.core.action.impl.initgame;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import rx.Single;
import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.action.Action;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.ancientone.AncientOneInfo;
import sk.sivak.eldritchhorror.core.constants.tracker.GoogleServicesHolder;

/**
 * @author msivak
 */
public class SelectAncientOneAction implements Action<Void, AncientOneInfo> {

    private static final Logger logger = LogManager.getLogger(SelectAncientOneAction.class);

    @Override
    public Single<AncientOneInfo> execute() {
        Single<AncientOneInfo> single = Single.create(onSub());
        return single.cache();
    }

    private Single.OnSubscribe<AncientOneInfo> onSub() {
        if (!GoogleServicesHolder.isTutorialPassed()) {
            return sub -> {
                AncientOneInfo azathothAncientOne = ServicePlatform.get().getModel().getAvailableAncientOnes().get(0);
                withAncientOne(sub, azathothAncientOne);
            };
        }
        return sub -> ServicePlatform.get().getInitGameController().selectAncientOne().subscribe(ancientOne -> {
            withAncientOne(sub, ancientOne);
        });
    }

    private void withAncientOne(SingleSubscriber<? super AncientOneInfo> sub, AncientOneInfo ancientOne) {
        logger.debug("Ancient one: " + ancientOne);
        ServicePlatform.get().getModel().setActiveAncientOne(ancientOne);
        ServicePlatform.get().getDoomTrack().initDoomTrack(ancientOne.getStartingDoom());
        ServicePlatform.get().getMonsterCup().removeFromCup(ancientOne.getRemovedMonsters());
        ServicePlatform.get().getResearchEncounterDeck().init(ancientOne.getAncientOneId());
        sub.onSuccess(ancientOne);
    }

    @Override
    public void setInput(Void input) {
        // input is ignored
    }
}
