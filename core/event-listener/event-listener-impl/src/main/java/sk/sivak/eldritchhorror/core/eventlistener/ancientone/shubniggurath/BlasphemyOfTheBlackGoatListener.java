package sk.sivak.eldritchhorror.core.eventlistener.ancientone.shubniggurath;

import sk.sivak.eldritchhorror.core.constants.MysteryCardInfo;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.constants.monster.epic.EpicMonsterId;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.ancientone.AbstractMysteryListener;
import sk.sivak.eldritchhorror.core.eventtype.data.SpawnMonsterData;

public class BlasphemyOfTheBlackGoatListener extends AbstractMysteryListener {

    public BlasphemyOfTheBlackGoatListener(MysteryCardInfo mysteryCardInfo) {
        super(mysteryCardInfo);
    }

    @Override
    protected int getProgress() {
        MonsterInfo epicMonster = findYeb();
        if (epicMonster != null) {
            return epicMonster.getToughness() - epicMonster.getCurrentHealth();
        } else {
            // not present on the board means it is dead
            return ServicePlatform.get().getModel().getReferenceCard().getPlayers() + 2;
        }
    }

    @Override
    public void register() {
        ServicePlatform.get().getGameService().hold();
        SpawnMonsterData spawnMonsterData = new SpawnMonsterData();
        spawnMonsterData.setLocationId(mysteryCardInfo.getPinLocations().get(0));
        spawnMonsterData.setMonsterId(EpicMonsterId.YEB);
        ServicePlatform.get().getGameService().spawnMonster(spawnMonsterData);
        ServicePlatform.get().getGameService().showCurrentMysteryCard(true, false);
        ServicePlatform.get().getGameService().release();
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
        MonsterInfo yeb = findYeb();
        if (yeb != null) {
            ServicePlatform.get().getMonsterService().dealDamageToMonster(yeb, 2);
            ServicePlatform.get().getGameService().advanceCurrentMysteryCard(Math.min(yeb.getCurrentHealth(), 2));
        } else {
            Question<Object> question = new Question<>();
            question.setOptions(Question.Option.okOption);
            question.setTitle("Can't advance active mystery.");
            ServicePlatform.get().getGameService().ask(question).subscribe();
        }
        ServicePlatform.get().getService().release();
    }

    private MonsterInfo findYeb() {
        for (MonsterInfo monsterInfo : ServicePlatform.get().getMonsterCup().getMonsters()) {
            if (monsterInfo.getMonsterId() != EpicMonsterId.YEB) {
                continue;
            }
            return monsterInfo;
        }
        return null;
    }
}
