package sk.sivak.eldritchhorror.core.action.impl.initgame;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import rx.Single;
import sk.sivak.eldritchhorror.core.action.Action;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.tracker.AnalyticsCategory;
import sk.sivak.eldritchhorror.core.constants.tracker.GoogleServicesHolder;
import sk.sivak.eldritchhorror.core.model.DiceRollSimulator;

/**
 * @author msivak
 */
public class SelectDifficultyAction implements Action<Void, Void> {

    private static final Logger logger = LogManager.getLogger(SelectDifficultyAction.class);

    @Override
    public Single<Void> execute() {
        Single<Void> single = Single.create(onSub());
        return single.cache();
    }

    private Single.OnSubscribe<Void> onSub() {
        if (!GoogleServicesHolder.isTutorialPassed()) {
            ServicePlatform.get().getModel().setDiceRollSimulator(new DiceRollSimulator.NormalDiceRoller());
            return sub -> sub.onSuccess(null);
        }
        return sub -> ServicePlatform.get().getInitGameController().selectDifficulty().subscribe(difficulty -> {
            GoogleServicesHolder.getAnalyticsTracker().trackInteraction(AnalyticsCategory.DIFFICULTY, difficulty.name());
            switch (difficulty) {
                case EASY:
                    ServicePlatform.get().getModel().setDiceRollSimulator(new DiceRollSimulator.EasyDiceRoller());
                    break;
                case NORMAL:
                    ServicePlatform.get().getModel().setDiceRollSimulator(new DiceRollSimulator.NormalDiceRoller());
                    break;
            }
            sub.onSuccess(null);
        });
    }

    @Override
    public void setInput(Void input) {
        // input is ignored
    }
}
