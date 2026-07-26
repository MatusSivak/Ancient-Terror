package sk.sivak.eldritchhorror.core.action.impl.monster;

import rx.Single;
import sk.sivak.eldritchhorror.core.action.Action;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;

public class HighlightSpawnTextAction implements Action<Object, Void> {

    private final MonsterInfo monsterInfo;

    public HighlightSpawnTextAction(MonsterInfo monsterInfo) {
        this.monsterInfo = monsterInfo;
    }

    @Override
    public Single<Void> execute() {
        return Single.create(onSub -> {
            ServicePlatform.get().getMonsterController().highlightSpawnText(monsterInfo).subscribe(() -> {
                onSub.onSuccess(null);
            });
        });
    }

    @Override
    public void setInput(Object input) {

    }
}
