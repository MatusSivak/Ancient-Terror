package sk.sivak.eldritchhorror.core.eventlistener.ancientone.shubniggurath;

import sk.sivak.eldritchhorror.core.constants.MysteryCardInfo;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.constants.monster.epic.EpicMonsterId;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.ancientone.AbstractMysteryListener;
import sk.sivak.eldritchhorror.core.eventtype.data.SpawnMonsterData;

public class BattleInTheWoodsListener extends AbstractMysteryListener {

    public BattleInTheWoodsListener(MysteryCardInfo mysteryCardInfo) {
        super(mysteryCardInfo);
    }

    @Override
    protected int getProgress() {
        MonsterInfo epicMonster = findShubNiggurath();
        if (epicMonster != null) {
            return epicMonster.getToughness() - epicMonster.getCurrentHealth();
        } else {
            // not present on the board means it is dead
            return ServicePlatform.get().getModel().getReferenceCard().getPlayers() + 3;
        }
    }

    @Override
    public void register() {
        ServicePlatform.get().getGameService().showCurrentMysteryCard(true, false);
    }

    @Override
    public void justAddRedPins() {

    }

    @Override
    public void justRegisterListeners(int progress) {
    }

    @Override
    public void unregister() {

    }

    @Override
    public void advanceActiveMystery() {
        ServicePlatform.get().getService().hold();
        MonsterInfo shubNiggurath = findShubNiggurath();
        if (shubNiggurath != null) {
            ServicePlatform.get().getMonsterService().dealDamageToMonster(shubNiggurath, 2);
            ServicePlatform.get().getGameService().advanceCurrentMysteryCard(Math.min(shubNiggurath.getCurrentHealth(), 3));
        } else {
            Question<Object> question = new Question<>();
            question.setOptions(Question.Option.okOption);
            question.setTitle("Can't advance active mystery.");
            ServicePlatform.get().getGameService().ask(question).subscribe();
        }
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
