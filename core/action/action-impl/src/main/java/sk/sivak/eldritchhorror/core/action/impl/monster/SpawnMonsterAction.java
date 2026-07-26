package sk.sivak.eldritchhorror.core.action.impl.monster;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import rx.Completable;
import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.action.AbstractHookableAction;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.monster.AbstractMonsterInfo;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.constants.monster.NonEpicMonsterId;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.SpawnMonsterData;

/**
 * @author msivak
 */
public class SpawnMonsterAction extends AbstractHookableAction<SpawnMonsterData, SpawnMonsterData> {

    private static final Logger logger = LogManager.getLogger(SpawnMonsterAction.class);
    private final boolean quick;

    public SpawnMonsterAction(boolean quick) {
        super(BeforeAfterEvent.SPAWN_MONSTER);
        this.quick = quick;
    }

    @Override
    protected void onExecute(SingleSubscriber<? super SpawnMonsterData> ss) {
        MonsterInfo monsterInfo;
        if (input.getMonsterId() == null) {
            monsterInfo = ServicePlatform.get().getMonsterCup()
                    .spawnMonster(input.getLocationId(), input.isOverrideSpawnLocation());
        } else {
            monsterInfo = ServicePlatform.get().getMonsterCup()
                    .spawnMonster(input.getMonsterId(), input.getLocationId(), input.isOverrideSpawnLocation());
        }

        if (monsterInfo == null) {
            Question<Object> question = new Question<>();
            question.setOptions(Question.Option.okOption);
            AbstractMonsterInfo notFoundMonster;
            try {
                notFoundMonster = (AbstractMonsterInfo) Class.forName(((NonEpicMonsterId)input.getMonsterId()).getMonsterClassName()).newInstance();
            } catch (Exception e) {
                throw new IllegalArgumentException(input.getMonsterId().toString());
            }
            question.setPortraitBeforeTitle(notFoundMonster);
            question.setTitle("There are no more " + input.getMonsterId().asString()+ "s available.");
            ServicePlatform.get().getGameController().ask(question).subscribe(ok -> {
                ss.onSuccess(input);
            });
            return;
        }
        input.setMonsterInfo(monsterInfo);

        ServicePlatform.get().getEventListenerProvider().beforeSpawnMonsterInit(monsterInfo);

        Completable monsterConfirmed;
        if (quick) {
            monsterConfirmed = Completable.complete();
            ServicePlatform.get().getGameController().confirmMonsterSpawn(monsterInfo).subscribe();
        } else {
            monsterConfirmed = ServicePlatform.get().getGameController().confirmMonsterSpawn(monsterInfo);
        }
        monsterConfirmed.subscribe(() -> {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEventListenerProvider().registerMonsterListeners(monsterInfo);
            ServicePlatform.get().getService().convertTo(SpawnMonsterData.class, () -> input);
            ServicePlatform.get().getService().release();
            ss.onSuccess(null);
        });

    }
}
