package sk.sivak.eldritchhorror.core.action.impl.test;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import rx.Completable;
import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.action.AbstractHookableAction;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.test.DiceRoll;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.test.CountRerollDiceData;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestData;

import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * @author msivak
 */
public class RerollDiceAction extends AbstractHookableAction<CountRerollDiceData, TestData> {

    private static final Logger logger = LogManager.getLogger(RerollDiceAction.class);

    private List<DiceRoll> dicesToReroll;
    public RerollDiceAction(List<DiceRoll> dicesToReroll) {
        super(BeforeAfterEvent.REROLL_DIE);
        this.dicesToReroll = dicesToReroll;
    }

    @Override
    protected void onExecute(SingleSubscriber<? super TestData> ss) {

        Collections.sort(input.getTestData().getDiceRolls(), (o1, o2) -> o1.getDiceValue() - o2.getDiceValue());
        if (dicesToReroll == null || dicesToReroll.isEmpty()) {
            dicesToReroll = input.getTestData().getDiceRolls().subList(0, input.getCount());
        }

        int minSuccessDiceValue = input.getTestData().getMinSuccessDiceValue();
        for (DiceRoll diceRoll : dicesToReroll) {
            if (input.getRerolledValues().isEmpty()) {
                diceRoll.setDiceValue(ServicePlatform.get().getModel().rollDie());
            } else {
                diceRoll.setDiceValue(input.getRerolledValues().remove(0));
            }
            diceRoll.setScore(diceRoll.getDiceValue() < minSuccessDiceValue ? DiceRoll.Score.BAD : DiceRoll.Score.GOOD);
        }

        Completable completable = ServicePlatform.get().getTestController().rerollDice(dicesToReroll);
        completable.subscribe(() -> ss.onSuccess(input.getTestData()));
    }
}
