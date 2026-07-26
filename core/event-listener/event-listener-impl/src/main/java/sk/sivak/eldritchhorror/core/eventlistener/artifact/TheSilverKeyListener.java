package sk.sivak.eldritchhorror.core.eventlistener.artifact;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import sk.sivak.eldritchhorror.core.constants.artifact.TheSilverKeyArtifact;
import sk.sivak.eldritchhorror.core.constants.displayasset.DisplayAssetResponseType;
import sk.sivak.eldritchhorror.core.constants.displayasset.HideType;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequest;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardResponse;
import sk.sivak.eldritchhorror.core.constants.test.TestFlavorType;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.card.EnableCardListener;
import sk.sivak.eldritchhorror.core.eventqueue.EventListener;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.test.CountRerollDiceData;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestData;
import sk.sivak.eldritchhorror.core.eventtype.data.token.SpendData;
import sk.sivak.eldritchhorror.core.eventtype.data.token.TokenType;
import sk.sivak.eldritchhorror.core.service.GameService;

import java.util.Arrays;
import java.util.List;

public class TheSilverKeyListener extends AbstractArtifactListener<TheSilverKeyArtifact> {

    private static final Logger logger = LogManager.getLogger(TheSilverKeyListener.class);
    private SpendOneLessClueListener spendOneLessClueListener;
    private RerollUsingAssetsListener rerollUsingAssetsListener;
    private UseKeyInsteadOfClueListener useKeyInsteadOfClueListener;

    private boolean temporarySupressSpendListener = false;
    private EnableCardListener enableCardListener;

    @Override
    protected List<EventListener> getEventListeners() {
        return Arrays.asList(spendOneLessClueListener, rerollUsingAssetsListener, useKeyInsteadOfClueListener, enableCardListener);
    }

    @Override
    protected void register() {
        rerollUsingAssetsListener = new RerollUsingAssetsListener();
        getEventQueue().addDirectEventListener(rerollUsingAssetsListener, DirectEvent.REROLL_USING_ASSETS_1);

        spendOneLessClueListener = new SpendOneLessClueListener();
        getEventQueue().addBeforeEventListener(spendOneLessClueListener, BeforeAfterEvent.SPEND);

        useKeyInsteadOfClueListener = new UseKeyInsteadOfClueListener();
        getEventQueue().addDirectEventListener(useKeyInsteadOfClueListener, DirectEvent.REROLL_USING_ASSETS_2);

        enableCardListener = new EnableCardListener(getArtifactInfo());
        getEventQueue().addDirectEventListener(enableCardListener, DirectEvent.REENABLE_DISABLED_ABILITIES);
    }

    private class UseKeyInsteadOfClueListener extends AbstractArtifactEventListener<TestData> {

        public UseKeyInsteadOfClueListener() {
            super(TheSilverKeyListener.this);
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

                ShowCardRequest showCardRequest = new ShowCardRequest();
                showCardRequest.setArtifactInfo(getArtifactInfo());
                showCardRequest.setTitle("Reroll using 'The Silver Key' instead of Clue?");
                showCardRequest.setDisplayAssetResponseType(DisplayAssetResponseType.YES_NO);
                showCardRequest.setHideType(HideType.DISABLE_ON_YES);

                ServicePlatform.get().getGameService().showCard(showCardRequest)
                        .subscribe(showCardResponse -> onAnswer(showCardResponse, testData));
            };
        }


        private void onAnswer(ShowCardResponse showCardResponse, TestData testData) {
            if (showCardResponse == ShowCardResponse.YES) {
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getService().disableCard(getArtifactInfo());
                ServicePlatform.get().getTestService().findCountRerollDice(CountRerollDiceData.CountRerollDiceType.CLUE, testData);
                ServicePlatform.get().getTestService().rerollDice();
                ServicePlatform.get().getService().convertTo(TestData.class, () -> testData);
                enabled = false;
                ServicePlatform.get().getService().release();
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

    private class SpendOneLessClueListener extends AbstractArtifactEventListener<SpendData> {

        public SpendOneLessClueListener() {
            super(TheSilverKeyListener.this);
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
            input.getTokenAmountMap().put(TokenType.CLUE, cluesRequired-1);
        }

        private void onLoud(SpendData input, Integer cluesRequired) {
            GameService service = ServicePlatform.get().getGameService();
            ShowCardRequest showCardRequest = new ShowCardRequest();
            showCardRequest.setArtifactInfo(getArtifactInfo());
            showCardRequest.setHideType(HideType.DISABLE_ON_YES);
            showCardRequest.setTitle("Use 'The Silver Key' instead of spending Clue?");
            showCardRequest.setDisplayAssetResponseType(DisplayAssetResponseType.YES_NO);
            service.showCard(showCardRequest).subscribe(showCardResponse ->
                    onAnswer(showCardResponse, input, cluesRequired));
        }

        private void onAnswer(ShowCardResponse showCardResponse, SpendData input, Integer cluesRequired) {
            ServicePlatform.get().getService().hold();
            if (ShowCardResponse.YES == showCardResponse) {
                ServicePlatform.get().getService().disableCard(getArtifactInfo());
                input.getTokenAmountMap().put(TokenType.CLUE, cluesRequired-1);
                enabled = false;
                input.addPostRevertAction(() -> ServicePlatform.get().getService().enableCard(getArtifactInfo()));
            }
            ServicePlatform.get().getGameService().convertTo(SpendData.class, () -> input);
            ServicePlatform.get().getService().release();
        }


        @Override
        public Class<SpendData> getDataClass() {
            return SpendData.class;
        }
    }


    private class RerollUsingAssetsListener extends AbstractArtifactEventListener<TestData> {

        public RerollUsingAssetsListener() {
            super(TheSilverKeyListener.this);
        }

        @Override
        protected Runnable getEventAction(TestData input) {
            return () -> {
                if (input.hasReachedMinScoreToEndTest()) {
                    return;
                }
                if (!isTestFlavorOfType(TestFlavorType.OTHER_WORLD)) {
                    return;
                }
                if (input.getCalculatedDicePool() <= input.getScore()) {
                    return;
                }
                ShowCardRequest showCardRequest = new ShowCardRequest();
                showCardRequest.setArtifactInfo(getArtifactInfo());
                showCardRequest.setTitle("Reroll 1 die?");
                showCardRequest.setDisplayAssetResponseType(DisplayAssetResponseType.YES_NO);

                ServicePlatform.get().getGameService().showCard(showCardRequest)
                        .subscribe(showCardResponse -> onAnswer(showCardResponse, input));
            };
        }

        private void onAnswer(ShowCardResponse showCardResponse, TestData input) {
            if (ShowCardResponse.YES == showCardResponse) {
                ServicePlatform.get().getTestService().rerollDie(input);
            } else {
                ServicePlatform.get().getService().convertFromTo(ShowCardResponse.class, TestData.class, response -> input);
            }

        }

        @Override
        public Class<TestData> getDataClass() {
            return TestData.class;
        }
    }

}
