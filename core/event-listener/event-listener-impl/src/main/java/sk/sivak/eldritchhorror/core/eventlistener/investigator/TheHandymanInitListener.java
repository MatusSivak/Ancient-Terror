package sk.sivak.eldritchhorror.core.eventlistener.investigator;

import sk.sivak.eldritchhorror.core.constants.asset.AssetId;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.question.Question;
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
public class TheHandymanInitListener extends AbstractInvestigatorInitListener {

    private TheHandymanPassiveListener theHandymanPassiveListener;
    private BeforeRerollUsingFocus beforeRerollUsingFocus;

    @Override
    protected InvestigatorId getInvestigatorId() {
        return InvestigatorId.THE_HANDYMAN;
    }

    private boolean wantsToUseSpecialAsked = false;

    @Override
    protected void initInvestigator() {
        theHandymanPassiveListener = new TheHandymanPassiveListener();
        beforeRerollUsingFocus = new BeforeRerollUsingFocus();

        ServicePlatform.get().getEventQueue().addBeforeEventListener(theHandymanPassiveListener, BeforeAfterEvent.COUNT_REROLL_DICE);
        ServicePlatform.get().getEventQueue().addBeforeEventListener(beforeRerollUsingFocus, BeforeAfterEvent.REROLL_USING_FOCUS);

        getService().hold();
        getService().gainAssetFromDeck(InvestigatorId.THE_HANDYMAN, AssetId.BLUNDERBUSS);
        getService().convertTo(Object.class, () -> eventData);
        getService().registerNewInvestigatorAction(getInvestigatorId());
        getService().release();
    }

    @Override
    public void justRegisterListeners() {
        theHandymanPassiveListener = new TheHandymanPassiveListener();
        beforeRerollUsingFocus = new BeforeRerollUsingFocus();

        ServicePlatform.get().getEventQueue().addBeforeEventListener(theHandymanPassiveListener, BeforeAfterEvent.COUNT_REROLL_DICE);
        ServicePlatform.get().getEventQueue().addBeforeEventListener(beforeRerollUsingFocus, BeforeAfterEvent.REROLL_USING_FOCUS);
    }

    @Override
    public void unregisterInvestigator() {
        ServicePlatform.get().getEventQueue().unregisterListener(theHandymanPassiveListener);
        ServicePlatform.get().getEventQueue().unregisterListener(beforeRerollUsingFocus);
        ServicePlatform.get().getEventQueue().unregisterListener(this);
    }

    private class BeforeRerollUsingFocus extends EventListenerImpl<RerollUsingData> {

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
            if (!isOnSpaceWithHandyman()) {
                wantsToUseSpecialAsked = false;
                return;
            }
            if (testData.getCalculatedDicePool() - testData.getSuccessRolls() < 2) { // 5 vs 4 -> return, 5 vs 3 go
                wantsToUseSpecialAsked = false;
                return;
            }

            Question<Boolean> question = new Question<>();
            question.setTitle("Reroll two dice with Focus?");
            question.setPortraitBeforeTitle(InvestigatorId.THE_HANDYMAN);

            wantsToUseSpecialAsked = true;
            eventData.setQuestion(question);
        }

        private boolean isOnSpaceWithHandyman() {
            InvestigatorsRead investigators = ServicePlatform.get().getInvestigators();
            InvestigatorRead theHandyman = investigators.getInvestigator(InvestigatorId.THE_HANDYMAN);
            LocationId theHandymanLocation = theHandyman.getCurrentLocationId();
            if (theHandymanLocation == null) {
                return false;
            }
            LocationId activeInvestigatorLocation = investigators.getActiveInvestigator().getCurrentLocationId();
            return theHandymanLocation == activeInvestigatorLocation;
        }

        @Override
        public Class<RerollUsingData> getDataClass() {
            return RerollUsingData.class;
        }
    }
    private class TheHandymanPassiveListener extends EventListenerImpl<CountRerollDiceData> {

        @Override
        public void onNotify(CountRerollDiceData eventData) {
            if (eventData.getCountRerollDiceType() != CountRerollDiceData.CountRerollDiceType.FOCUS) {
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
