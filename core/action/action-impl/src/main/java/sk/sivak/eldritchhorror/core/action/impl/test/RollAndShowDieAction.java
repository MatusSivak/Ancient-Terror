package sk.sivak.eldritchhorror.core.action.impl.test;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import rx.Completable;
import rx.SingleSubscriber;
import rx.functions.Action0;
import sk.sivak.eldritchhorror.core.action.AbstractHookableAction;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.test.DiceRoll;
import sk.sivak.eldritchhorror.core.controller.TestController;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.test.RollData;

import java.util.Collections;
import java.util.Random;

/**
 * @author msivak
 */
public class RollAndShowDieAction extends AbstractHookableAction<RollData, Integer> {

    private static final Logger logger = LogManager.getLogger(RollAndShowDieAction.class);
    private TestController controller;
    private DiceRoll diceRoll;

    public RollAndShowDieAction() {
        super(BeforeAfterEvent.ROLL_AND_SHOW_DIE);
    }

    @Override
    protected void onExecute(SingleSubscriber<? super Integer> ss) {
        diceRoll = new DiceRoll();
        diceRoll.setDiceNr(0);
        diceRoll.setDiceValue(ServicePlatform.get().getModel().rollDie());
        diceRoll.setScore(diceRoll.getDiceValue() >= input.getMinSuccessful() ? DiceRoll.Score.GOOD : DiceRoll.Score.BAD);
        controller = ServicePlatform.get().getTestController();
        Completable showRolledDices = controller.showRolledDices(Collections.singletonList(diceRoll));

        showRolledDices.subscribe(onCompletedRoll(ss));
    }

    private Action0 onCompletedRoll(SingleSubscriber<? super Integer> ss) {
        return () -> {
            int score;
            if (diceRoll.getDiceValue() <= input.getMaxFailed()) {
                score = 0;
            } else if (diceRoll.getDiceValue() >= input.getMinSuccessful()) {
                score = 2;
            } else {
                score = 1;
            }
            Completable confirmTestResult = controller.confirmRollResult(score);
            confirmTestResult.subscribe(() -> ss.onSuccess(diceRoll.getDiceValue()));
        };
    }
}
