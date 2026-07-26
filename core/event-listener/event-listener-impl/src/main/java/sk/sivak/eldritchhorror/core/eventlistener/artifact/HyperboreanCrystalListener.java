package sk.sivak.eldritchhorror.core.eventlistener.artifact;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import rx.Single;
import sk.sivak.eldritchhorror.core.constants.artifact.HyperboreanCrystalArtifact;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.displayasset.DisplayAssetResponseType;
import sk.sivak.eldritchhorror.core.constants.displayasset.HideType;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequest;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequestBuilder;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardResponse;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.question.Answer;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.constants.spell.SpellInfo;
import sk.sivak.eldritchhorror.core.constants.test.TestFlavorType;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.card.AbstractCardReckoningListener;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils;
import sk.sivak.eldritchhorror.core.eventqueue.EventListener;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;
import sk.sivak.eldritchhorror.core.eventtype.SelectCardData;
import sk.sivak.eldritchhorror.core.eventtype.data.test.RollData;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestData;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils.getActiveInvestigatorId;
import static sk.sivak.eldritchhorror.core.eventtype.data.test.CountRerollDiceData.CountRerollDiceType.OTHER;

public class HyperboreanCrystalListener extends AbstractArtifactListener<HyperboreanCrystalArtifact> {

    private static final Logger logger = LogManager.getLogger(HyperboreanCrystalListener.class);

    private RerollUsingAssetsListener rerollUsingAssetsListener;
    private ReckoningListener reckoningListener;


    @Override
    protected List<EventListener> getEventListeners() {
        return Arrays.asList(rerollUsingAssetsListener, reckoningListener);
    }

    @Override
    protected void register() {

        rerollUsingAssetsListener = new RerollUsingAssetsListener();
        getEventQueue().addDirectEventListener(rerollUsingAssetsListener, DirectEvent.REROLL_USING_ASSETS_2);

        reckoningListener = new ReckoningListener();
        getEventQueue().addDirectEventListener(reckoningListener, DirectEvent.RECKONING_CARDS_2);
    }

    private class RerollUsingAssetsListener extends AbstractArtifactEventListener<TestData> {

        public RerollUsingAssetsListener() {
            super(HyperboreanCrystalListener.this);
        }

        @Override
        protected Runnable getEventAction(TestData input) {
            return () -> {
                if (isTestFlavorOfType(TestFlavorType.SPELL)) {
                    return;
                }
                if (input.hasReachedMinScoreToEndTest()) {
                    return;
                }
                if (input.getCalculatedDicePool() <= input.getScore()) {
                    return;
                }
                if (ServicePlatform.get().getSpellsDeck().getSpells(getOwner()).isEmpty()) {
                    return;
                }
                ShowCardRequest showCardRequest = new ShowCardRequest();
                showCardRequest.setArtifactInfo(getArtifactInfo());
                showCardRequest.setTitle("Reroll dice for Spell?");
                showCardRequest.setDisplayAssetResponseType(DisplayAssetResponseType.YES_NO);
                showCardRequest.setHideType(HideType.DISCARD_ON_YES);

                ServicePlatform.get().getGameService().showCard(showCardRequest)
                        .subscribe(response -> onAnswer(response, input));
            };
        }


        private void onAnswer(ShowCardResponse showCardResponse, TestData input) {
            if (ShowCardResponse.YES == showCardResponse) {
                onUseArtifact(input);
            } else {
                ServicePlatform.get().getService().convertTo(TestData.class, () -> input);
            }

        }

        private void onUseArtifact(TestData input) {
            int maxDice = input.getCalculatedDicePool() - input.getSuccessRolls();
            if (maxDice == 1) {
                ServicePlatform.get().getService().hold();
                discardOneSpell();
                ServicePlatform.get().getTestService().rerollDie(input);
                ServicePlatform.get().getService().release();
            } else {
                Single<Answer<Integer, Object>> answer = askHowManyDiceToReroll(maxDice);
                answer.subscribe(amount -> {
                    ServicePlatform.get().getService().hold();
                    discardOneSpell();
                    ServicePlatform.get().getTestService().findCountRerollDice(amount.getResponseData(),
                            OTHER,
                            input);
                    ServicePlatform.get().getTestService().rerollDice();
                    ServicePlatform.get().getService().release();
                }, logger::error);
            }
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

        private void discardOneSpell() {
            InvestigatorId activeInvestigatorId = getActiveInvestigatorId();
            List<SpellInfo> spells = ServicePlatform.get().getSpellsDeck().getSpells(activeInvestigatorId);
            SelectCardData selectCardData = new SelectCardData();
            selectCardData.setAvailableCards(spells);
            selectCardData.setHideText("Display Spells?");
            selectCardData.setTitleText("Select Spell to discard");
            ServicePlatform.get().getCardService().selectSingleCard(selectCardData).subscribe(EncounterUtils::onSelectCardToDiscard);
        }
    }

    private class ReckoningListener extends AbstractCardReckoningListener {

        @Override
        protected InvestigatorId getCardOwnerId() {
            return investigatorId;
        }

        @Override
        protected void onNotify() {
            ShowCardRequest showCardRequest = new ShowCardRequestBuilder(getArtifactInfo())
                    .withTitle("Gain one Spell.")
                    .withDisplayAssetResponseType(DisplayAssetResponseType.OK)
                    .build();
            ServicePlatform.get().getGameService().showCard(showCardRequest).subscribe(ok -> {
                ServicePlatform.get().getGameService().gainSpell();
            });
        }

    }
}
