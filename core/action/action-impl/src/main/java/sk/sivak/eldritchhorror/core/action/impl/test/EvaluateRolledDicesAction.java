package sk.sivak.eldritchhorror.core.action.impl.test;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.action.AbstractHookableAction;
import sk.sivak.eldritchhorror.core.constants.test.DiceRoll;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestData;

/**
 * @author msivak
 */
public class EvaluateRolledDicesAction extends AbstractHookableAction<TestData, TestData> {

    private static final Logger logger = LogManager.getLogger(EvaluateRolledDicesAction.class);

    public EvaluateRolledDicesAction() {
        super(BeforeAfterEvent.EVALUATE_ROLLED_DICES);
    }

    @Override
    protected void onExecute(SingleSubscriber<? super TestData> ss) {
        int minSuccessDiceValue = input.getMinSuccessDiceValue();
        for (DiceRoll diceRoll : input.getDiceRolls()) {
            if (diceRoll.getDiceValue() < minSuccessDiceValue) {
                diceRoll.setScore(DiceRoll.Score.BAD);
            } else {
                diceRoll.setScore(DiceRoll.Score.GOOD);
            }
        }
        ss.onSuccess(input);
    }
}
