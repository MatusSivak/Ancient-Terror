package sk.sivak.eldritchhorror.core.action.impl.monster;

import rx.Single;
import sk.sivak.eldritchhorror.core.action.Action;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;

import java.util.concurrent.TimeUnit;

public class HighlightSpecialTextAction implements Action<Object, Void> {

    private final MonsterInfo monsterInfo;

    public HighlightSpecialTextAction(MonsterInfo monsterInfo) {
        this.monsterInfo = monsterInfo;
    }

    @Override
    public Single<Void> execute() {
        return Single.create(onSub -> {
            ServicePlatform.get().getMonsterController().highlightSpecialText(monsterInfo)
                    .subscribe(() -> {
                onSub.onSuccess(null);
            });
        });
    }

    @Override
    public void setInput(Object input) {

    }
}
