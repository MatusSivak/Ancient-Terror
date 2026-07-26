package sk.sivak.eldritchhorror.core.eventlistener.ancientone.shubniggurath;

import java8.features.stream.Stream;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import sk.sivak.eldritchhorror.core.constants.ancientone.AncientOneId;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.constants.monster.NonEpicMonsterId;
import sk.sivak.eldritchhorror.core.constants.monster.epic.EpicMonsterId;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.eventlistener.EventListenerImpl;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.ancientone.AncientOneInitListener;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.ReckoningFireType;
import sk.sivak.eldritchhorror.core.eventtype.data.SpawnMonsterData;
import sk.sivak.eldritchhorror.core.model.MythosDeckRead;

import java.util.LinkedList;
import java.util.List;

/**
 * @author msivak
 */
public class ShubNiggurathInitListener extends AncientOneInitListener {

    private static final Logger logger = LogManager.getLogger(ShubNiggurathInitListener.class);
    private ReckoningListener reckoningListener;
    private AfterAdvanceDoomListener afterAdvanceDoomListener;

    @Override
    public void register() {
        ServicePlatform.get().getService().hold();
        ServicePlatform.get().getGameService().showAncientOneCard();
        ServicePlatform.get().getMythosDeck().initSelectedMythosCards(
                new MythosDeckRead.MythosStageImpl(1,2,1),
                new MythosDeckRead.MythosStageImpl(3,2,1),
                new MythosDeckRead.MythosStageImpl(2,4,0));
        ServicePlatform.get().getService().release();

        reckoningListener = new ReckoningListener();
        afterAdvanceDoomListener = new AfterAdvanceDoomListener();

        ServicePlatform.get().getEventQueue().addDirectEventListener(reckoningListener, DirectEvent.RECKONING_ANCIENT_ONE);
        ServicePlatform.get().getEventQueue().addAfterEventListener(afterAdvanceDoomListener, BeforeAfterEvent.ADVANCE_DOOM);
    }

    @Override
    public void load() {
        reckoningListener = new ReckoningListener();
        afterAdvanceDoomListener = new AfterAdvanceDoomListener();

        ServicePlatform.get().getEventQueue().addDirectEventListener(reckoningListener, DirectEvent.RECKONING_ANCIENT_ONE);
        ServicePlatform.get().getEventQueue().addAfterEventListener(afterAdvanceDoomListener, BeforeAfterEvent.ADVANCE_DOOM);
    }

    private class AfterAdvanceDoomListener extends EventListenerImpl<Void> {

        @Override
        public void onNotify(Void eventData) {
            if (ServicePlatform.get().getDoomTrack().getCurrentDoom() > 0) {
                return;
            }
            Question<Object> question = new Question<>();
            question.setOptions(Question.Option.okOption);
            question.setTitle("Shub-Niggurath awakens! Investigators can't be replaced.");
            question.setPortraitBeforeTitle(AncientOneId.SHUB_NIGGURATH);
            ServicePlatform.get().getGameService().ask(question).subscribe(ok -> {
                ServicePlatform.get().getService().hold();

                ServicePlatform.get().getEventQueue().unregisterListener(reckoningListener);
                ServicePlatform.get().getEventQueue().unregisterListener(afterAdvanceDoomListener);

                ServicePlatform.get().getDoomOmenService().ancientOneAwakens();
                SpawnMonsterData spawnMonsterData = new SpawnMonsterData();
                spawnMonsterData.setMonsterId(EpicMonsterId.SHUB_NIGGURATH);
                spawnMonsterData.setLocationId(LocationId.THE_HEART_OF_AFRICA);
                ServicePlatform.get().getGameService().spawnMonster(spawnMonsterData);
                moveSomeMonstersHere();
                ServicePlatform.get().getService().convertTo(Void.class, () -> null);
                ServicePlatform.get().getService().release();
            });
        }

        private void moveSomeMonstersHere() {
            List<MonsterInfo> monsters = ServicePlatform.get().getMonsterCup().getMonsters();
            List<MonsterInfo> filteredMonsters = Stream.collectToList(monsters,
                    monster -> monster.getMonsterId() == NonEpicMonsterId.GHOUL ||
                            monster.getMonsterId() == NonEpicMonsterId.GOAT_SPAWN ||
                            monster.getMonsterId() == NonEpicMonsterId.DARK_YOUNG);
            filteredMonsters = Stream.collectToList(filteredMonsters,
                    monster -> monster.getCurrentLocation() != LocationId.THE_HEART_OF_AFRICA);

            if (filteredMonsters.isEmpty()) {
                return;
            }

            LinkedList<MonsterInfo> lambdaInput = new LinkedList<>(filteredMonsters);
            Question<Object> question = new Question<>();
            question.setOptions(Question.Option.okOption);
            question.setTitle("Shub-Niggurath calls for her children!");
            question.setPortraitBeforeTitle(AncientOneId.SHUB_NIGGURATH);
            ServicePlatform.get().getGameService().ask(question).subscribe(x -> {
                for (MonsterInfo monster : lambdaInput) {
                    ServicePlatform.get().getService().hold();
                    ServicePlatform.get().getService().moveCameraToLocation(monster.getCurrentLocation());
                    ServicePlatform.get().getMonsterService().moveMonster(monster, LocationId.THE_HEART_OF_AFRICA);
                    ServicePlatform.get().getService().release();
                }
            });

        }

        @Override
        public Class<Void> getDataClass() {
            return Void.class;
        }
    }

    private class ReckoningListener extends EventListenerImpl<ReckoningFireType> {

        @Override
        public void onNotify(ReckoningFireType eventData) {
            if (eventData == ReckoningFireType.CHARGE) {
                return;
            }

            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getDoomOmenService().onTriggeredReckoning();
            Question<Object> question = new Question<>();
            question.setOptions(Question.Option.okOption);
            question.setTitle("Monster is spawned on a random space.");
            question.setPortraitBeforeTitle(AncientOneId.SHUB_NIGGURATH);
            ServicePlatform.get().getGameService().ask(question).subscribe(ok -> {
                ServicePlatform.get().getService().hold();
                SpawnMonsterData spawnMonsterData = new SpawnMonsterData();
                spawnMonsterData.setLocationId(LocationId.getRandomLocations(1).get(0));
                ServicePlatform.get().getGameService().spawnMonster(spawnMonsterData);
                checkIfThereAre10orMoreMonsters();
                ServicePlatform.get().getService().release();
            });
            ServicePlatform.get().getService().release();
        }

        private void checkIfThereAre10orMoreMonsters() {
            int monstersCount = ServicePlatform.get().getMonsterCup().getMonsters().size();
            if (monstersCount < 10) {
                return;
            }
            Question<Object> question = new Question<>();
            question.setOptions(Question.Option.okOption);
            question.setTitle(monstersCount + " Monsters alive. Doom advances twice!");
            question.setPortraitBeforeTitle(AncientOneId.SHUB_NIGGURATH);
            ServicePlatform.get().getGameService().ask(question).subscribe(ok -> {
                ServicePlatform.get().getDoomOmenService().advanceDoom(2);
            });
        }

        @Override
        public Class<ReckoningFireType> getDataClass() {
            return ReckoningFireType.class;
        }
    }
}
