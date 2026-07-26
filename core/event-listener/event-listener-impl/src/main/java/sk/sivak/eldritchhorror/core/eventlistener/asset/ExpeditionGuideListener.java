package sk.sivak.eldritchhorror.core.eventlistener.asset;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import rx.Single;
import rx.functions.Action1;
import sk.sivak.eldritchhorror.core.constants.asset.ExpeditionGuideAsset;
import sk.sivak.eldritchhorror.core.constants.displayasset.DisplayAssetResponseType;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequest;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardResponse;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventqueue.EventListener;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.test.CountRerollDiceData;
import sk.sivak.eldritchhorror.core.eventtype.data.test.RerollUsingData;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestData;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author msivak
 */
public class ExpeditionGuideListener extends AbstractAssetListener<ExpeditionGuideAsset> {

    private static final Logger logger = LogManager.getLogger(ExpeditionGuideListener.class);
    private BeforeCountRerollDice beforeCountRerollDice;
    private boolean wantsToUseSpecialAsked = false;
    private BeforeRerollUsingFocus beforeRerollUsingFocus;

    @Override
    protected void register() {
        beforeCountRerollDice = new BeforeCountRerollDice();
        getEventQueue().addBeforeEventListener(beforeCountRerollDice, BeforeAfterEvent.COUNT_REROLL_DICE);
        beforeRerollUsingFocus = new BeforeRerollUsingFocus();
        getEventQueue().addBeforeEventListener(beforeRerollUsingFocus, BeforeAfterEvent.REROLL_USING_FOCUS);
    }

    @Override
    protected List<EventListener> getEventListeners() {
        return Arrays.asList(beforeCountRerollDice, beforeRerollUsingFocus);
    }

    private class BeforeRerollUsingFocus extends AbstractAssetEventListener<RerollUsingData> {

        public BeforeRerollUsingFocus() {
            super(ExpeditionGuideListener.this);
        }

        @Override
        protected Runnable getEventAction(RerollUsingData input) {
            return () -> {
                if (input.getTestData().hasReachedMinScoreToEndTest()) {
                    wantsToUseSpecialAsked = false;
                    return;
                }
                if (input.getTestData().getCalculatedDicePool() <= input.getTestData().getScore()) {
                    wantsToUseSpecialAsked = false;
                    return;
                }
                if (input.getTestData().getCalculatedDicePool() - input.getTestData().getSuccessRolls() < 2) {
                    wantsToUseSpecialAsked = false;
                    return;
                }
                if (input.getTestData().getStat() != Stat.OBSERVATION && input.getTestData().getStat() != Stat.STRENGTH) {
                    wantsToUseSpecialAsked = false;
                    return;
                }

                Question question = new Question();
                question.setTitle("Reroll two dice using Expedition Guide?");
                input.setQuestion(question);
                wantsToUseSpecialAsked = true;
            };
        }

        @Override
        public Class<RerollUsingData> getDataClass() {
            return RerollUsingData.class;
        }
    }

    private class BeforeCountRerollDice extends AbstractAssetEventListener<CountRerollDiceData> {

        public BeforeCountRerollDice() {
            super(ExpeditionGuideListener.this);
        }

        @Override
        protected Runnable getEventAction(CountRerollDiceData input) {
            return () -> {
                if (input.getCountRerollDiceType() != CountRerollDiceData.CountRerollDiceType.FOCUS) {
                    wantsToUseSpecialAsked = false;
                    return;
                }
                if (wantsToUseSpecialAsked) {
                    ShowCardRequest showCardRequest = new ShowCardRequest();
                    showCardRequest.setAssetInfo(getAssetInfo());
                    showCardRequest.setTitle("Reroll two dice");
                    showCardRequest.setDisplayAssetResponseType(DisplayAssetResponseType.OK);
                    Single<ShowCardResponse> showAsset = ServicePlatform.get().getGameService().showCard(showCardRequest);
                    showAsset.subscribe(showCardResponse -> onResponse(input));
                }
            };
        }

        private void onResponse(CountRerollDiceData input) {
            input.setCount(input.getCount() + 1);
            wantsToUseSpecialAsked = false;
            ServicePlatform.get().getGameService().convertTo(CountRerollDiceData.class, () -> input);
        }

        @Override
        public Class<CountRerollDiceData> getDataClass() {
            return CountRerollDiceData.class;
        }
    }
}
