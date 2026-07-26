package sk.sivak.eldritchhorror.core.eventlistener.investigator;

import rx.Single;
import sk.sivak.eldritchhorror.core.constants.asset.AssetId;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.question.Answer;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.eventlistener.EventListenerImpl;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.GainAssetFromDeckData;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestData;
import sk.sivak.eldritchhorror.core.model.InvestigatorRead;
import sk.sivak.eldritchhorror.core.model.InvestigatorsRead;

import static sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils.getActiveInvestigatorId;

/**
 * @author msivak
 */
public class TheActressInitListener extends AbstractInvestigatorInitListener {

    private ReenableDisabledAbility reenableDisabledAbility;
    private ImproveSkillListener improveSkillListener;
    private TheActressPassiveListener theActressPassiveListener;

    @Override
    protected InvestigatorId getInvestigatorId() {
        return InvestigatorId.THE_ACTRESS;
    }


    @Override
    protected void initInvestigator() {
        improveSkillListener = new ImproveSkillListener();
        ServicePlatform.get().getEventQueue().addAfterEventListener(improveSkillListener, BeforeAfterEvent.SHOW_PHASE);

        reenableDisabledAbility = new ReenableDisabledAbility();
        ServicePlatform.get().getEventQueue().addDirectEventListener(reenableDisabledAbility, DirectEvent.REENABLE_DISABLED_ABILITIES);

        theActressPassiveListener = new TheActressPassiveListener();
        ServicePlatform.get().getEventQueue().addDirectEventListener(theActressPassiveListener, DirectEvent.REGISTER_BONUS_DICE);

        getService().hold();
        getService().gainAssetFromDeck(getInvestigatorId(), AssetId.DOT_18_DERRINGER);
        getService().convertFromTo(GainAssetFromDeckData.class, InvestigatorId.class, (in) -> eventData);
        getService().registerNewInvestigatorAction(getInvestigatorId());
        getService().release();
    }

    @Override
    public void justRegisterListeners() {
        reenableDisabledAbility = new ReenableDisabledAbility();
        theActressPassiveListener = new TheActressPassiveListener();
        ServicePlatform.get().getEventQueue().addDirectEventListener(reenableDisabledAbility, DirectEvent.REENABLE_DISABLED_ABILITIES);
        ServicePlatform.get().getEventQueue().addDirectEventListener(theActressPassiveListener, DirectEvent.REGISTER_BONUS_DICE);
    }

    @Override
    public void unregisterInvestigator() {
        ServicePlatform.get().getEventQueue().unregisterListener(improveSkillListener);
        ServicePlatform.get().getEventQueue().unregisterListener(reenableDisabledAbility);
        ServicePlatform.get().getEventQueue().unregisterListener(theActressPassiveListener);
        ServicePlatform.get().getEventQueue().unregisterListener(this);
    }

    private class ReenableDisabledAbility extends EventListenerImpl<Void> {

        @Override
        public void onNotify(Void eventData) {
            ServicePlatform.get().getInvestigators().getInvestigator(InvestigatorId.THE_ACTRESS).setPassiveAbilityDisabled(false);
        }

        @Override
        public Class<Void> getDataClass() {
            return Void.class;
        }
    }

    private class ImproveSkillListener extends EventListenerImpl<Void> {

        @Override
        public void onNotify(Void eventData) {
            ServicePlatform.get().getService().addEventCommand(in -> {
                InvestigatorId activeInvestigatorId = ServicePlatform.get().getInvestigators().getActiveInvestigatorId();
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(InvestigatorId.THE_ACTRESS);
                ServicePlatform.get().getInvestigatorService().showActiveInvestigator(true);
                Question<Object> question = new Question<>();
                question.setTitle("The Actress starts with one skill improved.");
                question.setPortraitBeforeTitle(InvestigatorId.THE_ACTRESS);
                question.setOptions(Question.Option.okOption);
                ServicePlatform.get().getGameService().ask(question).subscribe(answer -> {
                    ServicePlatform.get().getService().hold();
                    ServicePlatform.get().getService().convertTo(Stat.class, () -> null);
                    ServicePlatform.get().getInvestigatorService().improveSkill(null);
                    ServicePlatform.get().getEventQueue().unregisterListener(this);
                    ServicePlatform.get().getService().convertTo(Void.class, () -> null);
                    ServicePlatform.get().getService().release();
                });
                ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(activeInvestigatorId);
                ServicePlatform.get().getService().release();
            });
        }

        @Override
        public Class<Void> getDataClass() {
            return Void.class;
        }
    }

    private class TheActressPassiveListener extends EventListenerImpl<TestData> {

        @Override
        public void onNotify(TestData eventData) {
            if (!isOnSpaceWithActressLeader()) {
                return;
            }
            if (ServicePlatform.get().getInvestigators().getInvestigator(InvestigatorId.THE_ACTRESS).isPassiveAbilityDisabled()) {
                return;
            }

            askQuestion().subscribe(answer -> {
                if (!answer.getResponseData()) {
                    ServicePlatform.get().getService().convertTo(TestData.class, () -> eventData);
                    return;
                }
                eventData.setAdditionalDicesCount(eventData.getAdditionalDicesCount() + 1);
                ServicePlatform.get().getInvestigators().getInvestigator(InvestigatorId.THE_ACTRESS).setPassiveAbilityDisabled(true);
                ServicePlatform.get().getService().convertTo(TestData.class, () -> eventData);
            });
        }

        private Single<Answer<Boolean, Object>> askQuestion() {
            Question<Boolean> question = new Question<>();

            question.setTitle("Roll additional die? (Once per round)");
            question.setPortraitBeforeTitle(InvestigatorId.THE_ACTRESS);
            question.setOptions(Question.Option.noYesOptions);
            return ServicePlatform.get().getGameService().ask(question);
        }


        private boolean isOnSpaceWithActressLeader() {
            InvestigatorsRead investigators = ServicePlatform.get().getInvestigators();
            InvestigatorRead theActress = investigators.getInvestigator(InvestigatorId.THE_ACTRESS);
            LocationId theActressLocation = theActress.getCurrentLocationId();
            LocationId activeInvestigatorLocation = investigators.getActiveInvestigator().getCurrentLocationId();

            return theActressLocation == activeInvestigatorLocation;
        }

        @Override
        public Class<TestData> getDataClass() {
            return TestData.class;
        }
    }

}
