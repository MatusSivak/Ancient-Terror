package sk.sivak.eldritchhorror.core.eventlistener.asset;

import java8.features.stream.Stream;
import java8.features.util.IterableUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import rx.functions.Action0;
import sk.sivak.eldritchhorror.core.constants.asset.BlunderbussAsset;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequestBuilder;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardResponse;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.test.DiceRoll;
import sk.sivak.eldritchhorror.core.constants.test.TestFlavorType;
import sk.sivak.eldritchhorror.core.constants.test.UsableAsset;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventqueue.EventListener;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestData;

import java.util.Arrays;
import java.util.List;

/**
 * @author msivak
 */
public class BlunderbussListener extends AbstractAssetListener<BlunderbussAsset> {

    private static final Logger logger = LogManager.getLogger(BlunderbussListener.class);
    private RegisterUsableAssetListener registerUsableAssetListener;
    private UpdateScoreListener updateScoreListener;
    private boolean usedInCombat = false;

    @Override
    protected void register() {
        registerUsableAssetListener = new RegisterUsableAssetListener();
        getEventQueue().addDirectEventListener(registerUsableAssetListener, DirectEvent.REGISTER_USABLE_ASSETS);
        updateScoreListener = new UpdateScoreListener();
        getEventQueue().addDirectEventListener(updateScoreListener, DirectEvent.UPDATE_SCORE_USING_ASSETS);
    }

    @Override
    protected List<EventListener> getEventListeners() {
        return Arrays.asList(updateScoreListener, registerUsableAssetListener);
    }

    private class UpdateScoreListener extends AbstractAssetEventListener<TestData> {

        public UpdateScoreListener() {
            super(BlunderbussListener.this);
        }

        @Override
        protected Runnable getEventAction(TestData input) {
            return () -> {
                if (!usedInCombat) {
                    return;
                }
                if (input.getStat() != Stat.STRENGTH) {
                    return;
                }
                if (!isTestFlavorOfType(TestFlavorType.COMBAT)) {
                    return;
                }
                usedInCombat = false;
                ServicePlatform.get().getService().hold();

                ServicePlatform.get().getService().addEventCommand( in -> {
                    if (Stream.anyMatch(input.getDiceRolls(), dr -> dr.getDiceValue() == 6)) {
                        onRolled6(input);
                    }
                });

                ServicePlatform.get().getService().addEventCommand( in -> {
                    if (Stream.anyMatch(input.getDiceRolls(), dr -> dr.getDiceValue() == 1)) {
                        boolean isGoodRollPresent = Stream.anyMatch(input.getDiceRolls(),
                                dr -> dr.getScore() == DiceRoll.Score.VERY_GOOD || dr.getScore() == DiceRoll.Score.GOOD);
                        if (!isGoodRollPresent) {
                            return;
                        }
                        onRolled1(input);
                    }
                });

                ServicePlatform.get().getService().release();


            };
        }

        private void onRolled1(TestData input) {
            ServicePlatform.get().getGameService().showCard(new ShowCardRequestBuilder(getAssetInfo())
                    .withTitle("Each 1 negates one successes")
                    .build())
                    .subscribe(showCardResponse -> onAnswer1(showCardResponse, input));
        }

        private void onAnswer1(ShowCardResponse response, TestData input) {
            int countOfRolledOnes = 0;
            for (DiceRoll diceRoll : input.getDiceRolls()) {
                countOfRolledOnes += diceRoll.getDiceValue() == 1 ? 1 : 0;
            }
            for (int i = 0; i < countOfRolledOnes; i++) {
                for (DiceRoll diceRoll : input.getDiceRolls()) {
                    if (diceRoll.getScore() == DiceRoll.Score.GOOD) {
                        diceRoll.setScore(DiceRoll.Score.BAD);
                        break;
                    } else if (diceRoll.getScore() == DiceRoll.Score.VERY_GOOD) {
                        diceRoll.setScore(DiceRoll.Score.GOOD);
                        break;
                    }
                }
            }

            ServicePlatform.get().getService().convertTo(TestData.class, () -> input);
        }

        private void onRolled6(TestData input) {
            if (input.hasReachedMinScoreToEndTest() && !Stream.anyMatch(input.getDiceRolls(), dr -> dr.getDiceValue() == 1)) {
                return;
            }
            ServicePlatform.get().getGameService().showCard(new ShowCardRequestBuilder(getAssetInfo())
                    .withTitle("Each 6 counts as two successes")
                    .build())
                    .subscribe(showCardResponse -> onAnswer6(showCardResponse, input));
        }

        private void onAnswer6(ShowCardResponse response, TestData input) {
            IterableUtils.forEach(input.getDiceRolls(), diceRoll -> {
                if (diceRoll.getDiceValue() == 6) {
                    diceRoll.setScore(DiceRoll.Score.VERY_GOOD);
                }
            });
            ServicePlatform.get().getService().convertTo(TestData.class, () -> input);
        }

        @Override
        public Class<TestData> getDataClass() {
            return TestData.class;
        }
    }

    private class RegisterUsableAssetListener extends AbstractAssetEventListener<TestData> {

        public RegisterUsableAssetListener() {
            super(BlunderbussListener.this);
        }

        @Override
        protected Runnable getEventAction(TestData input) {
            return () -> {
                usedInCombat = false;
                if (input.getStat() != Stat.STRENGTH) {
                    return;
                }
                if (!isTestFlavorOfType(TestFlavorType.COMBAT)) {
                    return;
                }
                UsableAsset usableAsset = addUsableAsset(input, getAssetInfo(), 2);
                usableAsset.setOnUseAction(getOnUseAction(input));
            };
        }

        private Action0 getOnUseAction(TestData input) {
            return () -> {
                usedInCombat = true;
            };
        }

        @Override
        public Class<TestData> getDataClass() {
            return TestData.class;
        }
    }
}
