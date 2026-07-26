package sk.sivak.eldritchhorror.core.action.impl.monster;

import rx.Completable;
import rx.Single;
import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.action.AbstractHookableAction;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.SelectMonsterData;

import java.util.Collections;

public class SelectSingleMonsterAction extends AbstractHookableAction<SelectMonsterData, MonsterInfo> {

    public SelectSingleMonsterAction() {
        super(BeforeAfterEvent.SELECT_SINGLE_MONSTER);
    }

    @Override
    protected void onExecute(SingleSubscriber<? super MonsterInfo> ss) {
        if (input.getAvailableMonsters().size() == 0) {
            Question<Object> question = new Question<>();
            question.setTitle("There are no Monsters.");
            question.setOptions(Collections.singletonList(new Question.Option<>("OK", true)));
            ServicePlatform.get().getGameService().ask(question).subscribe(response -> {
                ServicePlatform.get().getService().convertTo(SelectMonsterData.class, () -> null);
            });
            ss.onSuccess(null);
            return;
        }
        if (input.getAvailableMonsters().size() == 1) {
            ss.onSuccess(input.getAvailableMonsters().get(0));
            return;
        }
        ServicePlatform.get().getMonsterController().selectSingleMonster(
                input.getAvailableMonsters(),
                input.getTitleText(),
                input.getHideText()).subscribe(this::onSelect);
    }

    private void onSelect(MonsterInfo monsterInfo) {
        ss.onSuccess(monsterInfo);
    }
}
