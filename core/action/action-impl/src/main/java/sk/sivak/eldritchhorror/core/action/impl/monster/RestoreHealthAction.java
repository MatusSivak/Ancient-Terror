package sk.sivak.eldritchhorror.core.action.impl.monster;

import rx.Single;
import sk.sivak.eldritchhorror.core.action.Action;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.monster.AbstractMonsterInfo;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;

public class RestoreHealthAction implements Action<Object, Void> {

    private final MonsterInfo monsterInfo;
    private final int amount;
    private final boolean highlightSpecialText;
    private final boolean highlightReckoningText;

    public RestoreHealthAction(MonsterInfo monsterInfo, int amount, boolean highlightSpecialText, boolean highlightReckoningText) {
        this.monsterInfo = monsterInfo;
        this.amount = amount;
        this.highlightSpecialText = highlightSpecialText;
        this.highlightReckoningText = highlightReckoningText;
    }

    @Override
    public Single<Void> execute() {
        return Single.create(onSub -> {
            ServicePlatform.get().getMonsterController().restoreHealth(monsterInfo, amount, highlightSpecialText, highlightReckoningText).subscribe(() -> {
                int newCurrentHealth = Math.min(monsterInfo.getCurrentHealth() + amount, monsterInfo.getToughness());
                ((AbstractMonsterInfo) monsterInfo).setCurrentHealth(newCurrentHealth);
                onSub.onSuccess(null);
            });
        });
    }

    @Override
    public void setInput(Object input) {

    }
}
