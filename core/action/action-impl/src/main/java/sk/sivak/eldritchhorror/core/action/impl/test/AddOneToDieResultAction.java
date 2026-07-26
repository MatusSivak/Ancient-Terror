package sk.sivak.eldritchhorror.core.action.impl.test;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.action.AbstractHookableAction;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.test.DiceRoll;
import sk.sivak.eldritchhorror.core.controller.TestController;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.test.AddOneToDieResultData;

import static java8.features.stream.Stream.findFirstOrException;

/**
 * @author msivak
 */
public class AddOneToDieResultAction extends AbstractHookableAction<AddOneToDieResultData, AddOneToDieResultData> {

    private static final Logger logger = LogManager.getLogger(AddOneToDieResultAction.class);

    public AddOneToDieResultAction() {
        super(BeforeAfterEvent.ADD_ONE_TO_DIE_RESULT);
    }

    @Override
    protected void onExecute(SingleSubscriber<? super AddOneToDieResultData> ss) {
        TestController testController = ServicePlatform.get().getTestController();
        int minSuccessDiceValue = input.getTestData().getMinSuccessDiceValue();
        testController.addOneToDieResult(minSuccessDiceValue).subscribe(this::withDiceNr);
    }

    private void withDiceNr(Integer diceNr) {
        DiceRoll diceRoll = findFirstOrException(input.getTestData().getDiceRolls(), dr -> diceNr.equals(dr.getDiceNr()));
        diceRoll.setDiceValue(Math.min(6, diceRoll.getDiceValue() + 1));
        diceRoll.setScore(diceRoll.getDiceValue() < input.getTestData().getMinSuccessDiceValue() ? DiceRoll.Score.BAD : DiceRoll.Score.GOOD);
        ss.onSuccess(input);

    }
}
