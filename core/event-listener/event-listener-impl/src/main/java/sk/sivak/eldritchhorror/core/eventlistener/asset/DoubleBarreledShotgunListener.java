package sk.sivak.eldritchhorror.core.eventlistener.asset;

import java8.features.stream.Stream;
import java8.features.util.IterableUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import sk.sivak.eldritchhorror.core.constants.asset.DoubleBarreledShotgunAsset;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequestBuilder;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardResponse;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.test.DiceRoll;
import sk.sivak.eldritchhorror.core.constants.test.TestFlavorType;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventqueue.EventListener;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestData;

import java.util.Arrays;
import java.util.List;

/**
 * @author msivak
 */
public class DoubleBarreledShotgunListener extends AbstractAssetListener<DoubleBarreledShotgunAsset> {

    private static final Logger logger = LogManager.getLogger(DoubleBarreledShotgunListener.class);
    private RegisterUsableAssetListener registerUsableAssetListener;
    private UpdateScoreListener updateScoreListener;

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
            super(DoubleBarreledShotgunListener.this);
        }

        @Override
        protected Runnable getEventAction(TestData input) {
            return () -> {
                if (input.hasReachedMinScoreToEndTest()) {
                    return;
                }
                if (input.getStat() != Stat.STRENGTH) {
                    return;
                }
                if (!isTestFlavorOfType(TestFlavorType.COMBAT)) {
                    return;
                }
                if (!Stream.anyMatch(input.getDiceRolls(), dr -> dr.getDiceValue() == 6)) {
                    return;
                }

                ServicePlatform.get().getGameService().showCard(new ShowCardRequestBuilder(getAssetInfo())
                        .withTitle("Each 6 counts as 2 successes")
                        .build())
                        .subscribe(showCardResponse -> onAnswer(showCardResponse, input));
            };
        }

        private void onAnswer(ShowCardResponse response, TestData input) {
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
            super(DoubleBarreledShotgunListener.this);
        }

        @Override
        protected Runnable getEventAction(TestData input) {
            return () -> {
                if (input.getStat() != Stat.STRENGTH) {
                    return;
                }
                if (!isTestFlavorOfType(TestFlavorType.COMBAT)) {
                    return;
                }
                addUsableAsset(input, getAssetInfo(), 4);
            };
        }

        @Override
        public Class<TestData> getDataClass() {
            return TestData.class;
        }
    }
}
