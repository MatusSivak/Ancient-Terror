package sk.sivak.eldritchhorror.core.action.impl.test;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.action.AbstractHookableAction;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.test.DiceRoll;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestData;

import java.util.ArrayList;

/**
 * @author msivak
 */
public class RollDicesAction extends AbstractHookableAction<TestData, TestData> {

    private static final Logger logger = LogManager.getLogger(RollDicesAction.class);

    public RollDicesAction() {
        super(BeforeAfterEvent.ROLL_DICES);
    }

    @Override
    protected void onExecute(SingleSubscriber<? super TestData> ss) {
        int calculatedDicePool = input.getCalculatedDicePool();
        input.setDiceRolls(new ArrayList<>(calculatedDicePool));
        for (int i = 0; i < calculatedDicePool; i++) {
            int diceValue =  ServicePlatform.get().getModel().rollDie();
            DiceRoll diceRoll = new DiceRoll();
            diceRoll.setDiceNr(i);
            diceRoll.setDiceValue(diceValue);
            input.getDiceRolls().add(diceRoll);
        }
        ss.onSuccess(input);
    }
}
