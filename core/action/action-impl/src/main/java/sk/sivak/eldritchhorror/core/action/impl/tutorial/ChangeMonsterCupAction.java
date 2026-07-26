package sk.sivak.eldritchhorror.core.action.impl.tutorial;

import java8.features.stream.Stream;
import rx.Single;
import sk.sivak.eldritchhorror.core.action.Action;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.encounter.EncounterType;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.monster.AbstractMonsterInfo;
import sk.sivak.eldritchhorror.core.constants.monster.CthonianMonster;
import sk.sivak.eldritchhorror.core.constants.monster.GhoulMonster;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.constants.monster.NonEpicMonsterId;
import sk.sivak.eldritchhorror.core.constants.monster.ZombieMonster;
import sk.sivak.eldritchhorror.core.eventlistener.EventListenerImpl;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestData;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import static sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent.DRAW_MYTHOS_CARD;

public class ChangeMonsterCupAction implements Action<Object, Object> {


    @Override
    public Single<Object> execute() {
        return Single.create(onSub -> {

            AbstractMonsterInfo m1 = Stream.findFirstOrException(ServicePlatform.get().getMonsterCup().getMonstersInCup(),
                    monsterInfo -> monsterInfo.getMonsterId() == NonEpicMonsterId.GOAT_SPAWN);
            AbstractMonsterInfo m2 = Stream.findFirstOrException(ServicePlatform.get().getMonsterCup().getMonstersInCup(),
                    monsterInfo -> monsterInfo.getMonsterId() == NonEpicMonsterId.GHOUL);
            AbstractMonsterInfo m3 = Stream.findFirstOrException(ServicePlatform.get().getMonsterCup().getMonstersInCup(),
                    monsterInfo -> monsterInfo.getMonsterId() == NonEpicMonsterId.ZOMBIE);
            AbstractMonsterInfo m4 = Stream.findFirstOrException(ServicePlatform.get().getMonsterCup().getMonstersInCup(),
                    monsterInfo -> monsterInfo.getMonsterId() == NonEpicMonsterId.CTHONIAN);

            ServicePlatform.get().getMonsterCup().getMonstersInCup().remove(m1);
            ServicePlatform.get().getMonsterCup().getMonstersInCup().remove(m2);
            ServicePlatform.get().getMonsterCup().getMonstersInCup().remove(m3);
            ServicePlatform.get().getMonsterCup().getMonstersInCup().remove(m4);

            ServicePlatform.get().getMonsterCup().getMonstersInCup().add(0,m1);
            ServicePlatform.get().getMonsterCup().getMonstersInCup().add(1,m2);
            ServicePlatform.get().getMonsterCup().getMonstersInCup().add(2,m3);
            ServicePlatform.get().getMonsterCup().getMonstersInCup().add(3,m4);

            onSub.onSuccess(null);
        });
    }

    @Override
    public void setInput(Object input) {

    }
}
