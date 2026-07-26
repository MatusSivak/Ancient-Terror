package sk.sivak.eldritchhorror.core.eventlistener.artifact;

import java8.features.stream.Stream;
import java8.features.util.IterableUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import rx.Single;
import sk.sivak.eldritchhorror.core.constants.artifact.GlassOfMortlanArtifact;
import sk.sivak.eldritchhorror.core.constants.asset.DoubleBarreledShotgunAsset;
import sk.sivak.eldritchhorror.core.constants.displayasset.DisplayAssetResponseType;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequest;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequestBuilder;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardResponse;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.question.Answer;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.constants.spell.SpellInfo;
import sk.sivak.eldritchhorror.core.constants.test.DiceRoll;
import sk.sivak.eldritchhorror.core.constants.test.TestFlavorType;
import sk.sivak.eldritchhorror.core.eventlistener.EventListenerImpl;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.asset.AbstractAssetEventListener;
import sk.sivak.eldritchhorror.core.eventlistener.asset.AbstractAssetListener;
import sk.sivak.eldritchhorror.core.eventlistener.investigator.TheEntertainerInitListener;
import sk.sivak.eldritchhorror.core.eventqueue.EventListener;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestData;
import sk.sivak.eldritchhorror.core.eventtype.data.token.LoseTokenData;

import java.util.Arrays;
import java.util.List;

/**
 * @author msivak
 */
public class GlassOfMortlanListener extends AbstractArtifactListener<GlassOfMortlanArtifact> {

    private static final Logger logger = LogManager.getLogger(GlassOfMortlanListener.class);
    private UpdateScoreListener updateScoreListener;
    private BeforeLoseSanityListener beforeLoseSanityListener;

    @Override
    protected void register() {
        updateScoreListener = new UpdateScoreListener();
        getEventQueue().addDirectEventListener(updateScoreListener, DirectEvent.UPDATE_SCORE_USING_ASSETS);

        beforeLoseSanityListener = new BeforeLoseSanityListener();
        ServicePlatform.get().getEventQueue().addBeforeEventListener(beforeLoseSanityListener, BeforeAfterEvent.LOSE_SANITY);
    }

    @Override
    protected List<EventListener> getEventListeners() {
        return Arrays.asList(updateScoreListener, beforeLoseSanityListener);
    }

    private class UpdateScoreListener extends AbstractArtifactEventListener<TestData> {

        public UpdateScoreListener() {
            super(GlassOfMortlanListener.this);
        }

        @Override
        protected Runnable getEventAction(TestData input) {
            return () -> {
                if (input.hasReachedMinScoreToEndTest()) {
                    return;
                }
                if (!isTestFlavorOfType(TestFlavorType.SPELL)) {
                    return;
                }
                if (!Stream.anyMatch(input.getDiceRolls(), dr -> dr.getDiceValue() == 6)) {
                    return;
                }

                ServicePlatform.get().getGameService().showCard(new ShowCardRequestBuilder(getArtifactInfo())
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

    private class BeforeLoseSanityListener extends AbstractArtifactEventListener<LoseTokenData> {

        public BeforeLoseSanityListener() {
            super(GlassOfMortlanListener.this);
        }

        @Override
        public void onNotify(LoseTokenData eventData) {
            ServicePlatform.get().getService().<LoseTokenData>addEventCommand((input) -> {
                getEventAction(eventData).run();
            });
        }

        @Override
        protected Runnable getEventAction(LoseTokenData eventData) {
            return () -> {
                if (eventData.getAmount() <= 0) {
                    return;
                }
                if (TestFlavorType.SPELL != ServicePlatform.get().getTestFlavor().getFlavorType()) {
                    return;
                }
                SpellInfo castedSpellInfo = ServicePlatform.get().getTestFlavor().getFlavorData();
                boolean isSpellCastedByOwnerOfGlass = ServicePlatform.get().getSpellsDeck().getSpells(investigatorId).contains(castedSpellInfo);

                if (!isSpellCastedByOwnerOfGlass) {
                    return;
                }

                askQuestion().subscribe(answer -> {
                    if (answer == ShowCardResponse.YES) {
                        eventData.setAmount(Math.max(0, eventData.getAmount()-1));
                    }
                    ServicePlatform.get().getService().convertTo(LoseTokenData.class, () -> eventData);
                });
            };
        }

        private Single<ShowCardResponse> askQuestion() {
            ShowCardRequest request = new ShowCardRequestBuilder(getArtifactInfo())
                    .withTitle("Prevent the loss of 1 Sanity")
                    .withDisplayAssetResponseType(DisplayAssetResponseType.YES_NO)
                    .build();

            return ServicePlatform.get().getGameService().showCard(request);
        }

        @Override
        public Class<LoseTokenData> getDataClass() {
            return LoseTokenData.class;
        }
    }
}
