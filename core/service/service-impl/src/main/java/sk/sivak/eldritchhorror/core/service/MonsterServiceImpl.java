package sk.sivak.eldritchhorror.core.service;

import rx.Single;
import sk.sivak.eldritchhorror.core.action.provider.MonsterActionProvider;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterId;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.eventtype.SelectMonsterData;
import sk.sivak.eldritchhorror.core.eventtype.data.combat.CombatData;
import sk.sivak.eldritchhorror.core.eventtype.data.monster.DealDamageToMonsterData;
import sk.sivak.eldritchhorror.core.eventtype.data.monster.DefeatMonsterData;
import sk.sivak.eldritchhorror.core.eventtype.data.monster.MoveMonsterData;
import sk.sivak.eldritchhorror.core.service.command.ActionCommand;

public class MonsterServiceImpl extends AbstractCommonService implements MonsterService {
    private MonsterActionProvider monsterActionProvider;

    private TestService testService;
    private Service service;

    public void setService(Service service) {
        this.service = service;
    }

    public void setMonsterActionProvider(MonsterActionProvider monsterActionProvider) {
        this.monsterActionProvider = monsterActionProvider;
    }

    public void setTestService(TestService testService) {
        this.testService = testService;
    }

    @Override
    public void dealDamageToMonster(MonsterInfo monsterInfo, int amount) {
        if (amount <= 0) {
            return;
        }
        DealDamageToMonsterData data = new DealDamageToMonsterData(monsterInfo, amount);
        data.setDisplayOrHideMonsterCard(true);
        executeHookable(data, monsterActionProvider.dealDamageToMonster());
    }

    @Override
    public void dealDamageToMonsterInCombat(MonsterInfo monsterInfo, int amount, boolean displayOrHideMonsterCard) {
        if (amount <= 0) {
            return;
        }
        DealDamageToMonsterData data = new DealDamageToMonsterData(monsterInfo, amount);
        data.setInCombat();
        data.setDisplayOrHideMonsterCard(displayOrHideMonsterCard);
        executeHookable(data, monsterActionProvider.dealDamageToMonster());
    }

    @Override
    public void defeatMonster(MonsterInfo monsterInfo, boolean inCombat, boolean destroyCard) {
        DefeatMonsterData data = new DefeatMonsterData(monsterInfo, inCombat);
        data.setDestroyCard(destroyCard);
        executeHookable(data, monsterActionProvider.defeatMonster());
    }

    @Override
    public void discardMonster(MonsterInfo monsterInfo) {
        executeHookable(monsterInfo, monsterActionProvider.discardMonster());
    }

    @Override
    public Single<MonsterInfo> selectSingleMonster(SelectMonsterData selectMonsterData) {
        return Single.create(onSub -> {
            hold();
            service.hideBackground();
            executeHookable(selectMonsterData, monsterActionProvider.selectSingleMonster(), onSub);
            release();
        });
    }

    @Override
    public void highlightSpecialText(MonsterInfo monsterInfo) {
        execute(new ActionCommand<>(monsterActionProvider.highlightSpecialText(monsterInfo), "HIGHLIGHT_SPECIAL_TEXT"));
    }

    @Override
    public void highlightReckoningText(MonsterInfo monsterInfo) {
        execute(new ActionCommand<>(monsterActionProvider.highlightReckoningText(monsterInfo), "HIGHLIGHT_RECKONING_TEXT"));
    }

    @Override
    public void highlightSpawnText(MonsterInfo monsterInfo) {
        execute(new ActionCommand<>(monsterActionProvider.highlightSpawnText(monsterInfo), "HIGHLIGHT_SPAWN_TEXT"));
    }

    /**
     * @param monsterId
     * @return true if defeated
     */
    @Override
    public Single<CombatData> ambush(MonsterId monsterId) {
        return Single.create(onSub -> {
           hold();
            execute(new ActionCommand<>(monsterActionProvider.findAmbushingMonster(monsterId), "FIND_AMBUSHING_MONSTER"));

            this.<MonsterInfo>addEventCommand(monsterInfo -> {
                testService.combat(monsterInfo);
            });
            this.<CombatData>addEventCommand(combatData -> {
                onSub.onSuccess(combatData);
                execute(new ActionCommand<>(monsterActionProvider.returnAmbushingMonster(combatData.getMonsterInfo()), "RETURN_AMBUSHING_MONSTER"));
            });
           release();
        });
    }

    @Override
    public void moveMonster(MonsterInfo monsterInfo, LocationId targetLocation) {
        executeHookable(new MoveMonsterData(monsterInfo, targetLocation), monsterActionProvider.moveMonster());
    }

    @Override
    public void restoreHealth(MonsterInfo monsterInfo, int amount, boolean highlightSpecialText, boolean highlightReckoningText) {
        execute(new ActionCommand<>(
                monsterActionProvider.restoreHealth(monsterInfo, amount, highlightSpecialText, highlightReckoningText), "RESTORE_HEALTH"));
    }
}
