package sk.sivak.eldritchhorror.core.eventlistener.ancientone.cthulhu;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import sk.sivak.eldritchhorror.core.constants.ancientone.AncientOneId;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.constants.tracker.AnalyticsCategory;
import sk.sivak.eldritchhorror.core.constants.tracker.GoogleServicesHolder;
import sk.sivak.eldritchhorror.core.eventlistener.EventListenerImpl;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.ancientone.AncientOneInitListener;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.ReckoningFireType;
import sk.sivak.eldritchhorror.core.eventtype.data.investigator.DelayedData;
import sk.sivak.eldritchhorror.core.model.InvestigatorRead;

import java.util.List;

import static sk.sivak.eldritchhorror.core.constants.ViewProperties.VIEWPORT_HEIGHT;
import static sk.sivak.eldritchhorror.core.constants.ViewProperties.VIEWPORT_WIDTH;
import static sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils.getActiveInvestigatorId;

/**
 * @author msivak
 */
public class AwakenCthulhuInitListener extends AncientOneInitListener {

    private static final Logger logger = LogManager.getLogger(AwakenCthulhuInitListener.class);
    private ReckoningListener reckoningListener;
    private AfterMoveListener afterMoveListener;
    private BeforeAdvanceDoomListener beforeAdvanceDoomListener;
    private BeforeReplaceInvestigatorsListener beforeReplaceInvestigatorsListener;

    @Override
    public void register() {
        ServicePlatform.get().getGameService().showAncientOneCard();

        reckoningListener = new ReckoningListener();
        afterMoveListener = new AfterMoveListener();
        beforeAdvanceDoomListener = new BeforeAdvanceDoomListener();
        beforeReplaceInvestigatorsListener = new BeforeReplaceInvestigatorsListener();

        ServicePlatform.get().getEventQueue().addDirectEventListener(reckoningListener, DirectEvent.RECKONING_ANCIENT_ONE);
        ServicePlatform.get().getEventQueue().addAfterEventListener(afterMoveListener, BeforeAfterEvent.TRAVEL_TO_LOCATION);
        ServicePlatform.get().getEventQueue().addAfterEventListener(afterMoveListener, BeforeAfterEvent.SPAWN_INVESTIGATOR);
        ServicePlatform.get().getEventQueue().addBeforeEventListener(beforeAdvanceDoomListener, BeforeAfterEvent.ADVANCE_DOOM);
        ServicePlatform.get().getEventQueue().addBeforeEventListener(beforeReplaceInvestigatorsListener, BeforeAfterEvent.REPLACE_INVESTIGATORS);

    }

    @Override
    public void load() {
        reckoningListener = new ReckoningListener();
        afterMoveListener = new AfterMoveListener();
        beforeAdvanceDoomListener = new BeforeAdvanceDoomListener();
        beforeReplaceInvestigatorsListener = new BeforeReplaceInvestigatorsListener();

        ServicePlatform.get().getEventQueue().addDirectEventListener(reckoningListener, DirectEvent.RECKONING_ANCIENT_ONE);
        ServicePlatform.get().getEventQueue().addAfterEventListener(afterMoveListener, BeforeAfterEvent.TRAVEL_TO_LOCATION);
        ServicePlatform.get().getEventQueue().addAfterEventListener(afterMoveListener, BeforeAfterEvent.SPAWN_INVESTIGATOR);
        ServicePlatform.get().getEventQueue().addBeforeEventListener(beforeAdvanceDoomListener, BeforeAfterEvent.ADVANCE_DOOM);
        ServicePlatform.get().getEventQueue().addBeforeEventListener(beforeReplaceInvestigatorsListener, BeforeAfterEvent.REPLACE_INVESTIGATORS);
    }

    private class BeforeReplaceInvestigatorsListener extends EventListenerImpl<Object> {

        @Override
        public void onNotify(Object eventData) {
            if (ServicePlatform.get().getInvestigators().getToBeReplacedInvestigators().isEmpty()) {
                return;
            }
            Question<Object> question = new Question<>();
            question.setOptions(Question.Option.okOption);
            question.setTitle("Defeated investigators can't be replaced!");
            question.setPortraitBeforeTitle(AncientOneId.CTHULHU);

            ServicePlatform.get().getGameService().ask(question).subscribe(ok -> {
                ServicePlatform.get().getService().hold();
                if (ServicePlatform.get().getInvestigators().getPlayers() == 0) {
                    endGame();
                } else {
                    ServicePlatform.get().getService().skipAfterEvent(BeforeAfterEvent.REPLACE_INVESTIGATORS, null);
                }
                ServicePlatform.get().getService().release();
            });
        }

        private void endGame() {
            ServicePlatform.get().getService().hold();
            String endGameText = ServicePlatform.get().getModel().getAncientOne().getAncientOneInfo().getEndGameText();
            GoogleServicesHolder.getAnalyticsTracker().trackInteraction(AnalyticsCategory.GAME_ENDED, "CTHULHU", "defeat");
            ServicePlatform.get().getTutorialService().displayChalkboard(endGameText,
                    VIEWPORT_WIDTH/2, VIEWPORT_HEIGHT/2, true);
            ServicePlatform.get().getService().addEventCommand(in -> {
                ServicePlatform.get().getGameService().restartGame() ;
            });
            ServicePlatform.get().getService().release();
        }

        @Override
        public Class<Object> getDataClass() {
            return Object.class;
        }
    }

    private class BeforeAdvanceDoomListener extends EventListenerImpl<Integer> {

        @Override
        public void onNotify(Integer amount) {
            Question<Object> question = new Question<>();
            question.setOptions(Question.Option.okOption);
            question.setTitle("Cthulhu's power rises!");
            question.setPortraitBeforeTitle(AncientOneId.CTHULHU);
            ServicePlatform.get().getGameService().ask(question).subscribe(ok -> {
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getDoomOmenService().increaseAncientOnePower(amount);
                ServicePlatform.get().getService().skipAfterEvent(BeforeAfterEvent.ADVANCE_DOOM, null);
                ServicePlatform.get().getService().release();
            });
        }

        @Override
        public Class<Integer> getDataClass() {
            return Integer.class;
        }
    }

    private class ReckoningListener extends EventListenerImpl<ReckoningFireType> {

        @Override
        public void onNotify(ReckoningFireType eventData) {
            if (eventData == ReckoningFireType.CHARGE) {
                return;
            }
            List<? extends InvestigatorRead> investigators = ServicePlatform.get().getInvestigators().getOnBoardInvestigators();
            if (investigators.isEmpty()) {
                return;
            }
            int power = ServicePlatform.get().getModel().getAncientOne().getAncientOneInfo().getPower();
            if (power == 0) {
                return;
            }
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getDoomOmenService().onTriggeredReckoning();
            Question<Object> question = new Question<>();
            question.setOptions(Question.Option.okOption);
            question.setTitle("Each investigator loses "+ power+" Sanity");
            question.setPortraitBeforeTitle(AncientOneId.CTHULHU);
            ServicePlatform.get().getGameService().ask(question).subscribe(ok -> {
                for (InvestigatorRead investigator : investigators) {
                    ServicePlatform.get().getService().hold();
                    ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(investigator.getInfo().getInvestigatorId());
                    ServicePlatform.get().getInvestigatorService().showActiveInvestigator(false);
                    ServicePlatform.get().getTokenService().loseSanity(power);
                    ServicePlatform.get().getService().release();
                }
            });
            ServicePlatform.get().getService().release();
        }

        @Override
        public Class<ReckoningFireType> getDataClass() {
            return ReckoningFireType.class;
        }
    }

    private class AfterMoveListener extends EventListenerImpl<InvestigatorId> {

        @Override
        public void onNotify(InvestigatorId investigatorId) {
            if (investigatorId == null) {
                return;
            }
            InvestigatorRead activeInvestigator = ServicePlatform.get().getInvestigators().getInvestigator(investigatorId);
            if (activeInvestigator.getCurrentLocationId() == null) {
                return;
            }
            List<LocationId> spawnedVortexes = ServicePlatform.get().getVortexes().getSpawnedVortexes();
            if (!spawnedVortexes.contains(activeInvestigator.getCurrentLocationId())) {
                return;
            }
            Question<Object> confirmation = new Question<>();
            confirmation.setPortraitBeforeTitle(AncientOneId.CTHULHU);
            confirmation.setTitle("The " + getActiveInvestigatorId().toString() + " becomes Delayed and loses one Sanity. Cthulhu's power rises!");
            confirmation.setOptions(Question.Option.okOption);
            ServicePlatform.get().getGameService().ask(confirmation).subscribe(ok -> {
                ServicePlatform.get().getService().hold();
                DelayedData delayedData = new DelayedData(getActiveInvestigatorId(), true);
                ServicePlatform.get().getInvestigatorService().becomeDelayed(delayedData);
                ServicePlatform.get().getTokenService().loseSanity(1);
                ServicePlatform.get().getDoomOmenService().increaseAncientOnePower(1);
                ServicePlatform.get().getService().convertTo(Void.class, () -> null);
                ServicePlatform.get().getService().release();
            });
        }

        @Override
        public Class<InvestigatorId> getDataClass() {
            return InvestigatorId.class;
        }
    }
}
