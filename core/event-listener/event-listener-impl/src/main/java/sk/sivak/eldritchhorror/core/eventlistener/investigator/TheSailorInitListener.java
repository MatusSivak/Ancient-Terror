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
import sk.sivak.eldritchhorror.core.constants.spell.SpellId;
import sk.sivak.eldritchhorror.core.eventlistener.EventListenerImpl;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestData;
import sk.sivak.eldritchhorror.core.model.InvestigatorRead;
import sk.sivak.eldritchhorror.core.model.InvestigatorsRead;

import java.util.Collections;

/**
 * @author msivak
 */
public class TheSailorInitListener extends AbstractInvestigatorInitListener {

    private TheSailorPassiveListener theSailorPassiveListener;

    @Override
    protected InvestigatorId getInvestigatorId() {
        return InvestigatorId.THE_SAILOR;
    }

    @Override
    protected void initInvestigator() {
        theSailorPassiveListener = new TheSailorPassiveListener();
        ServicePlatform.get().getEventQueue().addDirectEventListener(theSailorPassiveListener, DirectEvent.REGISTER_BONUS_DICE);
        getService().hold();
        getService().gainAssetFromDeck(getInvestigatorId(), AssetId.FISHING_NET);
        getService().convertTo(InvestigatorId.class, () -> eventData);
        getService().registerNewInvestigatorAction(getInvestigatorId());
        getService().release();
    }

    @Override
    public void justRegisterListeners() {
        theSailorPassiveListener = new TheSailorPassiveListener();
        ServicePlatform.get().getEventQueue().addDirectEventListener(theSailorPassiveListener, DirectEvent.REGISTER_BONUS_DICE);
    }

    @Override
    public void unregisterInvestigator() {
        ServicePlatform.get().getEventQueue().unregisterListener(theSailorPassiveListener);
        ServicePlatform.get().getEventQueue().unregisterListener(this);
    }

    private class TheSailorPassiveListener extends EventListenerImpl<TestData> {

        @Override
        public void onNotify(TestData eventData) {
            if (!isOnSeaSpaceWithSailor()) {
                return;
            }

            askQuestion().subscribe(answer -> {
                eventData.setAdditionalDicesCount(eventData.getAdditionalDicesCount() + 1);
                ServicePlatform.get().getService().convertTo(TestData.class, () -> eventData);
            });
        }

        private Single<Answer<Boolean, Object>> askQuestion() {
            Question<Boolean> question = new Question<>();

            question.setTitle("On a sea space, roll additional die");
            question.setPortraitBeforeTitle(InvestigatorId.THE_SAILOR);
            question.setOptions(Collections.singletonList(
                    new Question.Option<>("OK", true)

            ));
            return ServicePlatform.get().getGameService().ask(question);
        }


        private boolean isOnSeaSpaceWithSailor() {
            InvestigatorsRead investigators = ServicePlatform.get().getInvestigators();
            InvestigatorRead theSailor = investigators.getInvestigator(InvestigatorId.THE_SAILOR);
            LocationId theSailorLocation = theSailor.getCurrentLocationId();
            LocationId activeInvestigatorLocation = investigators.getActiveInvestigator().getCurrentLocationId();

            if (theSailorLocation != activeInvestigatorLocation) {
                return false;
            }
            if (theSailorLocation == null) {
                return false;
            }
            LocationInfo locationInfo = ServicePlatform.get().getLocationMap().getLocationInfo(theSailorLocation);
            return locationInfo.getLocationType() == LocationType.SEA;
        }

        @Override
        public Class<TestData> getDataClass() {
            return TestData.class;
        }
    }
}
