package sk.sivak.eldritchhorror.core.eventlistener.ancientone.shubniggurath;

import java8.features.stream.Stream;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import sk.sivak.eldritchhorror.core.constants.ancientone.AncientOneId;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.constants.monster.epic.EpicMonsterId;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.constants.tracker.AnalyticsCategory;
import sk.sivak.eldritchhorror.core.constants.tracker.GoogleServicesHolder;
import sk.sivak.eldritchhorror.core.eventlistener.EventListenerImpl;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.ancientone.AncientOneInitListener;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.ReckoningFireType;
import sk.sivak.eldritchhorror.core.eventtype.data.SpawnMonsterData;
import sk.sivak.eldritchhorror.core.model.InvestigatorRead;

import java.util.List;

import static sk.sivak.eldritchhorror.core.constants.ViewProperties.VIEWPORT_HEIGHT;
import static sk.sivak.eldritchhorror.core.constants.ViewProperties.VIEWPORT_WIDTH;

/**
 * @author msivak
 */
public class AwakenShubNiggurathInitListener extends AncientOneInitListener {

    private static final Logger logger = LogManager.getLogger(AwakenShubNiggurathInitListener.class);
    private ReckoningListener reckoningListener;

    private BeforeAdvanceDoomListener beforeAdvanceDoomListener;
    private BeforeReplaceInvestigatorsListener beforeReplaceInvestigatorsListener;

    @Override
    public void register() {
        ServicePlatform.get().getGameService().showAncientOneCard();

        reckoningListener = new ReckoningListener();
        beforeAdvanceDoomListener = new BeforeAdvanceDoomListener();
        beforeReplaceInvestigatorsListener = new BeforeReplaceInvestigatorsListener();

        ServicePlatform.get().getEventQueue().addDirectEventListener(reckoningListener, DirectEvent.RECKONING_ANCIENT_ONE);
        ServicePlatform.get().getEventQueue().addBeforeEventListener(beforeAdvanceDoomListener, BeforeAfterEvent.ADVANCE_DOOM);
        ServicePlatform.get().getEventQueue().addBeforeEventListener(beforeReplaceInvestigatorsListener, BeforeAfterEvent.REPLACE_INVESTIGATORS);
    }

    @Override
    public void load() {
        reckoningListener = new ReckoningListener();
        beforeAdvanceDoomListener = new BeforeAdvanceDoomListener();
        beforeReplaceInvestigatorsListener = new BeforeReplaceInvestigatorsListener();

        ServicePlatform.get().getEventQueue().addDirectEventListener(reckoningListener, DirectEvent.RECKONING_ANCIENT_ONE);
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
            question.setPortraitBeforeTitle(AncientOneId.SHUB_NIGGURATH);

            ServicePlatform.get().getGameService().ask(question).subscribe(ok -> {
                ServicePlatform.get().getService().hold();
                if (ServicePlatform.get().getInvestigators().getPlayers() == 0) {
                    endGame("defeat");
                } else {
                    ServicePlatform.get().getService().skipAfterEvent(BeforeAfterEvent.REPLACE_INVESTIGATORS, null);
                }
                ServicePlatform.get().getService().release();
            });
        }

        @Override
        public Class<Object> getDataClass() {
            return Object.class;
        }
    }

    private class BeforeAdvanceDoomListener extends EventListenerImpl<Integer> {

        @Override
        public void onNotify(Integer amount) {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getService().moveCameraToLocation(LocationId.THE_HEART_OF_AFRICA);
            Question<Object> question = new Question<>();
            question.setOptions(Question.Option.okOption);
            question.setTitle("Shub-Niggurath spawns another Monster!");
            question.setPortraitBeforeTitle(AncientOneId.SHUB_NIGGURATH);
            ServicePlatform.get().getGameService().ask(question).subscribe(ok -> {
                ServicePlatform.get().getService().hold();
                SpawnMonsterData spawnMonsterData = new SpawnMonsterData();
                spawnMonsterData.setLocationId(LocationId.THE_HEART_OF_AFRICA);
                ServicePlatform.get().getGameService().spawnMonster(spawnMonsterData);
                checkMonsterCount();
                ServicePlatform.get().getService().skipAfterEvent(BeforeAfterEvent.ADVANCE_DOOM, null);
                ServicePlatform.get().getService().release();
            });
            ServicePlatform.get().getService().release();
        }

        private void checkMonsterCount() {
            ServicePlatform.get().getService().addEventCommand(in -> {
                List<MonsterInfo> monstersAtLocation = ServicePlatform.get().getMonsterCup().getMonstersAtLocation(LocationId.THE_HEART_OF_AFRICA);
                if (monstersAtLocation.size() < 6) {
                    return;
                }
                Question<Object> question = new Question<>();
                question.setOptions(Question.Option.okOption);
                question.setTitle("There are five or more monsters with Shub-Niggurath!");
                question.setPortraitBeforeTitle(AncientOneId.SHUB_NIGGURATH);
                ServicePlatform.get().getGameService().ask(question).subscribe(ok -> {
                    endGame("monsters");
                });
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

            List<? extends InvestigatorRead> investigatorsAtHisLocation = Stream.collectToList(investigators,
                    investigator -> investigator.getCurrentLocationId() == LocationId.THE_HEART_OF_AFRICA);

            if (investigatorsAtHisLocation.isEmpty()) {
                return;
            }

            ServicePlatform.get().getDoomOmenService().onTriggeredReckoning();
            for (InvestigatorRead investigatorRead : investigatorsAtHisLocation) {
                ServicePlatform.get().getService().addEventCommand(in -> {
                    ServicePlatform.get().getService().hold();
                    ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(investigatorRead.getInfo().getInvestigatorId());
                    ServicePlatform.get().getInvestigatorService().showActiveInvestigator(false);
                    Question<Object> question = new Question<>();
                    question.setOptions(Question.Option.okOption);
                    question.setTitle("The " + investigatorRead.getInfo().getInvestigatorId().toString() +" encounters Shub-Niggurath!");
                    question.setPortraitBeforeTitle(AncientOneId.SHUB_NIGGURATH);
                    ServicePlatform.get().getGameService().ask(question).subscribe(ok -> {
                        ServicePlatform.get().getService().hold();
                        ServicePlatform.get().getTestService().combat(findShubNiggurath());
                        ServicePlatform.get().getGameService().checkIfSomeoneIsDefeated();
                        ServicePlatform.get().getService().release();
                    });
                    ServicePlatform.get().getService().release();
                });
            }
        }

        @Override
        public Class<ReckoningFireType> getDataClass() {
            return ReckoningFireType.class;
        }
    }

    private void endGame(String label) {
        ServicePlatform.get().getService().hold();
        String endGameText = ServicePlatform.get().getModel().getAncientOne().getAncientOneInfo().getEndGameText();
        GoogleServicesHolder.getAnalyticsTracker().trackInteraction(AnalyticsCategory.GAME_ENDED, "SHUB-NIGGURATH", label);
        ServicePlatform.get().getTutorialService().displayChalkboard(endGameText,
                VIEWPORT_WIDTH/2, VIEWPORT_HEIGHT/2, true);
        ServicePlatform.get().getService().addEventCommand(in -> {
            ServicePlatform.get().getGameService().restartGame() ;
        });
        ServicePlatform.get().getService().release();
    }

    private MonsterInfo findShubNiggurath() {
        for (MonsterInfo monsterInfo : ServicePlatform.get().getMonsterCup().getMonsters()) {
            if (monsterInfo.getMonsterId() != EpicMonsterId.SHUB_NIGGURATH) {
                continue;
            }
            return monsterInfo;
        }
        return null;
    }
}
