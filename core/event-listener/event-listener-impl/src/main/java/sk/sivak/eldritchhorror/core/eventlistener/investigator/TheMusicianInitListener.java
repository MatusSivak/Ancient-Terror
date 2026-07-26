package sk.sivak.eldritchhorror.core.eventlistener.investigator;

import rx.Single;
import sk.sivak.eldritchhorror.core.constants.artifact.ArtifactId;
import sk.sivak.eldritchhorror.core.constants.asset.AssetId;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.question.Answer;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.constants.spell.SpellId;
import sk.sivak.eldritchhorror.core.constants.test.TestFlavorType;
import sk.sivak.eldritchhorror.core.eventlistener.EventListenerImpl;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestData;
import sk.sivak.eldritchhorror.core.model.InvestigatorRead;
import sk.sivak.eldritchhorror.core.model.InvestigatorsRead;

import java.util.Collections;

/**
 * @author msivak
 */
public class TheMusicianInitListener extends AbstractInvestigatorInitListener {

    private TheMusicianPassiveListener theMusicianPassiveListener;

    @Override
    protected InvestigatorId getInvestigatorId() {
        return InvestigatorId.THE_MUSICIAN;
    }

    @Override
    protected void initInvestigator() {
        theMusicianPassiveListener = new TheMusicianPassiveListener();
        ServicePlatform.get().getEventQueue().addDirectEventListener(theMusicianPassiveListener, DirectEvent.REGISTER_BONUS_DICE);
        getService().hold();
        getService().gainSpellFromDeck(getInvestigatorId(), SpellId.SHRIVELING);
        ServicePlatform.get().getTokenService().gainClueFromPool(InvestigatorId.THE_MUSICIAN);

        getService().convertTo(InvestigatorId.class, () -> eventData);
        getService().registerNewInvestigatorAction(getInvestigatorId());
        getService().release();
    }

    @Override
    public void justRegisterListeners() {
        theMusicianPassiveListener = new TheMusicianPassiveListener();
        ServicePlatform.get().getEventQueue().addDirectEventListener(theMusicianPassiveListener, DirectEvent.REGISTER_BONUS_DICE);
    }

    @Override
    public void unregisterInvestigator() {
        ServicePlatform.get().getEventQueue().unregisterListener(theMusicianPassiveListener);
        ServicePlatform.get().getEventQueue().unregisterListener(this);
    }

    private class TheMusicianPassiveListener extends EventListenerImpl<TestData> {

        @Override
        public void onNotify(TestData eventData) {
            if (!isCombatWithMusician()) {
                return;
            }

            askQuestion().subscribe(answer -> {
                eventData.setAdditionalDicesCount(eventData.getAdditionalDicesCount() + 1);
                ServicePlatform.get().getService().convertTo(TestData.class, () -> eventData);
            });
        }

        private Single<Answer<Boolean, Object>> askQuestion() {
            Question<Boolean> question = new Question<>();

            question.setTitle("During Combat Encounters, roll additional die");
            question.setPortraitBeforeTitle(InvestigatorId.THE_MUSICIAN);
            question.setOptions(Collections.singletonList(
                    new Question.Option<>("OK", true)

            ));
            return ServicePlatform.get().getGameService().ask(question);
        }


        private boolean isCombatWithMusician() {
            if (ServicePlatform.get().getTestFlavor().getFlavorType() != TestFlavorType.COMBAT) {
                return false;
            }
            InvestigatorsRead investigators = ServicePlatform.get().getInvestigators();
            InvestigatorRead theMusician = investigators.getInvestigator(InvestigatorId.THE_MUSICIAN);
            LocationId theMusicianLocation = theMusician.getCurrentLocationId();
            LocationId activeInvestigatorLocation = investigators.getActiveInvestigator().getCurrentLocationId();
            if (theMusicianLocation == null) {
                return false;
            }
            return theMusicianLocation == activeInvestigatorLocation;
        }

        @Override
        public Class<TestData> getDataClass() {
            return TestData.class;
        }
    }
}
