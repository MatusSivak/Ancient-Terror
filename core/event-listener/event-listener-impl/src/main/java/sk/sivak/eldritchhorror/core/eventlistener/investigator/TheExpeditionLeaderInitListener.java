package sk.sivak.eldritchhorror.core.eventlistener.investigator;

import rx.Single;
import sk.sivak.eldritchhorror.core.constants.artifact.ArtifactId;
import sk.sivak.eldritchhorror.core.constants.asset.AssetId;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.location.LocationInfo;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.constants.question.Answer;
import sk.sivak.eldritchhorror.core.constants.question.Question;
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
public class TheExpeditionLeaderInitListener extends AbstractInvestigatorInitListener {

    private TheExpeditionLeaderPassiveListener theExpeditionLeaderPassiveListener;

    @Override
    protected InvestigatorId getInvestigatorId() {
        return InvestigatorId.THE_EXPEDITION_LEADER;
    }

    @Override
    protected void initInvestigator() {
        theExpeditionLeaderPassiveListener = new TheExpeditionLeaderPassiveListener();
        ServicePlatform.get().getEventQueue().addDirectEventListener(theExpeditionLeaderPassiveListener, DirectEvent.REGISTER_BONUS_DICE);
        getService().hold();
        getService().gainAssetFromDeck(getInvestigatorId(), AssetId.HIRED_MUSCLE);
        getService().convertTo(InvestigatorId.class, () -> eventData);
        getService().registerNewInvestigatorAction(getInvestigatorId());
        getService().release();
    }

    @Override
    public void justRegisterListeners() {
        theExpeditionLeaderPassiveListener = new TheExpeditionLeaderPassiveListener();
        ServicePlatform.get().getEventQueue().addDirectEventListener(theExpeditionLeaderPassiveListener, DirectEvent.REGISTER_BONUS_DICE);
    }

    @Override
    public void unregisterInvestigator() {
        ServicePlatform.get().getEventQueue().unregisterListener(theExpeditionLeaderPassiveListener);
        ServicePlatform.get().getEventQueue().unregisterListener(this);
    }

    private class TheExpeditionLeaderPassiveListener extends EventListenerImpl<TestData> {

        @Override
        public void onNotify(TestData eventData) {
            if (!isOnWildernessSpaceWithExpeditionLeader()) {
                return;
            }

            askQuestion().subscribe(answer -> {
                eventData.setAdditionalDicesCount(eventData.getAdditionalDicesCount() + 1);
                ServicePlatform.get().getService().convertTo(TestData.class, () -> eventData);
            });
        }

        private Single<Answer<Boolean, Object>> askQuestion() {
            Question<Boolean> question = new Question<>();

            question.setTitle("On a wilderness space, roll additional die.");
            question.setPortraitBeforeTitle(InvestigatorId.THE_EXPEDITION_LEADER);
            question.setOptions(Collections.singletonList(
                    new Question.Option<>("OK", true)

            ));
            return ServicePlatform.get().getGameService().ask(question);
        }


        private boolean isOnWildernessSpaceWithExpeditionLeader() {
            InvestigatorsRead investigators = ServicePlatform.get().getInvestigators();
            InvestigatorRead theExpeditionLeader = investigators.getInvestigator(InvestigatorId.THE_EXPEDITION_LEADER);
            LocationId theExpeditionLeaderLocation = theExpeditionLeader.getCurrentLocationId();
            LocationId activeInvestigatorLocation = investigators.getActiveInvestigator().getCurrentLocationId();

            if (theExpeditionLeaderLocation != activeInvestigatorLocation) {
                return false;
            }
            LocationInfo locationInfo = ServicePlatform.get().getLocationMap().getLocationInfo(theExpeditionLeaderLocation);
            return locationInfo.getLocationType() == LocationType.WILDERNESS;
        }

        @Override
        public Class<TestData> getDataClass() {
            return TestData.class;
        }
    }
}
