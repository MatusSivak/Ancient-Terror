package sk.sivak.eldritchhorror.core.model.util;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import sk.sivak.eldritchhorror.core.constants.monster.*;
import sk.sivak.eldritchhorror.core.constants.monster.epic.CthulhuMonster;
import sk.sivak.eldritchhorror.core.constants.monster.epic.CthyllaMonster;
import sk.sivak.eldritchhorror.core.constants.monster.epic.DoppelgangerMonster;
import sk.sivak.eldritchhorror.core.constants.monster.epic.DunwichHorrorMonster;
import sk.sivak.eldritchhorror.core.constants.monster.epic.HydraMonster;
import sk.sivak.eldritchhorror.core.constants.monster.epic.NugMonster;
import sk.sivak.eldritchhorror.core.constants.monster.epic.ShubNiggurathMonster;
import sk.sivak.eldritchhorror.core.constants.monster.epic.TickTockMenMonster;
import sk.sivak.eldritchhorror.core.constants.monster.epic.TulzschaMonster;
import sk.sivak.eldritchhorror.core.constants.monster.epic.WindWalkerMonster;
import sk.sivak.eldritchhorror.core.constants.monster.epic.YebMonster;
import sk.sivak.eldritchhorror.core.constants.monster.epic.ZombieHordeMonster;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author msivak
 */
public class MonsterCupHelper {

    private static final Logger logger = LogManager.getLogger(MonsterCupHelper.class);

    public List<AbstractMonsterInfo> createMonsterCup() {
        LinkedList<AbstractMonsterInfo> monsterCup = new LinkedList<>();
        for (NonEpicMonsterId nonEpicMonsterId : NonEpicMonsterId.values()) {
            try {
                AbstractMonsterInfo monsterInfo = (AbstractMonsterInfo) Class.forName(nonEpicMonsterId.getMonsterClassName()).newInstance();
                monsterInfo.setMonsterId(nonEpicMonsterId);
                monsterCup.add(monsterInfo);
            } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        for (int i = 0; i < 6; i++) {
            CultistMonster cultistMonster = new CultistMonster();
            cultistMonster.setMonsterId(NonEpicMonsterId.CULTIST);
            monsterCup.add(cultistMonster);
        }
        GhoulMonster ghoulMonster = new GhoulMonster();
        ghoulMonster.setMonsterId(NonEpicMonsterId.GHOUL);
        monsterCup.add(ghoulMonster);

        GoatSpawnMonster goatSpawnMonster = new GoatSpawnMonster();
        goatSpawnMonster.setMonsterId(NonEpicMonsterId.GOAT_SPAWN);
        monsterCup.add(goatSpawnMonster);

        Collections.shuffle(monsterCup);
        logger.info("Monster cup created with " + monsterCup.size() + " monsters.");
        return monsterCup;
    }

    public List<AbstractMonsterInfo> createEpicMonsterCup() {
        LinkedList<AbstractMonsterInfo> epicMonsterCup = new LinkedList<>();
        epicMonsterCup.add(new TulzschaMonster());
        epicMonsterCup.add(new HydraMonster());
        epicMonsterCup.add(new DoppelgangerMonster());
        epicMonsterCup.add(new ZombieHordeMonster());
        epicMonsterCup.add(new TickTockMenMonster());
        epicMonsterCup.add(new DunwichHorrorMonster());
        epicMonsterCup.add(new WindWalkerMonster());
        epicMonsterCup.add(new CthyllaMonster());
        epicMonsterCup.add(new CthulhuMonster());
        epicMonsterCup.add(new YebMonster());
        epicMonsterCup.add(new NugMonster());
        epicMonsterCup.add(new ShubNiggurathMonster());

        return epicMonsterCup;
    }
}
