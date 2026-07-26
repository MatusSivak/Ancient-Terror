package sk.sivak.eldritchhorror.core.eventlistener.investigator;

import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.question.Answer;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.constants.spell.SpellId;
import sk.sivak.eldritchhorror.core.eventlistener.EventListenerImpl;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.CloseGateData;
import sk.sivak.eldritchhorror.core.eventtype.data.basic.SelectLocationData;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import static sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils.getActiveInvestigatorId;

/**
 * @author msivak
 */
public class TheViolinistInitListener extends AbstractInvestigatorInitListener {

    private TheViolinistPassiveListener theViolinistPassiveListener;

    @Override
    protected InvestigatorId getInvestigatorId() {
        return InvestigatorId.THE_VIOLINIST;
    }

    @Override
    protected void initInvestigator() {
        justRegisterListeners();
        getService().hold();
        ServicePlatform.get().getService().gainSpellFromDeck(InvestigatorId.THE_VIOLINIST, SpellId.BANISHMENT);
        ServicePlatform.get().getTokenService().gainClueFromPool(InvestigatorId.THE_VIOLINIST);
        getService().convertTo(Object.class, () -> eventData);
        getService().registerNewInvestigatorAction(getInvestigatorId());
        getService().release();
    }

    @Override
    public void justRegisterListeners() {
        theViolinistPassiveListener = new TheViolinistPassiveListener();
        ServicePlatform.get().getEventQueue().addAfterEventListener(theViolinistPassiveListener, BeforeAfterEvent.CLOSE_GATE);
    }

    @Override
    public void unregisterInvestigator() {
        ServicePlatform.get().getEventQueue().unregisterListener(theViolinistPassiveListener);
        ServicePlatform.get().getEventQueue().unregisterListener(this);
    }

    private class TheViolinistPassiveListener extends EventListenerImpl<CloseGateData> {

        @Override
        public void onNotify(CloseGateData eventData) {
            if (!eventData.isOtherworldEncounter()) {
                return;
            }
            if (ServicePlatform.get().getInvestigators().getActiveInvestigatorId() != InvestigatorId.THE_VIOLINIST) {
                return;
            }

            Question<Object> question = new Question<>();

            question.setTitle("Gain one Clue and one Focus.");
            question.setPortraitBeforeTitle(InvestigatorId.THE_VIOLINIST);
            question.setOptions(Question.Option.okOption);
            ServicePlatform.get().getGameService().ask(question).subscribe(answer -> onAnswer(eventData));
        }

        private void onAnswer(CloseGateData eventData) {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getTokenService().gainClueFromPool();
            ServicePlatform.get().getTokenService().gainFocus();
            ServicePlatform.get().getInvestigatorService().convertTo(CloseGateData.class, () -> eventData);
            ServicePlatform.get().getService().release();
        }

        @Override
        public Class<CloseGateData> getDataClass() {
            return CloseGateData.class;
        }
    }
}
