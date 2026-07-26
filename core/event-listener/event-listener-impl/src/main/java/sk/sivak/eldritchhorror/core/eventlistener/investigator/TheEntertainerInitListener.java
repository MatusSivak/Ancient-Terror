package sk.sivak.eldritchhorror.core.eventlistener.investigator;

import rx.Single;
import sk.sivak.eldritchhorror.core.constants.artifact.ArtifactId;
import sk.sivak.eldritchhorror.core.constants.asset.AssetId;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.question.Answer;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.constants.spell.SpellId;
import sk.sivak.eldritchhorror.core.constants.test.TestFlavorType;
import sk.sivak.eldritchhorror.core.eventlistener.EventListenerImpl;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.token.LoseTokenData;
import sk.sivak.eldritchhorror.core.eventtype.data.token.SpendData;
import sk.sivak.eldritchhorror.core.eventtype.data.token.TokenType;

import java.util.Arrays;
import java.util.Map;

/**
 * @author msivak
 */
public class TheEntertainerInitListener extends AbstractInvestigatorInitListener {

    private BeforeSpendListener beforeSpendListener;
    private BeforeLoseSanityListener beforeLoseSanityListener;

    @Override
    protected InvestigatorId getInvestigatorId() {
        return InvestigatorId.THE_ENTERTAINER;
    }

    @Override
    protected void initInvestigator() {
        beforeSpendListener = new BeforeSpendListener();
        beforeLoseSanityListener = new BeforeLoseSanityListener();

        ServicePlatform.get().getEventQueue().addBeforeEventListener(beforeSpendListener, BeforeAfterEvent.SPEND);
        ServicePlatform.get().getEventQueue().addBeforeEventListener(beforeLoseSanityListener, BeforeAfterEvent.LOSE_SANITY);

        getService().hold();
        getService().gainAssetFromDeck(getInvestigatorId(), AssetId.RITUAL_DAGGER);
        getService().gainSpellFromDeck(getInvestigatorId(), SpellId.VOICE_OF_RA);
        getService().convertTo(InvestigatorId.class, () -> eventData);
        getService().registerNewInvestigatorAction(getInvestigatorId());
        getService().release();
    }

    @Override
    public void justRegisterListeners() {
        beforeSpendListener = new BeforeSpendListener();
        beforeLoseSanityListener = new BeforeLoseSanityListener();

        ServicePlatform.get().getEventQueue().addBeforeEventListener(beforeSpendListener, BeforeAfterEvent.SPEND);
        ServicePlatform.get().getEventQueue().addBeforeEventListener(beforeLoseSanityListener, BeforeAfterEvent.LOSE_SANITY);

    }

    @Override
    public void unregisterInvestigator() {
        ServicePlatform.get().getEventQueue().unregisterListener(beforeSpendListener);
        ServicePlatform.get().getEventQueue().unregisterListener(beforeLoseSanityListener);
        ServicePlatform.get().getEventQueue().unregisterListener(this);
    }

    private class BeforeSpendListener extends EventListenerImpl<SpendData> {


        @Override
        public void onNotify(SpendData eventData) {
            if (ServicePlatform.get().getInvestigators().getActiveInvestigatorId() != InvestigatorId.THE_ENTERTAINER) {
                return;
            }
            if (eventData.getTokenAmountMap().get(TokenType.SANITY) <= 0) {
                return;
            }
            if (TestFlavorType.SPELL != ServicePlatform.get().getTestFlavor().getFlavorType()) {
                return;
            }
            if (eventData.isSilent()) {
                updateMap(eventData.getTokenAmountMap());
            } else {
                askQuestion().subscribe(answer -> {
                    updateMap(eventData.getTokenAmountMap());
                    ServicePlatform.get().getService().convertTo(SpendData.class, () -> eventData);

                });
            }
        }


        private void updateMap(Map<TokenType, Integer> tokenMap) {
            Integer sanityRequired = tokenMap.get(TokenType.SANITY);
            tokenMap.put(TokenType.SANITY, sanityRequired - 1);
        }

        private Single<Answer<Boolean, Object>> askQuestion() {
            Question<Boolean> question = new Question<>();

            question.setTitle("Spend 1 fewer Sanity as part of Spell effect");
            question.setPortraitBeforeTitle(InvestigatorId.THE_ENTERTAINER);
            question.setOptions(Arrays.asList(
                    new Question.Option<>("OK", true)
            ));
            return ServicePlatform.get().getGameService().ask(question);
        }

        @Override
        public Class<SpendData> getDataClass() {
            return SpendData.class;
        }
    }

    private class BeforeLoseSanityListener extends EventListenerImpl<LoseTokenData> {

        @Override
        public void onNotify(LoseTokenData eventData) {
            if (ServicePlatform.get().getInvestigators().getActiveInvestigatorId() != InvestigatorId.THE_ENTERTAINER) {
                return;
            }
            if (eventData.getAmount() <= 0) {
                return;
            }
            if (TestFlavorType.SPELL != ServicePlatform.get().getTestFlavor().getFlavorType()) {
                return;
            }
            askQuestion().subscribe(answer -> {
                eventData.setAmount(Math.max(0, eventData.getAmount()-1));
                ServicePlatform.get().getService().convertTo(LoseTokenData.class, () -> eventData);
            });
        }

        private Single<Answer<Boolean, Object>> askQuestion() {
            Question<Boolean> question = new Question<>();

            question.setTitle("Lose 1 fewer Sanity as part of Spell effect");
            question.setPortraitBeforeTitle(InvestigatorId.THE_ENTERTAINER);
            question.setOptions(Arrays.asList(
                    new Question.Option<>("OK", true)
            ));
            return ServicePlatform.get().getGameService().ask(question);
        }

        @Override
        public Class<LoseTokenData> getDataClass() {
            return LoseTokenData.class;
        }
    }

}
