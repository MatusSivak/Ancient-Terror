package sk.sivak.eldritchhorror.core.eventlistener.asset;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import rx.Single;
import sk.sivak.eldritchhorror.core.constants.asset.AxeAsset;
import sk.sivak.eldritchhorror.core.constants.displayasset.DisplayAssetResponseType;
import sk.sivak.eldritchhorror.core.constants.displayasset.HideType;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequest;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardResponse;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.question.Answer;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.constants.test.TestFlavorType;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventqueue.EventListener;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestData;
import sk.sivak.eldritchhorror.core.eventtype.data.token.SpendData;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static sk.sivak.eldritchhorror.core.eventtype.data.test.CountRerollDiceData.CountRerollDiceType.OTHER;

/**
 * @author msivak
 */
public class AxeListener extends AbstractAssetListener<AxeAsset> {

    private static final Logger logger = LogManager.getLogger(AxeListener.class);
    private RegisterUsableAssetListener registerUsableAssetListener;
    private RerollUsingAssetsListener rerollUsingAssetsListener;

    @Override
    protected void register() {
        registerUsableAssetListener = new RegisterUsableAssetListener();
        getEventQueue().addDirectEventListener(registerUsableAssetListener, DirectEvent.REGISTER_USABLE_ASSETS);
        rerollUsingAssetsListener = new RerollUsingAssetsListener();
        getEventQueue().addDirectEventListener(rerollUsingAssetsListener, DirectEvent.REROLL_USING_ASSETS_2);
    }

    @Override
    protected List<EventListener> getEventListeners() {
        return Arrays.asList(registerUsableAssetListener, rerollUsingAssetsListener);
    }

    private class RegisterUsableAssetListener extends AbstractAssetEventListener<TestData> {

        public RegisterUsableAssetListener() {
            super(AxeListener.this);
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
                addUsableAsset(input, getAssetInfo(), 2);
            };
        }

        @Override
        public Class<TestData> getDataClass() {
            return TestData.class;
        }
    }

    private class RerollUsingAssetsListener extends AbstractAssetEventListener<TestData> {

        public RerollUsingAssetsListener() {
            super(AxeListener.this);
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
                if (input.hasReachedMinScoreToEndTest()) {
                    return;
                }
                if (input.getCalculatedDicePool() <= input.getScore()) {
                    return;
                }
                ServicePlatform.get().getTokenService().canSpend(0, 0, 0, 2).subscribe(response -> onCanSpend(response, input));
            };
        }

        private void onCanSpend(SpendData spendData, TestData input) {
            if (!spendData.hasEnough()) {
                ServicePlatform.get().getGameService().convertTo(TestData.class, () -> input);
                return;
            }
            ShowCardRequest showCardRequest = new ShowCardRequest();
            showCardRequest.setAssetInfo(getAssetInfo());
            showCardRequest.setTitle("Reroll dice for Sanity?");
            showCardRequest.setDisplayAssetResponseType(DisplayAssetResponseType.YES_NO);

            ServicePlatform.get().getGameService().showCard(showCardRequest)
                    .subscribe(response -> onAnswer(response, input));

        }

        private void onAnswer(ShowCardResponse showCardResponse, TestData input) {
            if (ShowCardResponse.YES == showCardResponse) {
                onUseAxe(input);
            } else {
                ServicePlatform.get().getService().convertTo(TestData.class, () -> input);
            }

        }

        private void onUseAxe(TestData input) {
            ServicePlatform.get().getTokenService().spend(0, 0, 0, 2).subscribe(response -> onSpend(response, input));
        }

        private void onSpend(SpendData spendData, TestData input) {
            if (spendData.hasEnough()) {
                int maxDice = input.getCalculatedDicePool() - input.getSuccessRolls();
                if (maxDice == 1) {
                    ServicePlatform.get().getService().hold();
                    spendData.pay();
                    ServicePlatform.get().getTestService().rerollDie(input);
                    ServicePlatform.get().getService().release();
                } else {
                    Single<Answer<Integer, Object>> answer = askHowManyDiceToReroll(maxDice);
                    answer.subscribe(amount -> {
                        ServicePlatform.get().getService().hold();
                        spendData.pay();
                        ServicePlatform.get().getTestService().findCountRerollDice(amount.getResponseData(),
                                OTHER,
                                input);
                        ServicePlatform.get().getTestService().rerollDice();
                        ServicePlatform.get().getService().release();
                    }, logger::error);
                }
            } else {
                ShowCardRequest showCardRequest = new ShowCardRequest();
                showCardRequest.setAssetInfo(getAssetInfo());
                showCardRequest.setHideType(HideType.RETURN);
                showCardRequest.setDisplayAssetResponseType(DisplayAssetResponseType.OK);
                showCardRequest.setTitle("Not enough Sanity");
                ServicePlatform.get().getGameService().showCard(showCardRequest).subscribe(showCardResponse -> notEnoughSanity(showCardResponse, input));
            }
        }

        private void notEnoughSanity(ShowCardResponse showCardResponse, TestData input) {
            ServicePlatform.get().getService().convertTo(TestData.class, () -> input);
        }

        private Single<Answer<Integer, Object>> askHowManyDiceToReroll(int maxDice) {
            Question<Integer> question = new Question<>();
            question.setTitle("How many dice you want to reroll?");
            question.setOptions(new LinkedList<>());
            for (int i = 1; i <= maxDice; i++) {
                question.getOptions().add(new Question.Option<>("" + i, i));
            }
            return ServicePlatform.get().getGameService().ask(question);
        }

        @Override
        public Class<TestData> getDataClass() {
            return TestData.class;
        }
    }
}
