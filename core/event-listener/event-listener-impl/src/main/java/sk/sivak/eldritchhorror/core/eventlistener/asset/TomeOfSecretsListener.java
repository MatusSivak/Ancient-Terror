package sk.sivak.eldritchhorror.core.eventlistener.asset;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import sk.sivak.eldritchhorror.core.constants.asset.TomeOfSecretsAsset;
import sk.sivak.eldritchhorror.core.constants.displayasset.DisplayAssetResponseType;
import sk.sivak.eldritchhorror.core.constants.displayasset.HideType;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequest;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardResponse;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.card.EnableCardListener;
import sk.sivak.eldritchhorror.core.eventqueue.EventListener;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.action.FocusData;
import sk.sivak.eldritchhorror.core.eventtype.data.basic.GainSanityData;
import sk.sivak.eldritchhorror.core.eventtype.data.test.CountRerollDiceData;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestData;
import sk.sivak.eldritchhorror.core.eventtype.data.token.SpendData;
import sk.sivak.eldritchhorror.core.eventtype.data.token.TokenType;
import sk.sivak.eldritchhorror.core.model.InvestigatorRead;
import sk.sivak.eldritchhorror.core.service.GameService;
import sk.sivak.eldritchhorror.core.service.TokenService;

import java.util.Arrays;
import java.util.List;

/**
 * @author msivak
 */
public class TomeOfSecretsListener extends AbstractAssetListener<TomeOfSecretsAsset> {

    private static final Logger logger = LogManager.getLogger(TomeOfSecretsListener.class);
    private TomeOfSecretsAfterFocus tomeOfSecretsAfterFocus;
    private UseTomeInsteadOfClueListener useTomeInsteadOfClueListener;
    private SpendOneLessClueListener spendOneLessClueListener;
    private EnableCardListener enableCardListener;

    @Override
    protected void register() {
        tomeOfSecretsAfterFocus = new TomeOfSecretsAfterFocus();
        getEventQueue().addAfterEventListener(tomeOfSecretsAfterFocus, BeforeAfterEvent.FOCUS);

        useTomeInsteadOfClueListener = new UseTomeInsteadOfClueListener();
        getEventQueue().addDirectEventListener(useTomeInsteadOfClueListener, DirectEvent.REROLL_USING_ASSETS_2);

        spendOneLessClueListener = new SpendOneLessClueListener();
        getEventQueue().addBeforeEventListener(spendOneLessClueListener, BeforeAfterEvent.SPEND);

        enableCardListener = new EnableCardListener(getAssetInfo());
        getEventQueue().addDirectEventListener(enableCardListener, DirectEvent.REENABLE_DISABLED_ABILITIES);
    }

    @Override
    protected List<EventListener> getEventListeners() {
        return Arrays.asList(tomeOfSecretsAfterFocus, useTomeInsteadOfClueListener, spendOneLessClueListener, enableCardListener);
    }

    private boolean temporarySupressSpendListener = false;

    private class UseTomeInsteadOfClueListener extends AbstractAssetEventListener<TestData> {

        public UseTomeInsteadOfClueListener() {
            super(TomeOfSecretsListener.this);
        }

        @Override
        protected Runnable getEventAction(TestData testData) {
            return () -> {
                if (!enabled) {
                    return;
                }
                if (testData.hasReachedMinScoreToEndTest()) {
                    return;
                }
                if (testData.getCalculatedDicePool() <= testData.getScore()) {
                    return;
                }

                ServicePlatform.get().getTokenService().canSpend(0,1,0,0).subscribe(canSpendData -> {
                    if (canSpendData.hasEnough()) {
                        ShowCardRequest showCardRequest = new ShowCardRequest();
                        showCardRequest.setAssetInfo(getAssetInfo());
                        showCardRequest.setTitle("Reroll using Focus instead of Clue?");
                        showCardRequest.setDisplayAssetResponseType(DisplayAssetResponseType.YES_NO);
                        showCardRequest.setHideType(HideType.DISABLE_ON_YES);

                        ServicePlatform.get().getGameService().showCard(showCardRequest)
                                .subscribe(showCardResponse -> onAnswer(showCardResponse, testData));
                    } else {
                        ServicePlatform.get().getService().convertTo(TestData.class, () -> testData);
                    }
                });
            };
        }


        private void onAnswer(ShowCardResponse showCardResponse, TestData testData) {
            if (showCardResponse == ShowCardResponse.YES) {
                ServicePlatform.get().getTokenService().spend(0,1,0,0).subscribe(spendData -> {
                    if (spendData.hasEnough()) {
                        ServicePlatform.get().getService().hold();
                        spendData.pay();
                        ServicePlatform.get().getService().disableCard(getAssetInfo());
                        ServicePlatform.get().getTestService().findCountRerollDice(CountRerollDiceData.CountRerollDiceType.CLUE, testData);
                        ServicePlatform.get().getTestService().rerollDice();
                        ServicePlatform.get().getService().convertTo(TestData.class, () -> testData);
                        enabled = false;
                        ServicePlatform.get().getService().release();
                    } else {
                        ServicePlatform.get().getService().convertTo(TestData.class, () -> testData);
                    }
                });
            } else {
                temporarySupressSpendListener = true;
                ServicePlatform.get().getService().convertTo(TestData.class, () -> testData);
            }

        }

        @Override
        public Class<TestData> getDataClass() {
            return TestData.class;
        }
    }

    private class SpendOneLessClueListener extends AbstractAssetEventListener<SpendData> {

        public SpendOneLessClueListener() {
            super(TomeOfSecretsListener.this);
        }

        @Override
        protected Runnable getEventAction(SpendData input) {
            return () -> {
                if (!enabled) {
                    return;
                }
                Integer cluesRequired = input.getTokenAmountMap().get(TokenType.CLUE);
                if (cluesRequired <= 0) {
                    return;
                }
                if (temporarySupressSpendListener) {
                    temporarySupressSpendListener = false;
                    return;
                }
                if (input.isSilent()) {
                    onSilent(input, cluesRequired);
                } else {
                    onLoud(input, cluesRequired);
                }

            };
        }

        private void onSilent(SpendData input, Integer cluesRequired) {
            Integer focusRequired = input.getTokenAmountMap().get(TokenType.FOCUS);
            ServicePlatform.get().getTokenService().canSpend(cluesRequired-1,focusRequired + 1,0,0).subscribe(canSpendData -> {
                if (canSpendData.hasEnough()) {
                    input.getTokenAmountMap().put(TokenType.CLUE, cluesRequired-1);
                    input.getTokenAmountMap().put(TokenType.FOCUS, focusRequired +1);
                } else {
                    ServicePlatform.get().getService().convertTo(SpendData.class, () -> input);
                }
            });
        }

        private void onLoud(SpendData input, Integer cluesRequired) {
            Integer focusRequired = input.getTokenAmountMap().get(TokenType.FOCUS);
            ServicePlatform.get().getTokenService().canSpend(cluesRequired-1,focusRequired + 1,0,0).subscribe(canSpendData -> {
                if (!canSpendData.hasEnough()) {
                    ServicePlatform.get().getService().convertTo(SpendData.class, () -> input);
                    return;
                }
                GameService service = ServicePlatform.get().getGameService();
                ShowCardRequest showCardRequest = new ShowCardRequest();
                showCardRequest.setAssetInfo(getAssetInfo());
                showCardRequest.setHideType(HideType.DISABLE_ON_YES);
                showCardRequest.setTitle("Spend 1 Focus in place of spending 1 Clue?");
                showCardRequest.setDisplayAssetResponseType(DisplayAssetResponseType.YES_NO);
                service.showCard(showCardRequest).subscribe(showCardResponse ->
                        onAnswer(showCardResponse, input, cluesRequired));
            });
        }

        private void onAnswer(ShowCardResponse showCardResponse, SpendData input, Integer cluesRequired) {
            ServicePlatform.get().getService().hold();
            if (ShowCardResponse.YES == showCardResponse) {
                ServicePlatform.get().getService().disableCard(getAssetInfo());
                Integer focusRequired = input.getTokenAmountMap().get(TokenType.FOCUS);
                input.getTokenAmountMap().put(TokenType.CLUE, cluesRequired-1);
                input.getTokenAmountMap().put(TokenType.FOCUS, focusRequired+1);
                enabled = false;
                input.addPostRevertAction(() -> ServicePlatform.get().getService().enableCard(getAssetInfo()));
            }
            ServicePlatform.get().getGameService().convertTo(SpendData.class, () -> input);
            ServicePlatform.get().getService().release();
        }


        @Override
        public Class<SpendData> getDataClass() {
            return SpendData.class;
        }
    }

    private class TomeOfSecretsAfterFocus extends AbstractAssetEventListener<FocusData> {

        private TomeOfSecretsAfterFocus() {
            super(TomeOfSecretsListener.this);
        }

        @Override
        protected Runnable getEventAction(FocusData input) {
            return () -> {
                InvestigatorRead activeInvestigator = ServicePlatform.get().getInvestigators().getActiveInvestigator();
                if (activeInvestigator.getCurrentSanity() == activeInvestigator.getInfo().getMaxSanity()) {
                    return;
                }

                GameService service = ServicePlatform.get().getGameService();
                ShowCardRequest showCardRequest = new ShowCardRequest();
                showCardRequest.setAssetInfo(getAssetInfo());
                showCardRequest.setTitle("Recover 1 sanity");
                showCardRequest.setDisplayAssetResponseType(DisplayAssetResponseType.OK);
                service.showCard(showCardRequest).subscribe(showCardResponse -> onSuccess(showCardResponse, input));
            };
        }

        private void onSuccess(ShowCardResponse showCardResponse, FocusData input) {
            TokenService service = ServicePlatform.get().getTokenService();
            service.hold();
            service.gainSanity(1);
            service.convertFromTo(GainSanityData.class, FocusData.class, in -> input);
            service.release();
        }

        @Override
        public Class<FocusData> getDataClass() {
            return FocusData.class;
        }
    }
}
