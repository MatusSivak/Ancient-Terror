package sk.sivak.eldritchhorror.core.eventlistener.investigator;

import sk.sivak.eldritchhorror.core.constants.artifact.ArtifactId;
import sk.sivak.eldritchhorror.core.constants.asset.AssetId;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.constants.spell.SpellId;
import sk.sivak.eldritchhorror.core.eventlistener.EventListenerImpl;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.GainAssetFromDeckData;
import sk.sivak.eldritchhorror.core.eventtype.data.test.CountRerollDiceData;
import sk.sivak.eldritchhorror.core.eventtype.data.test.RerollUsingData;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestData;
import sk.sivak.eldritchhorror.core.model.InvestigatorRead;
import sk.sivak.eldritchhorror.core.model.InvestigatorsRead;

/**
 * @author msivak
 */
public class TheSpyInitListener extends AbstractInvestigatorInitListener {

    private TheSpyPassiveListener theSpyPassiveListener;
    private BeforeRerollUsingClue beforeRerollUsingClue;

    @Override
    protected InvestigatorId getInvestigatorId() {
        return InvestigatorId.THE_SPY;
    }

    private boolean wantsToUseSpecialAsked = false;

    @Override
    protected void initInvestigator() {
        theSpyPassiveListener = new TheSpyPassiveListener();
        beforeRerollUsingClue = new BeforeRerollUsingClue();

        ServicePlatform.get().getEventQueue().addBeforeEventListener(theSpyPassiveListener, BeforeAfterEvent.COUNT_REROLL_DICE);
        ServicePlatform.get().getEventQueue().addBeforeEventListener(beforeRerollUsingClue, BeforeAfterEvent.REROLL_USING_CLUE);

        getService().hold();
        getService().gainAssetFromDeck(getInvestigatorId(), AssetId.DOT_45_AUTOMATIC);
        getService().convertFromTo(GainAssetFromDeckData.class, InvestigatorId.class, (in) -> eventData);
        getService().registerNewInvestigatorAction(getInvestigatorId());
        getService().release();
    }

    @Override
    public void justRegisterListeners() {
        theSpyPassiveListener = new TheSpyPassiveListener();
        beforeRerollUsingClue = new BeforeRerollUsingClue();

        ServicePlatform.get().getEventQueue().addBeforeEventListener(theSpyPassiveListener, BeforeAfterEvent.COUNT_REROLL_DICE);
        ServicePlatform.get().getEventQueue().addBeforeEventListener(beforeRerollUsingClue, BeforeAfterEvent.REROLL_USING_CLUE);
    }

    @Override
    public void unregisterInvestigator() {
        ServicePlatform.get().getEventQueue().unregisterListener(theSpyPassiveListener);
        ServicePlatform.get().getEventQueue().unregisterListener(beforeRerollUsingClue);
        ServicePlatform.get().getEventQueue().unregisterListener(this);
    }

    private class BeforeRerollUsingClue extends EventListenerImpl<RerollUsingData> {

        @Override
        public void onNotify(RerollUsingData eventData) {
            TestData testData = eventData.getTestData();
            if (testData.hasReachedMinScoreToEndTest()) {
                wantsToUseSpecialAsked = false;
                return;
            }
            if (testData.getCalculatedDicePool() <= testData.getScore()) {
                wantsToUseSpecialAsked = false;
                return;
            }
            if (!isOnSpaceWithSpy()) {
                wantsToUseSpecialAsked = false;
                return;
            }
            if (testData.getCalculatedDicePool() - testData.getSuccessRolls() < 2) { // 5 vs 4 -> return, 5 vs 3 go
                wantsToUseSpecialAsked = false;
                return;
            }

            Question<Boolean> question = new Question<>();
            question.setTitle("Reroll two dice with Clue?");
            question.setPortraitBeforeTitle(InvestigatorId.THE_SPY);

            wantsToUseSpecialAsked = true;
            eventData.setQuestion(question);
        }

        private boolean isOnSpaceWithSpy() {
            InvestigatorsRead investigators = ServicePlatform.get().getInvestigators();
            InvestigatorRead theSpy = investigators.getInvestigator(InvestigatorId.THE_SPY);
            LocationId theSpyLocation = theSpy.getCurrentLocationId();
            if (theSpyLocation == null) {
                return false;
            }
            LocationId activeInvestigatorLocation = investigators.getActiveInvestigator().getCurrentLocationId();
            return theSpyLocation == activeInvestigatorLocation;
        }

        @Override
        public Class<RerollUsingData> getDataClass() {
            return RerollUsingData.class;
        }
    }
    private class TheSpyPassiveListener extends EventListenerImpl<CountRerollDiceData> {

        @Override
        public void onNotify(CountRerollDiceData eventData) {
            if (eventData.getCountRerollDiceType() != CountRerollDiceData.CountRerollDiceType.CLUE) {
                wantsToUseSpecialAsked = false;
                return;
            }
            if (wantsToUseSpecialAsked) {
                eventData.setCount(eventData.getCount() + 1);
                wantsToUseSpecialAsked = false;
            }
        }


        @Override
        public Class<CountRerollDiceData> getDataClass() {
            return CountRerollDiceData.class;
        }
    }

}
