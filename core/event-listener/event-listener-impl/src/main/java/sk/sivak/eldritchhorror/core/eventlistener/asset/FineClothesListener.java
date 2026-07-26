package sk.sivak.eldritchhorror.core.eventlistener.asset;

import java8.features.stream.Stream;
import java8.features.util.IterableUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import sk.sivak.eldritchhorror.core.constants.asset.FineClothesAsset;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequestBuilder;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardResponse;
import sk.sivak.eldritchhorror.core.constants.test.DiceRoll;
import sk.sivak.eldritchhorror.core.constants.test.TestFlavorType;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventqueue.EventListener;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestData;

import java.util.Collections;
import java.util.List;

/**
 * @author msivak
 */
public class FineClothesListener extends AbstractAssetListener<FineClothesAsset> {

    private static final Logger logger = LogManager.getLogger(FineClothesListener.class);
    private UpdateScoreListener updateScoreListener;

    @Override
    protected void register() {
        updateScoreListener = new UpdateScoreListener();
        getEventQueue().addDirectEventListener(updateScoreListener, DirectEvent.UPDATE_SCORE_USING_ASSETS);
    }

    @Override
    protected List<EventListener> getEventListeners() {
        return Collections.singletonList(updateScoreListener);
    }

    private class UpdateScoreListener extends AbstractAssetEventListener<TestData> {

        public UpdateScoreListener() {
            super(FineClothesListener.this);
        }

        @Override
        protected Runnable getEventAction(TestData input) {
            return () -> {
                if (input.hasReachedMinScoreToEndTest()) {
                    return;
                }
                if (!isTestFlavorOfType(TestFlavorType.ACQUIRE_ASSETS)) {
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
}
